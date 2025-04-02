package com.hardware_today.service;

import java.util.Map;

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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

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
    
    public void addHttpOnlyCookie(String token, String cookieKey, Integer cookieMaxAge, HttpServletResponse response) {
    	Cookie authCookie = new Cookie(cookieKey, token);
    	authCookie.setHttpOnly(true);
    	authCookie.setSecure(false); // --TODO add an env variable to change it to true in prd
    	authCookie.setPath("/");
    	authCookie.setMaxAge(cookieMaxAge);
    	response.addCookie(authCookie);    	
    }
    
    public void clearAuthCookie(String cookieKey, HttpServletResponse response) {
    	addHttpOnlyCookie(null, cookieKey, 0, response);
    }

    public String validateUser(Map<String, String> user, HttpServletResponse res) throws Exception {
    	authManager.authenticate(new UsernamePasswordAuthenticationToken(user.get("email"), user.get("password")));    	
    	UserDetails userDetails = userDetailsService.loadUserByUsername(user.get("email"));
//    	AuthResponse response = new AuthResponse();

    	addHttpOnlyCookie(jwtUtil.generateAccessToken(userDetails.getUsername(), userDetails.getUsername()), "access_token", 900, res);
    	addHttpOnlyCookie(jwtUtil.generateAccessToken(userDetails.getUsername(), userDetails.getUsername()), "refresh_token", 604800, res);
    	
//    	response.setAccessToken(jwtUtil.generateAccessToken(userDetails.getUsername(), userDetails.getUsername()));
//    	response.setRefreshToken(jwtUtil.generateRefreshToken(userDetails.getUsername(), userDetails.getUsername()));
    	
    	return "You're now successfully logged in";
    }

    public String refreshAccessToken(String refreshToken, HttpServletResponse response) throws Exception {
    	AuthResponse authRes = new AuthResponse();
    	
//    	if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//    		throw new BadRequestException("The provided token is invalid!");
//    	}
//    	
//    	String token = authHeader.substring(7);
    	String username = this.jwtUtil.extractUsername(refreshToken);
    		
    	if (refreshToken == null || username == null || !this.jwtUtil.validateToken(refreshToken, username)) {
    		throw new UnauthorizedException("The refresh token expired! Login to generate a new one");
    	}
    	
    	addHttpOnlyCookie(jwtUtil.generateAccessToken(username, username), "access_token", 900, response);
    	
//    	String accessToken = jwtUtil.generateAccessToken(username, username);
//    	
//    	authRes.setAccessToken(accessToken);
    	
    	return "The access token received a refresh";
    }
    
    public String logoutUser(HttpServletResponse response) {
    	clearAuthCookie("access_token", response);
    	clearAuthCookie("refresh_token", response);
    	
    	return "Logout complete!";
    }
    
    public ResponseEntity<String> createRole(String roleName) throws Exception {
    	Role role = new Role(roleName);
    	roleRepository.save(role);
    	
    	return ResponseEntity.ok("Role created successfully!");
    }
}

