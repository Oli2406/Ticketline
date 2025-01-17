package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.config.SecurityPropertiesConfig;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DeleteUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLogoutDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateReadNewsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.security.RandomStringGenerator;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.validators.UserValidator;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class UserDetailServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final SecurityPropertiesConfig.Jwt jwt;
    private final SecurityPropertiesConfig.Auth auth;
    private final UserValidator userValidator;
    private final RandomStringGenerator randomStringGenerator;

    @Autowired
    public UserDetailServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
        JwtTokenizer jwtTokenizer,
        UserValidator userValidator, SecurityPropertiesConfig.Jwt jwt,
        SecurityPropertiesConfig.Auth auth, RandomStringGenerator randomStringGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.userValidator = userValidator;
        this.jwt = jwt;
        this.auth = auth;
        this.randomStringGenerator = randomStringGenerator;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Loading user by email: {}", email);
        ApplicationUser applicationUser = findApplicationUserByEmail(email);

        List<GrantedAuthority> grantedAuthorities =
            applicationUser.isAdmin() ? AuthorityUtils.createAuthorityList("ROLE_ADMIN",
                "ROLE_USER") : AuthorityUtils.createAuthorityList("ROLE_USER");

        return User.builder().username(applicationUser.getEmail())
            .password(applicationUser.getPassword()).accountLocked(applicationUser.isLocked())
            .authorities(grantedAuthorities).build();
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOGGER.debug("Finding application user by email: {}", email);
        return userRepository.findUserByEmail(email).orElseThrow(() -> new NotFoundException(
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

        if (user.isLocked()) {
            throw new BadCredentialsException("Account is locked");
        }
        if (userDetails.isAccountNonExpired() && userDetails.isCredentialsNonExpired()
            && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())) {
            user.resetLoginAttempts();
            user.setLoggedIn(true);
            userRepository.save(user);

            List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles,
                randomStringGenerator.generateRandomString(user.getId()), user.getPoints(),
                user.getFirstName(), user.getLastName());
        }

        user.incrementLoginAttempts();
        user.setLastFailedLogin(LocalDateTime.now());

        if (user.getLoginAttempts() >= auth.getMaxLoginAttempts()) {
            user.setLocked(true);
            user.setLoggedIn(false);
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

        ApplicationUser user = userRepository.findUserByEmail(userLogoutDto.getEmail()).orElseThrow(
            () -> new NotFoundException(
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

    @Override
    public boolean isUserLoggedIn(UserLogoutDto userLogoutDto) {
        LOGGER.debug("Is user logged in: {}", userLogoutDto);
        String authToken = userLogoutDto.getAuthToken();

        ApplicationUser user = userRepository.findUserByEmail(userLogoutDto.getEmail()).orElseThrow(
            () -> new NotFoundException(
                String.format("Could not find the user with the email address %s",
                    userLogoutDto.getEmail())));

        if (!user.isLoggedIn()) {
            jwtTokenizer.blockToken(authToken);
            return false;
        }

        return true;
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
        return jwtTokenizer.getAuthToken(toRegister.getEmail(), roles,
            randomStringGenerator.generateRandomString(toRegister.getId()), toRegister.getPoints(),
            toRegister.getFirstName(), toRegister.getLastName());
    }

    @Override
    @Transactional
    public void updateReadNews(UserUpdateReadNewsDto userUpdateReadNewsDto) {
        LOGGER.trace("updateReadNews({})", userUpdateReadNewsDto);
        ApplicationUser user = findApplicationUserByEmail(userUpdateReadNewsDto.getEmail());
        user.getReadNewsIds().add(userUpdateReadNewsDto.getNewsId());
        userRepository.save(user);
    }

    @Transactional
    @Override
    public String updateUserPoints(String encryptedId, int pointsToDeduct) throws Exception {
        Long originalId = randomStringGenerator.retrieveOriginalId(encryptedId)
            .orElseThrow(() -> new RuntimeException("User not found for the given encrypted ID"));

        ApplicationUser user = userRepository.findById(originalId)
            .orElseThrow(() -> new RuntimeException("User not found for the given ID"));

        if (user.getPoints() >= pointsToDeduct) {
            user.setPoints(user.getPoints() - pointsToDeduct);
            userRepository.save(user);
            return "Points updated successfully!";
        } else {
            throw new RuntimeException("Insufficient points!");
        }
    }

    @Transactional
    @Override
    public void addUserPoints(String encryptedId, int pointsToAdd) {
        Long originalId = randomStringGenerator.retrieveOriginalId(encryptedId)
            .orElseThrow(() -> new RuntimeException("User not found for the given encrypted ID"));

        ApplicationUser user = userRepository.findById(originalId)
            .orElseThrow(() -> new RuntimeException("User not found for the given ID"));

        user.setPoints(user.getPoints() + pointsToAdd);
        userRepository.save(user);
    }

    @Override
    public String updateUser(UserUpdateDto user) throws ValidationException, ConflictException {
        LOGGER.info("update user: {}", user);

        Long originalId = randomStringGenerator.retrieveOriginalId(user.getId())
            .orElseThrow(() -> new RuntimeException("User not found for the given encrypted ID"));

        ApplicationUser userToUpdate = userRepository.findById(originalId).orElseThrow(
            () -> new NotFoundException(
                String.format("Could not find the user with the email address %s",
                    user.getEmail())));

        if (!user.getVersion().equals(userToUpdate.getVersion())) {
            List<String> error = new ArrayList<>();
            error.add(
                "The data has been modified by another session. \n You will be logged out now.");
            throw new ConflictException("Update for customer has a conflict: ",
                error);
        }

        boolean emailChanged = hasEmailChanged(user, userToUpdate);
        boolean hasUserChanged = emailChanged || hasUserChanged(user, userToUpdate);
        if (hasUserChanged) {

            userValidator.validateUserForUpdate(user, emailChanged);

            userToUpdate.setFirstName(user.getFirstName());
            userToUpdate.setLastName(user.getLastName());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.incrementVersion();

            var password = user.getPassword();
            if (!password.isEmpty()) {
                String hashedPassword = passwordEncoder.encode(password);
                userToUpdate.setPassword(hashedPassword);
            }

            userRepository.save(userToUpdate);

            String authToken = user.getCurrentAuthToken();
            if (!jwtTokenizer.validateToken(authToken)) {
                throw new SecurityException("Invalid authentication token");
            }

            jwtTokenizer.blockToken(authToken);

            List<String> roles =
                userToUpdate.isAdmin() ? List.of("ROLE_ADMIN", "ROLE_USER") : List.of("ROLE_USER");

            return jwtTokenizer.getAuthToken(userToUpdate.getEmail(), roles,
                randomStringGenerator.generateRandomString(userToUpdate.getId()),
                userToUpdate.getPoints(), userToUpdate.getFirstName(), userToUpdate.getLastName());
        }

        return user.getCurrentAuthToken();
    }

    private static boolean hasUserChanged(UserUpdateDto user, ApplicationUser userToUpdate) {
        boolean hasFirstNameChanged = user.getFirstName().equals(userToUpdate.getFirstName());
        boolean hasLastNameChanged = user.getLastName().equals(userToUpdate.getLastName());
        boolean hasPasswordChanged = !user.getPassword().isEmpty();

        return hasFirstNameChanged || hasLastNameChanged || hasPasswordChanged;
    }

    private static boolean hasEmailChanged(UserUpdateDto user, ApplicationUser userToUpdate) {
        return !user.getEmail().equals(userToUpdate.getEmail());
    }

    @Override
    public void deleteUser(DeleteUserDto userDto) throws ValidationException {
        LOGGER.info("delete user: {}", userDto);

        ApplicationUser userToDelete = userRepository.findUserByEmail(userDto.getEmail())
            .orElseThrow(
                () -> new NotFoundException(
                    String.format("Could not find the user with the id %s",
                        userDto.getEmail())));

        if (userToDelete.isAdmin()) {
            List<ApplicationUser> admins = userRepository.findAllByAdmin(true);

            if (admins.size() <= 1) {
                List<String> errors = new ArrayList<>();
                errors.add("Can not delete user because at least one admin has to exist.");
                throw new ValidationException("Error during deletion", errors);
            }
        }

        userRepository.delete(userToDelete);

        String authToken = userDto.getAuthToken();

        // In case an admin deletes a user authToken should be null
        if (authToken != null && !authToken.isEmpty()) {
            if (!jwtTokenizer.validateToken(authToken)) {
                throw new SecurityException("Invalid authentication token");
            }

            jwtTokenizer.blockToken(authToken);
        }

        LOGGER.info("deleted user: {}", userDto);
    }

    @Override
    public UserUpdateDto getUser(String encryptedId) throws NotFoundException {
        LOGGER.info("get user with encrypted id: {}", encryptedId);

        Long originalId = randomStringGenerator.retrieveOriginalId(encryptedId)
            .orElseThrow(() -> new RuntimeException("User not found for the given encrypted ID"));

        ApplicationUser user = userRepository.findById(originalId).orElseThrow(
            () -> new NotFoundException(
                String.format("Could not find the user with the encrypted id %s", encryptedId)));

        UserUpdateDto userDto = new UserUpdateDto();
        userDto.setId(encryptedId);
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setVersion(user.getVersion());
        return userDto;
    }
}
