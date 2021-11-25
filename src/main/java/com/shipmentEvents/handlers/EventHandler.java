package com.shipmentEvents.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.shipmentEvents.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class EventHandler implements RequestHandler<ScheduledEvent, String> {

    /**
     * Shipment events for a carrier are uploaded to separate S3 buckets based on the source of events. E.g., events originating from
     * the hand-held scanner are stored in a separate bucket than the ones from mobile App. The Lambda processes events from multiple
     * sources and updates the latest status of the package in a summary S3 bucket every 15 minutes.
     * 
     * The events are stored in following format:
     * - Each status update is a file, where the name of the file is tracking number + random id.
     * - Each file has status and time-stamp as the first 2 lines respectively.
     * - The time at which the file is stored in S3 is not an indication of the time-stamp of the event.
     * - Once the status is marked as DELIVERED, we can stop tracking the package.
     * 
     * A Sample files looks as below:
     *  FILE-NAME-> '8787323232232332--55322798-dd29-4a04-97f4-93e18feed554'
     *   >status:IN TRANSIT
     *   >timestamp: 1573410202
     *   >Other fields like...tracking history and address
     */
    public String handleRequest(ScheduledEvent scheduledEvent, Context context) {

        final LambdaLogger logger = context.getLogger();
        try {
            processShipmentUpdates(logger);
            return "SUCCESS";
        } catch (final Exception ex) {
            logger.log(String.format("Failed to process shipment Updates in %s due to %s", scheduledEvent.getAccount(), ex.getMessage()));
            throw new RuntimeException(ex);
        }
    }


    private void processShipmentUpdates(final LambdaLogger logger) throws InterruptedException {

        final List<String> bucketsToProcess = Constants.BUCKETS_TO_PROCESS;
        final Map<String, Pair<Long, String>> latestStatusForTrackingNumber = new HashMap<>();
        final Map<String, List<ObjectIdentifier>> filesToDelete = new HashMap<>();
        for (final String bucketName : bucketsToProcess) {
            final List<ObjectIdentifier> filesProcessed = processEventsInBucket(bucketName, logger, latestStatusForTrackingNumber);
            filesToDelete.put(bucketName, filesProcessed);
        }
        final S3Client s3Client = EventHandler.getS3Client();

        //Create a new file in the Constants.SUMMARY_BUCKET
        logger.log("Map of statuses -> " + latestStatusForTrackingNumber);
        String summaryUpdateName = Long.toString(System.currentTimeMillis());
        
        //EventHandler.getS3Client().putObject(Constants.SUMMARY_BUCKET, summaryUpdateName, latestStatusForTrackingNumber.toString());
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(Constants.SUMMARY_BUCKET).key(summaryUpdateName).build();
        EventHandler.getS3Client().putObject(putObjectRequest, RequestBody.fromString(latestStatusForTrackingNumber.toString()));
        
        long expirationTime = System.currentTimeMillis() + Duration.ofMinutes(1).toMillis();
        while (System.currentTimeMillis() < expirationTime) {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(Constants.SUMMARY_BUCKET).key(summaryUpdateName).build();
            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            if (StringUtils.isNotBlank(headObjectResponse.eTag())) {
                break;
            }
            logger.log("waiting for file to be created " + summaryUpdateName);
            Thread.sleep(1000);
        }
        
        // Before we delete the shipment updates make sure the summary update file exists
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(Constants.SUMMARY_BUCKET).key(summaryUpdateName).build();
        HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
        if (StringUtils.isNotBlank(headObjectResponse.eTag())) {
            deleteProcessedFiles(filesToDelete);
            logger.log("All updates successfully processed");
        } else {
            throw new RuntimeException("Failed to write summary status, will be retried in 15 minutes");
        }
        
    }

    private List<ObjectIdentifier> processEventsInBucket(String bucketName, LambdaLogger logger, Map<String, Pair<Long, String>> latestStatusForTrackingNumber) {
        final S3Client s3Client = EventHandler.getS3Client();
        logger.log("Processing Bucket: " + bucketName);

        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucketName).build();

        ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsV2Request);
        List<ObjectIdentifier> filesProcessed = new ArrayList<>();

        for (S3Object response : listObjectsResponse.contents()) {
            logger.log("Reading Object: " + response.key());

            String trackingNumber = response.key().split("--")[0];
            Pair<Long, String> lastKnownStatus = latestStatusForTrackingNumber.get(trackingNumber);

            // Check if this shipment has already been delivered, skip this file
            if (lastKnownStatus != null && "DELIVERED".equals(lastKnownStatus)) {
                continue;
            }

            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(response.key()).build();
            ResponseBytes<GetObjectResponse> getObjectResponse = s3Client.getObjectAsBytes(getObjectRequest);

            String fileContents = new String(getObjectResponse.asByteArray());
            if (!isValidFile(fileContents)) {
                logger.log(String.format("Skipping invalid file %s", response.key()));
                continue;
            }
            
            if (!fileContents.contains("\n")) {
                
            }
            String[] lines = fileContents.split("\n");
            String line1 = lines[0];
            String line2 = lines[1];

            String status = line1.split(":")[1];
            Long timeStamp = Long.parseLong(line2.split(":")[1]);


            if (null == lastKnownStatus || lastKnownStatus.getLeft() < timeStamp) {
                lastKnownStatus = new MutablePair<Long, String>(timeStamp, status);
                latestStatusForTrackingNumber.put(trackingNumber, lastKnownStatus);
            }

            //Add to list of processed files
            filesProcessed.add(ObjectIdentifier.builder().key(response.key()).build());
            logger.log("logging Contents of the file" + fileContents);
        }
        return filesProcessed;
    }
    

    private void deleteProcessedFiles(Map<String, List<ObjectIdentifier>> filesToDelete) {
      final S3Client s3Client = EventHandler.getS3Client();
      for (Entry<String, List<ObjectIdentifier>> entry : filesToDelete.entrySet()) {
          //final DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(entry.getKey()).withKeys(entry.getValue()).withQuiet(false);
          DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder().delete(Delete.builder().objects(entry.getValue()).build()).build();
          s3Client.deleteObjects(deleteObjectsRequest);
      }
    }
    
    private boolean isValidFile(String fileContents) {
        if (!fileContents.contains("\n")) {
            return false;
        }
        String[] lines = fileContents.split("\n");
        for (String l: lines) {
            if (!l.contains(":")) {
                return false;
            }
        }
        return true;
    }
    
    public static S3Client getS3Client() {
        return S3Client.builder().region(Region.US_EAST_1).build();
    }
    
    
}


