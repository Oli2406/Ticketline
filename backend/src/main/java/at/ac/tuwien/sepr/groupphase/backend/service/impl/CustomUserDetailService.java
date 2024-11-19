package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
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

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Loading user by email: {}", email);
        ApplicationUser applicationUser = findApplicationUserByEmail(email);

        List<GrantedAuthority> grantedAuthorities = applicationUser.getAdmin()
            ? AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER")
            : AuthorityUtils.createAuthorityList("ROLE_USER");

        return new User(applicationUser.getEmail(), applicationUser.getPassword(), grantedAuthorities);
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOGGER.debug("Finding application user by email: {}", email);
        return userRepository.findUserByEmail(email)
            .orElseThrow(() -> new NotFoundException(String.format("Could not find the user with the email address %s", email)));
    }


    @Override
    public String login(UserLoginDto userLoginDto) {

        ApplicationUser user = userRepository.findUserByEmail(userLoginDto.getEmail()).orElseThrow(() -> new NotFoundException(String.format("Could not find the user with the email address %s", userLoginDto.getEmail())));

        if (user.isLocked()) {
            throw new BadCredentialsException("Account is locked due to too many failed login attempts");
        }

        UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
        if (userDetails != null
            && userDetails.isAccountNonExpired()
            && userDetails.isAccountNonLocked()
            && userDetails.isCredentialsNonExpired()
            && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())
        ) {
            user.resetLoginAttempts();

            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }


            user.incrementLoginAttempts();
            user.setLastFailedLogin(LocalDateTime.now());

            if (user.getLoginAttempts() >= 5) {
                user.setLocked(true);
                throw new BadCredentialsException("User account has been locked because of too many incorrect attempts");
            }
            userRepository.save(user);

        throw new BadCredentialsException("Username or password is incorrect or account is locked");
    }
}
