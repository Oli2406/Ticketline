package at.ac.tuwien.sepr.groupphase.backend.datagenerator;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

@Component
public class UserDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDataGenerator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostConstruct
    public void loadInitialData() {
        LOGGER.debug("generating users");
        if (userRepository.findUserByEmail("user@email.com").isEmpty()) {
            ApplicationUser user = new ApplicationUser(
                "user@email.com",
                passwordEncoder.encode("password"),
                false
            );
            userRepository.save(user);
        }

        if (userRepository.findUserByEmail("admin@email.com").isEmpty()) {
            ApplicationUser admin = new ApplicationUser(
                "admin@email.com",
                passwordEncoder.encode("password"),
                true
            );
            userRepository.save(admin);
        }
    }
}

