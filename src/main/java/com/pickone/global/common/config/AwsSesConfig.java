//package com.PickOne.global.common.config;
//
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
//import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AwsSesConfig {
//
//    @Value("${aws.ses.access-key}")
//    private String accessKey;
//
//    @Value("${aws.ses.secret-key}")
//    private String secretKey;
//
//    @Value("${aws.ses.region}")
//    private String region;
//
//    @Bean
//    public AmazonSimpleEmailService amazonSimpleEmailService() {
//        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
//        return AmazonSimpleEmailServiceClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withRegion(region)
//                .build();
//    }
//}