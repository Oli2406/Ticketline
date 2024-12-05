package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateReadNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(UserEndpoint.BASE_PATH)
public class UserEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String BASE_PATH = "/api/v1/users";
    private final UserService userService;
    private final RandomStringGenerator randomStringGenerator;

    public UserEndpoint(UserService userService, RandomStringGenerator randomStringGenerator) {
        this.userService = userService;
        this.randomStringGenerator = randomStringGenerator;
    }

    @PermitAll
    @PutMapping()
    public ResponseEntity<Void> updateReadNews(@RequestBody UserUpdateReadNewsDto dto) {
        LOG.info("PUT " + BASE_PATH + "/{}", dto);
        LOG.debug("Body of request:\n{}", dto);

        userService.updateReadNews(dto);
        return ResponseEntity.ok().build();
    }

    @PermitAll
    @PostMapping("/deduct-points")
    public ResponseEntity<?> deductPoints(@RequestParam String encryptedId, @RequestParam int points) {
        try {
            userService.updateUserPoints(encryptedId, points);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PermitAll
    @PostMapping("/add-points")
    public ResponseEntity<?> addPoints(@RequestParam String encryptedId, @RequestParam int points) {
        try {
            userService.addUserPoints(encryptedId, points);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
