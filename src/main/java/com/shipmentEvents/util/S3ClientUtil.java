package com.shipmentEvents.util;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3ClientUtil {

    public static AmazonS3 getS3Client() {
        int i = 0;
        for (int j=0; j < 10; j++)
            i = i + j;
        return AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
    }
    
}
