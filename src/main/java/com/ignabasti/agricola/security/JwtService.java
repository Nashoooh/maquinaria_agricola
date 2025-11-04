package com.ignabasti.agricola.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora

    public String generateToken(String username) {
      return Jwts.builder()
        .subject(username) // Nuevo método en lugar de setSubject()
        .issuedAt(new Date()) // Nuevo método en lugar de setIssuedAt()
        .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(getSignInKey()) // Ya no se necesita pasar el algoritmo aquí
        .compact();
    }
    
    public Claims isTokenValid(String jwtToken) {
      return Jwts.parser()
      .verifyWith((SecretKey)getSignInKey())
      .build()
      .parseSignedClaims(jwtToken)
      .getPayload();
    }
    
    private Key getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        Claims claims = this.isTokenValid(token);
        return claims.getSubject();
    }
}