package at.ac.tuwien.sepr.groupphase.backend.security;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

@Service
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SecurityProperties securityProperties;

    public JwtAuthorizationFilter(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        List<String> excludedPaths = List.of(
            "/api/v1/register",
            "/api/v1/public",
            "/api/v1/news",
            "/api/v1/purchase",
            "/api/v1/authentication/send-email",
            "/api/v1/authentication/reset-password.*",
            "/api/v1/authentication/verify-reset-code"
        );

        String requestPath = request.getRequestURI();

        for (String excludedPath : excludedPaths) {
            if (requestPath.matches(excludedPath)) {
                chain.doFilter(request, response);
                return;
            }
        }

        try {
            UsernamePasswordAuthenticationToken authToken = getAuthToken(request);
            if (authToken != null) {
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (JwtException e) {
            LOGGER.warn("JWT validation failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid authorization attempt: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Malformed authorization header");
            return;
        }

        // Continue the filter chain
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthToken(HttpServletRequest request)
        throws JwtException {
        String token = request.getHeader(securityProperties.getAuthHeader());
        if (token == null || token.isEmpty()) {
            return null;
        }

        if (!token.startsWith(securityProperties.getAuthTokenPrefix())) {
            throw new IllegalArgumentException(
                "Authorization header must start with the correct prefix");
        }

        String strippedToken = token.replace(securityProperties.getAuthTokenPrefix(), "").trim();
        byte[] signingKey = securityProperties.getJwtSecret().getBytes();

        if (!token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token must start with 'Bearer'");
        }

        Claims claims =
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(signingKey))
                .build()
                .parseSignedClaims(strippedToken)
                .getPayload();

        validateTokenExpiration(claims);

        String username = claims.getSubject();
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Token does not contain a valid user");
        }

        if (!"reset_password".equals(claims.get("purpose")) && claims.get("rol") == null) {
            throw new IllegalArgumentException("Token purpose or roles are invalid");
        } else if ("reset_password".equals(claims.get("purpose"))) {
            return new UsernamePasswordAuthenticationToken(username, null, null);
        }

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("rol");
        List<SimpleGrantedAuthority> authorities =
            roles.stream().map(SimpleGrantedAuthority::new).toList();

        MDC.put("u", username);

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    private void validateTokenExpiration(Claims claims) {
        Date expiration = claims.getExpiration();
        if (expiration == null || expiration.before(new Date())) {
            throw new JwtException("Token is expired");
        }
    }
}
