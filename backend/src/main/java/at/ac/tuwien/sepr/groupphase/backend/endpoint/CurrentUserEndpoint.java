package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/authentication")
public class CurrentUserEndpoint {

    @Autowired
    private UserService userService;

    @PermitAll
    @GetMapping("/user-points")
    public ResponseEntity<Integer> getUserPoints(@RequestParam String email) {
        Optional<ApplicationUser> user = Optional.ofNullable(userService.findApplicationUserByEmail(email));
        return user.map(applicationUser -> ResponseEntity.ok(applicationUser.getPoints())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(0));
    }
}
