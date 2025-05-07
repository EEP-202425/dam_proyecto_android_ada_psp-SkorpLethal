package com.transportes.coches.utils;

import java.util.Date;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

	private final String SECRET = "mi-super-clave-secreta-muy-larga-y-segura-para-firmar-jwts-123456"; // al menos 256 bits

	private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

	    public String generarToken(String username) {
	        return Jwts.builder()
	                .setSubject(username)
	                .setIssuedAt(new Date())
	                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10h
	                .signWith(key)
	                .compact();
	    }

	    public String extraerNombreUsuario(String token) {
	        return Jwts.parserBuilder()
	                .setSigningKey(key)
	                .build()
	                .parseClaimsJws(token)
	                .getBody()
	                .getSubject();
	    }

	    public boolean validarToken(String token, String expectedUsername) {
	        try {
	            String username = extraerNombreUsuario(token);
	            return username.equals(expectedUsername);
	        } catch (JwtException e) {
	            return false;
	        }
	    }
	    
	    public boolean validarTokenExpirado(String token) {
	        try {
	            Date expiration = Jwts.parserBuilder()
	                    .setSigningKey(key)
	                    .build()
	                    .parseClaimsJws(token)
	                    .getBody()
	                    .getExpiration();

	            return expiration.before(new Date());
	        } catch (JwtException e) {
	            return true; // también lo tratamos como expirado si falla parsing
	        }
	    }
	}