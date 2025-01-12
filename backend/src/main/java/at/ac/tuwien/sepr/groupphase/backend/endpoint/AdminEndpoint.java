package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.AdminService;
import at.ac.tuwien.sepr.groupphase.backend.service.ResetPasswordService;
import java.lang.invoke.MethodHandles;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AdminEndpoint.BASE_PATH)
public class AdminEndpoint {

    public static final String BASE_PATH = "/api/v1/admin";
    private final AdminService adminService;
    private final ResetPasswordService resetPasswordService;
    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public AdminEndpoint(AdminService adminService, ResetPasswordService resetPasswordService) {
        this.adminService = adminService;
        this.resetPasswordService = resetPasswordService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/unlock/{id}")
    public ResponseEntity<Void> unlockUser(@PathVariable(name = "id") Long id) {
        LOGGER.trace("POST " + BASE_PATH + "/unlock/{}", id);
        adminService.unlockUser(id);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/lock/{id}")
    public ResponseEntity<Void> lockUser(@PathVariable(name = "id") Long id) {
        LOGGER.trace("POST " + BASE_PATH + "/lock/{}", id);
        adminService.lockUser(id);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/send-email")
    public ResponseEntity<Void> sendEmailToResetPassword(
        @RequestBody String email) {
        LOGGER.trace("POST " + BASE_PATH + "/send-email/{}", email);
        resetPasswordService.sendEmailToResetPassword(email);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public ResponseEntity<List<UserDetailDto>> getAllUsers() {
        LOGGER.trace("GET " + BASE_PATH);
        List<UserDetailDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

}
