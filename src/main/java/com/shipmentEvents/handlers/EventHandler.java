package com.shipmentEvents.handlers;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.shipmentEvents.util.Constants;
import com.shopify.ShopifySdk;
import com.shopify.model.ShopifyShop;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;


public class EventHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {       
        //create tracking mechanism of errors:
        Boolean NoError = true;

        final LambdaLogger logger = context.getLogger();

        try {
            //Now depends on the value of the API we will do one thing or others, let's get the API values:
            String BodyOfAPI = input.getBody();

            //extract the values:
            JSONObject obj = new JSONObject(BodyOfAPI);

            //get the number of times repeat this exercise
            int numberofloops = Integer.parseInt(obj.getString("Number of loops"));
            
            for (int i = 0; i < numberofloops; i++) {
                processShipmentUpdates(logger);    
            }

        } catch (final Exception ex) {
            
            //get the account id:
            String accountid = context.getInvokedFunctionArn().split(":")[4];

            //pass the error to the logger
            logger.log(String.format("Failed to process shipment Updates in %s due to %s",accountid, ex.getMessage()));

            //pass the error:
            NoError=false;
        }

        //return at the end:
        return CreateAPIGatewayMessage(NoError);
    }

    private APIGatewayProxyResponseEvent CreateAPIGatewayMessage(Boolean NoError)
    {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                 .withHeaders(headers);
        
           if(NoError)
           {
                String output = "{ \"message\": \"Action Completed\"}";

                return response
                    .withStatusCode(200)
                    .withBody(output);

           } 
           else
           {
                return response
                    .withBody("{}")
                    .withStatusCode(500);
           }
    }

    public String weakMessageEncryption(String message, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return new String(cipher.doFinal(message.getBytes()), StandardCharsets.UTF_8);
    }

    public ShopifyShop connectToShopify(String subdomain) {
        final String token = "shpss_sdkfhkjh134134141341344133412312345678";
        final ShopifySdk shopifySdk = ShopifySdk.newBuilder()
             .withSubdomain(subdomain)
             .withAccessToken(token).build();
        return shopifySdk.getShop();
    }

    private void processShipmentUpdates(final LambdaLogger logger) throws InterruptedException {

        final List<String> bucketsToProcess = Constants.BUCKETS_TO_PROCESS;
        final Map<String, Pair<Long, String>> latestStatusForTrackingNumber = new HashMap<String, Pair<Long, String>>();
        final Map<String, List<KeyVersion>> filesToDelete = new HashMap<String, List<DeleteObjectsRequest.KeyVersion>>(); 
        for (final String bucketName : bucketsToProcess) {
            final List<KeyVersion> filesProcessed = processEventsInBucket(bucketName, logger, latestStatusForTrackingNumber);
            filesToDelete.put(bucketName, filesProcessed);
        }
        final AmazonS3 s3Client = EventHandler.getS3Client();

        //Create a new file in the Constants.SUMMARY_BUCKET
        logger.log("Map of statuses -> " + latestStatusForTrackingNumber);
        String summaryUpdateName = Long.toString(System.currentTimeMillis());
        
        EventHandler.getS3Client().putObject(Constants.SUMMARY_BUCKET, summaryUpdateName, latestStatusForTrackingNumber.toString());
        
        long expirationTime = System.currentTimeMillis() + Duration.ofMinutes(1).toMillis();
        while(System.currentTimeMillis() < expirationTime) {
            if (s3Client.doesObjectExist(Constants.SUMMARY_BUCKET, summaryUpdateName)) {
                break;
            }
            logger.log("waiting for file to be created " + summaryUpdateName);
            Thread.sleep(1000);
        }
        
        // Before we delete the shipment updates make sure the summary update file exists
        if (EventHandler.getS3Client().doesObjectExist(Constants.SUMMARY_BUCKET, summaryUpdateName)) {
            deleteProcessedFiles(filesToDelete);
            logger.log("All updates successfully processed");
        } else {
            throw new RuntimeException("Failed to write summary status, will be retried in 15 minutes");
        }
        
    }

    private List<KeyVersion> processEventsInBucket(String bucketName, LambdaLogger logger, Map<String, Pair<Long, String>> latestStatusForTrackingNumber) {
        final AmazonS3 s3Client = EventHandler.getS3Client();
        logger.log("Processing Bucket: " + bucketName);

        ObjectListing files = s3Client.listObjects(bucketName);
        List<KeyVersion> filesProcessed = new ArrayList<DeleteObjectsRequest.KeyVersion>();

        for (Iterator<?> iterator = files.getObjectSummaries().iterator(); iterator.hasNext(); ) {
            S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
            logger.log("Reading Object: " + summary.getKey());

            String trackingNumber = summary.getKey().split("--")[0];
            Pair<Long, String> lastKnownStatus = latestStatusForTrackingNumber.get(trackingNumber);

            // Check if this shipment has already been delivered, skip this file
            if (lastKnownStatus != null && "DELIVERED".equals(lastKnownStatus.getRight())) {
                continue;
            }

            String fileContents = s3Client.getObjectAsString(bucketName, summary.getKey());

            if (!isValidFile(fileContents)) {
                logger.log(String.format("Skipping invalid file %s", summary.getKey()));
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
            filesProcessed.add(new KeyVersion(summary.getKey()));
            logger.log("logging Contents of the file" + fileContents);
        }
        return filesProcessed;
    }
    

    private void deleteProcessedFiles(Map<String, List<KeyVersion>> filesToDelete) {
      final AmazonS3 s3Client = EventHandler.getS3Client();
      for (Entry<String, List<KeyVersion>> entry : filesToDelete.entrySet()) {
          final DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(entry.getKey()).withKeys(entry.getValue()).withQuiet(false);
          s3Client.deleteObjects(deleteRequest);
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
    
    public static AmazonS3 getS3Client() {
        return AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
    }
    
    
}


