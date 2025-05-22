package com.hardware_today.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hardware_today.service.AWSService;

@RestController
@RequestMapping("files")
public class AWSController {
	private final AWSService awsService;
	
	public AWSController (AWSService awsService) {
		this.awsService = awsService;
	}
	
	@GetMapping("/{fileKey}")
	public ResponseEntity<byte[]> getFile (@PathVariable String fileKey) {
		byte[] response = this.awsService.getFileContent(fileKey);
		
		return ResponseEntity.ok().body(response);
	}
}
