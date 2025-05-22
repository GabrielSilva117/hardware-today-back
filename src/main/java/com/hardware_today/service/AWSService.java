package com.hardware_today.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class AWSService {
	@Value("${aws.s3.bucket}")
	private String bucketName;
	
	private final S3Client s3Client;
	
	public AWSService(S3Client s3Client) {
		this.s3Client = s3Client; 
	}
	
	private GetObjectRequest getObjectByKey(String key) {
		return GetObjectRequest.builder().bucket(this.bucketName).key(key).build();
	}
	
	public byte[] getFileContent(String key) {
		try {
			GetObjectRequest objRequest = this.getObjectByKey(key);			
			ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(objRequest);
			return responseBytes.asByteArray();
		} catch (S3Exception e) {
			throw new RuntimeException("Error fetching the file: " + e.awsErrorDetails().errorMessage(), e);
		}

	}
}
