package com.hardware_today.service;

import org.apache.coyote.BadRequestException;
//import org.apache.;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.hardware_today.custom.controller_exceptions.UnauthorizedException;
import com.hardware_today.dto.AuthResponse;
import com.hardware_today.entity.Role;
import com.hardware_today.entity.User;
import com.hardware_today.model.UserModel;
import com.hardware_today.repository.RoleRepository;
import com.hardware_today.repository.UserRepository;

import com.hardware_today.utils.JwtUtil;

@Service
public class UserService {
    private final UserRepository userRepository;
    
    private final JwtUtil jwtUtil;
    
    private final AuthenticationManager authManager;
    
    private final UserDetailsService userDetailsService;
    
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authManager, 
    		UserDetailsService userDetailsService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
        this.userDetailsService = userDetailsService;
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<String> saveUser(UserModel user) throws Exception {
        try {
            User userEntity = new User(user);
            userRepository.save(userEntity);
            return ResponseEntity.ok("User created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public AuthResponse validateUser(UserModel user) throws Exception {
    	authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));    	
    	UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    	AuthResponse response = new AuthResponse();
    	
    	response.setAccessToken(jwtUtil.generateAccessToken(userDetails.getUsername(), userDetails.getUsername()));
    	response.setRefreshToken(jwtUtil.generateRefreshToken(userDetails.getUsername(), userDetails.getUsername()));
    	
    	return response;
    }

    public AuthResponse refreshAccessToken(String authHeader) throws Exception {
    	AuthResponse authRes = new AuthResponse();
    	
    	if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    		throw new BadRequestException("The provided token is invalid!");
    	}
    	
    	String token = authHeader.substring(7);
    	String username = this.jwtUtil.extractUsername(token);
    	
    	if (username == null || !this.jwtUtil.validateToken(token, username)) {
    		throw new UnauthorizedException("The refresh token expired! Login to generate a new one");
    	}
    	
    	String accessToken = jwtUtil.generateAccessToken(username, username);
    	
    	authRes.setAccessToken(accessToken);
    	
    	return authRes;
    }
    
    public ResponseEntity<String> createRole(String roleName) throws Exception {
    	Role role = new Role(roleName);
    	roleRepository.save(role);
    	
    	return ResponseEntity.ok("Role created successfully!");
    }
}

