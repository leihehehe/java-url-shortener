package com.leih.url.account.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "sns")
@Configuration
@Data
@Slf4j
public class SmsConfig {
  private String accessKey;
  private String secretKey;
  private String regions;
  private AmazonSNSClient amazonSNSClient;

  @PostConstruct
  public void init() {
    AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
    AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(awsCredentials);
    AmazonSNSAsyncClientBuilder clientBuilder = AmazonSNSAsyncClientBuilder.standard();
    clientBuilder.setCredentials(provider);
    clientBuilder.setRegion(regions);
    amazonSNSClient = (AmazonSNSClient) clientBuilder.build();
  }
}
