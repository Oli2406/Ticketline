package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLogoutDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateReadNewsDto;
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
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder,
        JwtTokenizer jwtTokenizer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.getAdmin()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }

            return new User(applicationUser.getEmail(), applicationUser.getPassword(),
                grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOGGER.debug("Find application user by email");
        ApplicationUser applicationUser = userRepository.findUserByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(
            String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());

        if (!userDetails.isAccountNonLocked()) {
            throw new BadCredentialsException(
                "Account is locked");
        }
        if (userDetails.isAccountNonExpired()
            && userDetails.isCredentialsNonExpired()
            && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())
        ) {
            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
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

        ApplicationUser user =
            userRepository
                .findUserByEmail(userLogoutDto.getEmail())
                .orElseThrow(
                    () ->
                        new NotFoundException(
                            String.format(
                                "Could not find the user with the email address %s",
                                userLogoutDto.getEmail())));

        if (!user.isLoggedIn()) {
            throw new IllegalStateException(
                String.format(
                    "The user with email %s is not currently logged in", userLogoutDto.getEmail()));
        }

        if (!jwtTokenizer.validateToken(authToken)) {
            throw new SecurityException("Invalid authentication token");
        }

        jwtTokenizer.blockToken(authToken);

        user.setLoggedIn(false);
        userRepository.save(user);

        LOGGER.info("User with email {} has successfully logged out.", userLogoutDto.getEmail());
    }

    @Override
    public String register(UserRegistrationDto userRegistrationDto)
        throws ValidationException, ConflictException {
        LOGGER.info("register user with email: {}", userRegistrationDto.getEmail());

        userValidator.validateRegister(userRegistrationDto);

        ApplicationUser toRegister = new ApplicationUser();
        toRegister.setFirstName(userRegistrationDto.getFirstName());
        toRegister.setLastName(userRegistrationDto.getLastName());
        toRegister.setEmail(userRegistrationDto.getEmail());
        String hashedPassword = passwordEncoder.encode(userRegistrationDto.getPassword());
        toRegister.setPassword(hashedPassword);
        toRegister.setAdmin(Boolean.TRUE.equals(userRegistrationDto.getIsAdmin()));

        LOGGER.debug("saving user to database with details: {}", toRegister);
        userRepository.save(toRegister);

        List<String> roles =
            toRegister.isAdmin() ? List.of("ROLE_ADMIN", "ROLE_USER") : List.of("ROLE_USER");
        return jwtTokenizer.getAuthToken(toRegister.getEmail(), roles);
    }

    @Override
    @Transactional
    public void updateReadNews(UserUpdateReadNewsDto userUpdateReadNewsDto) {
        LOGGER.trace("updateReadNews({})", userUpdateReadNewsDto);
        ApplicationUser user = findApplicationUserByEmail(userUpdateReadNewsDto.getEmail());
        user.getReadNewsIds().add(userUpdateReadNewsDto.getNewsId());
        userRepository.save(user);
    }
}
