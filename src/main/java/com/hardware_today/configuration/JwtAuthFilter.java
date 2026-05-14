package com.hardware_today.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hardware_today.utils.JwtUtil;
import jakarta.security.auth.message.callback.PrivateKeyCallback.Request;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;
	
	@Autowired
	public JwtAuthFilter (JwtUtil jwtUtil, UserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}
	
	@Override
	protected void doFilterInternal (HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException{
//		final String authHeader = req.getHeader("Authorization");
//		
//		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//			chain.doFilter(req, res);
//			return;
//		}
		
//		final String token = authHeader.substring(7);
		
		Map<String, String> tokens = Arrays.stream(req.getCookies() != null ? req.getCookies() : new Cookie[0])
			.filter(cookie -> "access_token".equals(cookie.getName()) || "refresh_token".equals(cookie.getName()))
			.collect(Collectors.toMap(Cookie::getName, Cookie::getValue));

		String refreshToken = tokens.get("refresh_token");
		String accessToken  = tokens.get("access_token");
		
		if(accessToken != null && !accessToken.isBlank()) {
			final String username = jwtUtil.extractUsername(accessToken);
			
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				
				if (jwtUtil.validateToken(accessToken, userDetails.getUsername())) {
					this.runSecurityContext(userDetails);
				} else if (jwtUtil.refreshToken(refreshToken, username, res)) this.runSecurityContext(userDetails);
			}
		}

		chain.doFilter(req, res);
	}

	private void runSecurityContext(UserDetails userDetails) {
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
			);
	}
}
