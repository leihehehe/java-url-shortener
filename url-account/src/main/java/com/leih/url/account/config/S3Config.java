package com.leih.url.account.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sns.AmazonSNSClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@ConfigurationProperties(prefix = "s3")
@Configuration
@Data
public class S3Config {
    private String bucketName;
    private String accessKey;
    private String secretKey;
    private String regions;
    private AmazonS3Client amazonS3Client;
    @PostConstruct
    public void init() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(awsCredentials);
        AmazonS3ClientBuilder clientBuilder = AmazonS3ClientBuilder.standard();
        clientBuilder.setCredentials(provider);
        clientBuilder.setRegion(regions);
        amazonS3Client = (AmazonS3Client) clientBuilder.build();
    }
}
