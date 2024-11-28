package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.service.AdminService;
import at.ac.tuwien.sepr.groupphase.backend.service.ResetPasswordService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/admin")
public class AdminEndpoint {

    private final AdminService adminService;
    private final ResetPasswordService resetPasswordService;

    public AdminEndpoint(AdminService adminService, ResetPasswordService resetPasswordService) {
        this.adminService = adminService;
        this.resetPasswordService = resetPasswordService;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping(value = "/unlock/{id}")
    public ResponseEntity<Void> unlockUser(@PathVariable(name = "id") Long id) {
        adminService.unlockUser(id);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/lock/{id}")
    public ResponseEntity<Void> lockUser(@PathVariable(name = "id") Long id) {
        adminService.lockUser(id);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/reset-password/{email}")
    public ResponseEntity<Void> resetPassword(@PathVariable(name = "email") String email) {
        resetPasswordService.resetPassword(email);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public ResponseEntity<List<UserDetailDto>> getAllUsers() {
        List<UserDetailDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
