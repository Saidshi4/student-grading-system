package com.supremecourt.studentgradingsystem.service.auth;

import com.supremecourt.studentgradingsystem.dao.entity.UserEntity;
import com.supremecourt.studentgradingsystem.exception.NotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String ACCESS_TOKEN = "access";
    private static final String REFRESH_TOKEN = "refresh";

    @Value("${spring.jwt.secret}")
    private String secret;

    @Value("${spring.jwt.expiration}")
    private long jwtExpiration;

    @Value("${spring.jwt.refresh-expiration}")
    private long refreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public String extractRolesFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token");
        }
        Claims claims = extractAllClaims(token.replace("Bearer ", ""));
        return claims.get("role", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public Long extractUserIdFromAccessToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Invalid token");
            }
            Claims claims = extractAllClaims(token.replace("Bearer ", ""));
            return claims.get("userId", Long.class);
        } catch (NotFoundException e) {
            return null;
        }
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(CLAIM_TOKEN_TYPE, ACCESS_TOKEN);
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(CLAIM_TOKEN_TYPE, REFRESH_TOKEN);
        return buildToken(extraClaims, userDetails, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationMs) {
        UserEntity userEntity = (UserEntity) userDetails;
        extraClaims.put("userId", userEntity.getId());
        extraClaims.put("role", userEntity.getAuthorities().iterator().next().getAuthority());
        extraClaims.put("tokenVersion", userEntity.getTokenVersion());

        var builder = Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs));

        extraClaims.forEach(builder::claim);

        return builder
                .signWith(getSignInKey())
                .compact();
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && isAccessToken(token)
                && isTokenVersionValid(token, userDetails);
    }

    public boolean isAccessToken(String token) {
        return ACCESS_TOKEN.equals(extractTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return REFRESH_TOKEN.equals(extractTokenType(token));
    }

    public boolean isTokenVersionValid(String token, UserDetails userDetails) {
        if (!(userDetails instanceof UserEntity userEntity)) {
            return false;
        }
        Integer tokenVersion = extractTokenVersion(token);
        return tokenVersion != null && tokenVersion.equals(userEntity.getTokenVersion());
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_TOKEN_TYPE, String.class));
    }

    public Integer extractTokenVersion(String token) {
        return extractClaim(token, claims -> {
            Object value = claims.get("tokenVersion");
            if (value instanceof Number number) {
                return number.intValue();
            }
            return null;
        });
    }

    public long getRefreshExpirationMs() {
        return refreshExpiration;
    }

    public long getRemainingTtlMinutes(String token) {
        Date expiration = extractExpiration(token);
        long minutes = Duration.between(Instant.now(), expiration.toInstant()).toMinutes();
        return Math.max(1, minutes);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public List<SimpleGrantedAuthority> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object rolesObj = claims.get("role");
        if (rolesObj instanceof String role && !role.isBlank()) {
            return List.of(new SimpleGrantedAuthority(role));
        }
        if (rolesObj instanceof List<?> rolesList) {
            return rolesList.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private SecretKey getSignInKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (RuntimeException ex) {
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }
}
