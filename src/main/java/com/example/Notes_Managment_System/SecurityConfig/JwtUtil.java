package com.example.Notes_Managment_System.SecurityConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration}")
    private String Expire;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ TOKEN GENERATE (NEW API)
    public String generateToken(String email, String role) {

        long expireMillis = Long.parseLong(Expire);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireMillis);

        return Jwts.builder()
                .setSubject(email)          // ❌ subject() → ✅ setSubject()
                .claim("role", role)
                .setIssuedAt(now)           // ❌ issuedAt() → ✅ setIssuedAt()
                .setExpiration(expiryDate) // ❌ expiration() → ✅ setExpiration()
                .signWith(key)              // key already HS256
                .compact();
    }

    // ✅ COMMON METHOD – parse claims (NEW API)
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()         // ❌ parser() → ✅ parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();                 // ❌ getPayload() → ✅ getBody()
    }

    // ✅ EXTRACT EMAIL
    public String extractEmail(String token) {
        return getAllClaims(token).getSubject();
    }

    // ✅ EXTRACT ROLE
    public String extractRole(String token) {
        return getAllClaims(token).get("role", String.class);
    }
}