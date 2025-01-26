package at.ac.tuwien.sepr.groupphase.backend.unittests.endpoint;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.AuthTokenEndpoint;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class AuthTokenEndpointTest {

    @Mock
    private JwtTokenizer jwtTokenizer;

    @InjectMocks
    private AuthTokenEndpoint authTokenEndpoint;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateToken_Success() {
        String token = "Bearer validToken";

        doAnswer(invocation -> null).when(jwtTokenizer).validateToken("validToken");

        ResponseEntity<Boolean> response = authTokenEndpoint.validateToken(token);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());
        verify(jwtTokenizer, times(1)).validateToken("validToken");
    }

    @Test
    void validateToken_InvalidToken() {
        String token = "Bearer invalidToken";

        doThrow(new JwtException("Invalid token")).when(jwtTokenizer).validateToken("invalidToken");

        ResponseEntity<Boolean> response = authTokenEndpoint.validateToken(token);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody());
        verify(jwtTokenizer, times(1)).validateToken("invalidToken");
    }

    @Test
    void validateToken_NullToken() {
        String token = null;

        ResponseEntity<Boolean> response = authTokenEndpoint.validateToken(token);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody());
        verify(jwtTokenizer, never()).validateToken(anyString());
    }

    @Test
    void validateResetToken_Success() {
        String token = "Bearer validResetToken";
        Claims claims = mock(Claims.class);

        when(claims.get("purpose")).thenReturn("reset_password");
        when(jwtTokenizer.getClaims("validResetToken")).thenReturn(claims);
        when(jwtTokenizer.isTokenBlocked("validResetToken")).thenReturn(false);

        ResponseEntity<Boolean> response = authTokenEndpoint.validateResetToken(token);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody());
        verify(jwtTokenizer, times(1)).getClaims("validResetToken");
        verify(jwtTokenizer, times(1)).isTokenBlocked("validResetToken");
    }

    @Test
    void validateResetToken_InvalidPurpose() {
        String token = "Bearer validResetToken";
        Claims claims = mock(Claims.class);

        when(claims.get("purpose")).thenReturn("other_purpose");
        when(jwtTokenizer.getClaims("validResetToken")).thenReturn(claims);

        ResponseEntity<Boolean> response = authTokenEndpoint.validateResetToken(token);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody());
        verify(jwtTokenizer, times(1)).getClaims("validResetToken");
        verify(jwtTokenizer, never()).isTokenBlocked(anyString());
    }

    @Test
    void validateResetToken_TokenBlocked() {
        String token = "Bearer validResetToken";
        Claims claims = mock(Claims.class);

        when(claims.get("purpose")).thenReturn("reset_password");
        when(jwtTokenizer.getClaims("validResetToken")).thenReturn(claims);
        when(jwtTokenizer.isTokenBlocked("validResetToken")).thenReturn(true);

        ResponseEntity<Boolean> response = authTokenEndpoint.validateResetToken(token);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody());
        verify(jwtTokenizer, times(1)).getClaims("validResetToken");
        verify(jwtTokenizer, times(1)).isTokenBlocked("validResetToken");
    }

    @Test
    void validateResetToken_InvalidToken() {
        String token = "Bearer invalidToken";

        doThrow(new JwtException("Invalid token")).when(jwtTokenizer).getClaims("invalidToken");

        ResponseEntity<Boolean> response = authTokenEndpoint.validateResetToken(token);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody());
        verify(jwtTokenizer, times(1)).getClaims("invalidToken");
    }
}
