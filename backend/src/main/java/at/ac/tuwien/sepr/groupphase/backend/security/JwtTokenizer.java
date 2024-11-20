package at.ac.tuwien.sepr.groupphase.backend.security;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtTokenizer {

    private final SecurityProperties securityProperties;
    private final SecretKey secretKey;
    private final Set<String> blockedTokens = ConcurrentHashMap.newKeySet();

    public JwtTokenizer(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.secretKey = Keys.hmacShaKeyFor(securityProperties.getJwtSecret().getBytes());
    }

    public String getAuthToken(String user, List<String> roles) {
        String token = Jwts.builder()
            .header().add("typ", securityProperties.getJwtType()).and()
            .issuer(securityProperties.getJwtIssuer())
            .audience().add(securityProperties.getJwtAudience()).and()
            .subject(user)
            .expiration(new Date(System.currentTimeMillis() + securityProperties.getJwtExpirationTime()))
            .claim("rol", roles)
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact();

        return securityProperties.getAuthTokenPrefix() + token;
    }

    public boolean validateToken(String token) {
        try {
            String strippedToken = stripTokenPrefix(token);
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(strippedToken);

            return !isTokenBlocked(strippedToken);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public void blockToken(String token) {
        String strippedToken = stripTokenPrefix(token);
        blockedTokens.add(strippedToken);
    }

    public boolean isTokenBlocked(String token) {
        String strippedToken = stripTokenPrefix(token);
        return blockedTokens.contains(strippedToken);
    }

    public Claims getClaims(String token) {
        String strippedToken = stripTokenPrefix(token);
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(strippedToken)
            .getPayload();
    }

    private String stripTokenPrefix(String token) {
        if (token.startsWith(securityProperties.getAuthTokenPrefix())) {
            return token.substring(securityProperties.getAuthTokenPrefix().length()).trim();
        }
        return token;
    }
}
