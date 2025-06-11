package com.hardware_today.utils;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieHandler {
    public static void addCookie(String value, String cookieKey, Integer cookieMaxAge, HttpServletResponse response) {
    	Cookie authCookie = new Cookie(cookieKey, value);
    	authCookie.setHttpOnly(true);
    	authCookie.setSecure(false); // --TODO add an env variable to change it to true in prd
    	authCookie.setPath("/");
    	authCookie.setMaxAge(cookieMaxAge);
    	response.addCookie(authCookie);    	
    }
    
    public static void clearCookie(String cookieKey, HttpServletResponse response) {
    	addCookie(null, cookieKey, 0, response);
    }    
}
