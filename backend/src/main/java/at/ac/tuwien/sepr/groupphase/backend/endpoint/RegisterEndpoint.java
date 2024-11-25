package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class RegisterEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final String basePath = "/api/v1";
    private final UserService userService;

    public RegisterEndpoint(UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserRegistrationDto registerDto) throws ValidationException, ConflictException {
        LOGGER.trace("POST" + basePath + "/register");
        Map<String, String> response = new HashMap<>();
        userService.register(registerDto);
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }
}
