package com.hardware_today.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hardware_today.dto.AuthResponse;
import com.hardware_today.model.UserModel;
import com.hardware_today.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> validateUser(@RequestBody UserModel user) {
        try {
            return ResponseEntity.ok(userService.validateUser(user));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse("Login failed! Try again..."));
        }
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
    	try {
            return ResponseEntity.ok(userService.refreshAccessToken(refreshToken));
    	} catch (Exception e) {
    		e.printStackTrace();
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse(e.getMessage()));
    	}
    }

    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody UserModel user) {
        try {
            return userService.saveUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registration failed!");
        }
    }
}
