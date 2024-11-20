package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLogoutDto;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/authentication")
public class LogoutEndpoint {

  private final UserService userService;

  public LogoutEndpoint(UserService userService) {
    this.userService = userService;
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void logout(@RequestBody UserLogoutDto userLogoutDto) {
    userService.logout(userLogoutDto);
  }
}
