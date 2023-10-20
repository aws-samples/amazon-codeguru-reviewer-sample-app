package com.shipmentEvents.util;

import com.amazonaws.services.s3.AmazonS3;

public class S3Events {
    
    AmazonS3 s3Client = S3ClientUtil.getS3Client();
    
    //Create method to upload file to S3
    public void uploadFile(String bucketName, String key, String filePath) {
        s3Client.putObject(bucketName, key, filePath);
    }

    //Create method to delete a file in S3
    public void deleteFile(String bucketName, String key) {
        s3Client.deleteObject(bucketName, key);
    }

    //Create a method to list all files in a bucket
    public void listFiles(String bucketName) {
        s3Client.listObjects(bucketName);
    }

    //create method to iterate accross all the different s3 objects
    public void iterateS3Objects() {
        s3Client.listBuckets().forEach(bucket -> {
            bucket.getName();
            bucket.getCreationDate();
            bucket.getOwner();
            bucket.getOwner().getDisplayName();
        }
        );
    }
}
