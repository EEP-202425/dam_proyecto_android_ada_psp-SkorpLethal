package com.transportes.coches.utils;

import java.util.Date;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

	 private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // clave aleatoria segura

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
	}
