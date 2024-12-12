package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("generateData")
public class UserDataGenerator {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDataGenerator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void loadInitialData() {
        LOGGER.debug("generating users");

        createUserIfNotExists("Max", "Mustermann", "max.mustermann@email.com", "password", false, 77777);
        createUserIfNotExists("Lena", "Müller", "lena.mueller@email.com", "password", false, 6666);
        createUserIfNotExists("Tom", "Schmidt", "tom.schmidt@email.com", "password", false, 555);
        createUserIfNotExists("Anna", "Meier", "anna.meier@email.com", "password", false, 44);
        createUserIfNotExists("Felix", "Berger", "felix.berger@email.com", "password", false, 33);

        createUserIfNotExists("Herta", "Musterfrau", "herta.musterfrau@email.com", "password",
            true, 123456);
        createUserIfNotExists("Karl", "Admin", "karl.admin@email.com", "password", true, 77777);
    }

    private void createUserIfNotExists(String firstName, String lastName, String email,
                                       String password, boolean isAdmin, int points) {
        if (userRepository.findUserByEmail(email).isEmpty()) {
            ApplicationUser user = new ApplicationUser(
                firstName, lastName, email, passwordEncoder.encode(password), isAdmin, points
            );
            userRepository.save(user);
            LOGGER.debug("User created: {}", email);
        }
    }
}
