package com.boothlens.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.boothlens.dto.jwt.JwtProfile;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_EMAIL = "email";

    @Value("${jwt.secret}")
    private String secret;

    // Milliseconds from issuance until expiry
    @Value("${jwt.expiration}")
    private long jwtExpirationMillis;

    private SecretKey signingKey;

    @PostConstruct
    void initSigningKey() {
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException("jwt.secret must be configured");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public SecretKey getSigningKey() {
        return signingKey;
    }

    public String generateToken(String email, Integer userId, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_EMAIL, email);
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_ROLE, role);

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(jwtExpirationMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString()) // Set subject as userId for standard JWT practice
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiresAt))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parses the token and returns a JwtProfile. Returns null if invalid.
     */
    public JwtProfile getJwtProfile(String token) {
        Claims claims = getClaims(token);
        if (claims == null) {
            return null;
        }

        JwtProfile profile = new JwtProfile();
        profile.setEmail((String) claims.get(CLAIM_EMAIL));
        profile.setUserId(parseUserId(claims));
        
        Integer role = toInteger(claims.get(CLAIM_ROLE));
        profile.setRole(role != null ? role : 0);

        return profile;
    }

    public String extractEmail(String token) {
        JwtProfile profile = getJwtProfile(token);
        return profile != null ? profile.getEmail() : null;
    }

    public Integer extractUserId(String token) {
        JwtProfile profile = getJwtProfile(token);
        return profile != null ? profile.getUserId() : null;
    }

    public Integer extractRole(String token) {
        JwtProfile profile = getJwtProfile(token);
        return profile != null ? profile.getRole() : null;
    }

    public Date extractExpiration(String token) {
        Claims claims = getClaims(token);
        return claims != null ? claims.getExpiration() : null;
    }


    public boolean validateToken(String token, Integer userId) {
        if (!StringUtils.hasText(token) || userId == null) {
            return false;
        }
        Claims claims = getClaims(token);
        if (claims == null || isExpired(claims)) {
            return false;
        }
        
        String subject = claims.getSubject();
        return userId.toString().equals(subject);
    }

    /**
     * Internal method to extract raw Claims from the token.
     */
    private Claims getClaims(String token) {
        if (!StringUtils.hasText(token) || signingKey == null) {
            return null;
        }
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token.trim())
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null; // Token is invalid, expired, or malformed
        }
    }

    private static boolean isExpired(Claims claims) {
        Date exp = claims.getExpiration();
        if (exp == null) {
            return true;
        }
        return !exp.toInstant().isAfter(Instant.now());
    }

    private static Integer parseUserId(Claims claims) {
        Integer fromClaim = toInteger(claims.get(CLAIM_USER_ID));
        if (fromClaim != null) {
            return fromClaim;
        }
        String sub = claims.getSubject();
        if (!StringUtils.hasText(sub)) {
            return null;
        }
        try {
            return Integer.valueOf(sub.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer toInteger(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Integer i) {
            return i;
        }
        if (raw instanceof Number n) {
            return n.intValue();
        }
        return null;
    }
}
