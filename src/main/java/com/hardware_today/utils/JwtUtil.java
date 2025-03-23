package com.hardware_today.utils;

import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hardware_today.configuration.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	private final JwtConfig jwtConfig; 
	private static final long access_exp_time = 1000 * 60 * 15;
	private static final long refresh_exp_time = 1000 * 60 * 60 * 24 * 7;
	
	@Autowired
	public JwtUtil(JwtConfig config) {
		this.jwtConfig = config;
	}
	
	public String generateAccessToken(String subject, String email) {
		return this.createToken(subject, email, access_exp_time);
	}
	
	public String generateRefreshToken(String subject, String email) {
		return this.createToken(subject, email, refresh_exp_time);
	}
	
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(token).getBody();
    }

    private String createToken(String subject, String issuerEmail, long expTime) {
        return Jwts.builder()
        		.setSubject(subject.toString())
        		.setIssuer(issuerEmail)
        		.setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expTime))
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret()).compact();
    }

    public Boolean validateToken(String token, String userName) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(userName) && !this.isTokenExpired(token);
    }
    
    public Boolean isTokenExpired(String token) {
    	return extractExpiration(token).before(new Date());
    }
}
