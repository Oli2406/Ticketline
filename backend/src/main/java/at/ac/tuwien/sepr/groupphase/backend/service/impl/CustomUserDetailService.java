package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.config.SecurityPropertiesConfig;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLogoutDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      MethodHandles.lookup().lookupClass());
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenizer jwtTokenizer;
  private final SecurityPropertiesConfig.Jwt jwt;
  private final SecurityPropertiesConfig.Auth auth;

  @Autowired
  public CustomUserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      JwtTokenizer jwtTokenizer, SecurityPropertiesConfig.Jwt jwt,
      SecurityPropertiesConfig.Auth auth) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenizer = jwtTokenizer;
    this.jwt = jwt;
    this.auth = auth;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    LOGGER.debug("Loading user by email: {}", email);
    ApplicationUser applicationUser = findApplicationUserByEmail(email);

    List<GrantedAuthority> grantedAuthorities = applicationUser.getAdmin()
        ? AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER")
        : AuthorityUtils.createAuthorityList("ROLE_USER");

    return User.builder()
        .username(applicationUser.getEmail())
        .password(applicationUser.getPassword())
        .accountLocked(applicationUser.isLocked())
        .authorities(grantedAuthorities)
        .build();
  }

  @Override
  public ApplicationUser findApplicationUserByEmail(String email) {
    LOGGER.debug("Finding application user by email: {}", email);
    return userRepository.findUserByEmail(email)
        .orElseThrow(() -> new NotFoundException(
            String.format("Could not find the user with the email address %s", email)));
  }


  @Override
  public String login(UserLoginDto userLoginDto) {
    LOGGER.debug("Login user: {}", userLoginDto);
    ApplicationUser user = userRepository.findUserByEmail(userLoginDto.getEmail()).orElseThrow(
        () -> new NotFoundException(
            String.format("Could not find the user with the email address %s",
                userLoginDto.getEmail())));

    UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
    if (!userDetails.isAccountNonLocked()) {
      throw new BadCredentialsException(
          "Account is locked due to too many failed login attempts");
    }
    if (userDetails.isAccountNonExpired()
        && userDetails.isCredentialsNonExpired()
        && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())
    ) {
      user.resetLoginAttempts();
      user.setLoggedIn(true);
      userRepository.save(user);

      List<String> roles = userDetails.getAuthorities()
          .stream()
          .map(GrantedAuthority::getAuthority)
          .toList();
      return jwtTokenizer.getAuthToken(
          userDetails.getUsername(),
          roles);
    }

    user.incrementLoginAttempts();
    user.setLastFailedLogin(LocalDateTime.now());

    if (user.getLoginAttempts() >= auth.getMaxLoginAttempts()) {
      user.setLocked(true);
      userRepository.save(user);
      throw new BadCredentialsException(
          "User account has been locked because of too many incorrect attempts");
    }
    userRepository.save(user);
    throw new BadCredentialsException("Username or password is incorrect");
  }

  @Override
  public void logout(UserLogoutDto userLogoutDto) {
    LOGGER.debug("Logout user: {}", userLogoutDto);
    String authToken = userLogoutDto.getAuthToken();

    ApplicationUser user = userRepository.findUserByEmail(userLogoutDto.getEmail())
        .orElseThrow(() -> new NotFoundException(
            String.format("Could not find the user with the email address %s",
                userLogoutDto.getEmail())));

    if (!user.isLoggedIn()) {
      throw new IllegalStateException(
          String.format("The user with email %s is not currently logged in",
              userLogoutDto.getEmail()));
    }

    if (!jwtTokenizer.validateToken(authToken)) {
      throw new SecurityException("Invalid authentication token");
    }

    jwtTokenizer.blockToken(authToken);

    user.setLoggedIn(false);
    userRepository.save(user);

    LOGGER.info("User with email {} has successfully logged out.", userLogoutDto.getEmail());
  }
}
