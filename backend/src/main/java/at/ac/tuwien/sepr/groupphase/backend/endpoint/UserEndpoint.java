package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DeleteUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PurchaseItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateReadNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.InsufficientStockException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.MerchandiseService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UserEndpoint.BASE_PATH)
public class UserEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());
    public static final String BASE_PATH = "/api/v1/users";
    private final UserService userService;
    private final RandomStringGenerator randomStringGenerator;
    private final MerchandiseService merchandiseService;

    public UserEndpoint(UserService userService, RandomStringGenerator randomStringGenerator,
        MerchandiseService merchandiseService) {
        this.userService = userService;
        this.randomStringGenerator = randomStringGenerator;
        this.merchandiseService = merchandiseService;
    }

    @PermitAll
    @PutMapping()
    public ResponseEntity<Void> updateReadNews(@RequestBody UserUpdateReadNewsDto dto) {
        LOGGER.info("PUT " + BASE_PATH + "/{}", dto);
        LOGGER.debug("Body of request:\n{}", dto);

        userService.updateReadNews(dto);
        return ResponseEntity.ok().build();
    }

    @PermitAll
    @GetMapping("/user-points")
    public ResponseEntity<Integer> getUserPoints(@RequestParam String email) {
        Optional<ApplicationUser> user = Optional.ofNullable(userService.findApplicationUserByEmail(email));
        return user.map(applicationUser -> ResponseEntity.ok(applicationUser.getPoints())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(0));
    }

    @PermitAll
    @PostMapping("/deduct-points")
    public ResponseEntity<?> deductPoints(@RequestParam String encryptedId,
        @RequestParam int points) {
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

    @PermitAll
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseItems(@RequestBody List<PurchaseItemDto> purchaseItems) {
        if (purchaseItems == null || purchaseItems.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Purchase items cannot be empty"));
        }
        try {
            merchandiseService.processPurchase(purchaseItems);
            return ResponseEntity.ok(Map.of("message", "Purchase successful, stock updated"));
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @PermitAll
    @PutMapping("/update-user")
    public String updateUser(@RequestBody UserUpdateDto user)
        throws ValidationException, ConflictException {
        LOGGER.trace("PUT " + BASE_PATH + "/update-user/{}", user);
        return userService.updateUser(user);
    }

    @PermitAll
    @DeleteMapping
    public void deleteUser(@RequestBody DeleteUserDto userDto) throws ValidationException {
        LOGGER.trace("DELETE " + BASE_PATH + " {}", userDto);
        userService.deleteUser(userDto);
    }

    @PermitAll
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserUpdateDto> getUser(@PathVariable String id) {
        LOGGER.trace("GET " + BASE_PATH + " {}", id);
        UserUpdateDto user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }
}
