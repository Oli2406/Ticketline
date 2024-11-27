package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PermitAll
@RestController
@RequestMapping("/api/v1/authentication")
public class AuthTokenEndpoint {

    private final JwtTokenizer jwtTokenizer;

    public AuthTokenEndpoint(JwtTokenizer jwtTokenizer) {
        this.jwtTokenizer = jwtTokenizer;
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                jwtTokenizer.validateToken(jwt);
                return ResponseEntity.ok(true);
            }
            return ResponseEntity.ok(false);
        } catch (JwtException e) {
            return ResponseEntity.ok(false);
        }
    }
}



