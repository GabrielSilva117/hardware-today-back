package com.hardware_today.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSConfig {
	
	@Bean
	public S3Client s3Client(@Value("${aws.accessKeyId}") String accessKeyId,
            @Value("${aws.secretAccessKey}") String secretAccessKey) {
		return S3Client.builder()
				.region(Region.US_EAST_2)
				.credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                ))
				.build();
	}
	
}
