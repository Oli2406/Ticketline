package at.ac.tuwien.sepr.groupphase.backend.security;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenizer {

    private final SecurityProperties securityProperties;
    private final SecretKey secretKey;
    private final Set<String> blockedTokens = ConcurrentHashMap.newKeySet();

    public JwtTokenizer(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.secretKey = Keys.hmacShaKeyFor(securityProperties.getJwtSecret().getBytes());
    }

    public String getAuthToken(String user, List<String> roles, String encryptedId, int points,
        String firstName, String lastName) {
        String token =
            Jwts.builder()
                .header()
                .add("typ", securityProperties.getJwtType())
                .and()
                .issuer(securityProperties.getJwtIssuer())
                .audience()
                .add(securityProperties.getJwtAudience())
                .and()
                .subject(user)
                .expiration(
                    new Date(
                        System.currentTimeMillis() + securityProperties.getJwtExpirationTime()))
                .claim("rol", roles)
                .claim("id", encryptedId)
                .claim("points", points)
                .claim("firstName", firstName)
                .claim("lastName", lastName)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        return securityProperties.getAuthTokenPrefix() + token;
    }

    public boolean validateToken(String token) {
        try {
            String strippedToken = stripTokenPrefix(token);
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(strippedToken);

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

    public String getResetToken(String email) {
        return Jwts.builder()
            .subject(email)
            .claim("purpose", "reset_password")
            .expiration(
                new Date(System.currentTimeMillis() + 2 * 60 * 1000))
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact();
    }

    private String stripTokenPrefix(String token) {
        if (token.startsWith(securityProperties.getAuthTokenPrefix())) {
            return token.substring(securityProperties.getAuthTokenPrefix().length()).trim();
        }
        return token;
    }
}
