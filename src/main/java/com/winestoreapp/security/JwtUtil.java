package com.winestoreapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private static final Long MILLISECONDS_IN_DAY = 8_6400_000L;
    private static final Map<String, LocalDateTime> INVALID_TOKENS = new ConcurrentHashMap<>();
    private static final String EVERY_DAY_AT_MIDNIGHT = "0 0 0 * * *";
    private Key secret;
    @Value("${jwt.expiration:1000}")
    private long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secretString) {
        secret = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public void addToInvalidTokens(String token) {
        INVALID_TOKENS.put(token, LocalDateTime.now());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secret)
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            if (INVALID_TOKENS.containsKey(token)) {
                return false;
            }
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Expired or JWT token is invalid");
        }
    }

    public String getUsername(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    @Scheduled(cron = EVERY_DAY_AT_MIDNIGHT)
    private void deleteAllExpiredTokensBySchedule() {
        int daysQuantityForDeletingExpiredTokens
                = (int) Math.ceil((double) expiration / MILLISECONDS_IN_DAY);

        LocalDateTime dayAfterWhichExpiredTokensMustBeDeleted =
                LocalDateTime.now().minusDays(daysQuantityForDeletingExpiredTokens);

        INVALID_TOKENS.entrySet().removeIf(entry
                -> entry.getValue().isAfter(dayAfterWhichExpiredTokensMustBeDeleted));
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}
