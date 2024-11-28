package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
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

        // Users without admin rights
        createUserIfNotExists("Max", "Mustermann", "anna.simhofer@hotmail.com", "password", false);
        createUserIfNotExists("Lena", "Müller", "lena.mueller@email.com", "password", true);
        createUserIfNotExists("Tom", "Schmidt", "tom.schmidt@email.com", "password", false);
        createUserIfNotExists("Anna", "Meier", "anna.meier@email.com", "password", true);
        createUserIfNotExists("Felix", "Berger", "felix.berger@email.com", "password", false);

        // user with admin rights
        createUserIfNotExists("Herta", "Musterfrau", "herta.musterfrau@email.com", "password",
            true);
        createUserIfNotExists("Karl", "Admin", "karl.admin@email.com", "password", true);
    }

    private void createUserIfNotExists(String firstName, String lastName, String email,
        String password, boolean isAdmin) {
        if (userRepository.findUserByEmail(email).isEmpty()) {
            ApplicationUser user = new ApplicationUser(
                firstName, lastName, email, passwordEncoder.encode(password), isAdmin
            );
            userRepository.save(user);
            LOGGER.debug("User created: {}", email);
        }
    }

}
