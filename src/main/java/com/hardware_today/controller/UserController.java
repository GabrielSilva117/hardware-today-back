package com.hardware_today.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hardware_today.dto.AuthResponse;
import com.hardware_today.model.UserModel;
import com.hardware_today.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> checkSession() {
		return ResponseEntity.ok(true);
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> validateUser(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        try {
            return ResponseEntity.ok(userService.validateUser(credentials,response));
        } catch (Exception e) {
            log.error("Login failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed! Try again...");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
    	try {
    		return ResponseEntity.ok(userService.logoutUser(response));
    	} catch (Exception e) {
    		log.error("Logout failed", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    	}
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> createUser(@RequestBody UserModel user) {
        try {
            return userService.saveUser(user);
        } catch (Exception e) {
            log.error("User registration failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registration failed!");
        }
    }
    
    @PostMapping("/role")
    public ResponseEntity<String> createRole(@RequestBody String roleName) {
    	try {
    		return userService.createRole(roleName);
    	} catch (Exception e) {
    		log.error("Role registration failed", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Role registration failed!");
    	}
    }
}
