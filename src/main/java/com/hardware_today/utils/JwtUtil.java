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
	private JwtConfig jwtConfig; 
	
	@Autowired
	public JwtUtil(JwtConfig config) {
		this.jwtConfig = config;
	}
	
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(Long subject, String email) {
        return createToken(subject, email);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(token).getBody();
    }

    private String createToken(Long subject, String issuerEmail) {
    	System.out.println(jwtConfig.getSecret());
        return Jwts.builder()
        		.setSubject(subject.toString())
        		.setIssuer(issuerEmail)
        		.setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() * 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret()).compact();
    }

    public Boolean validateToken(String token, String userName) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(userName) && extractExpiration(token).before(new Date());
    }
}
