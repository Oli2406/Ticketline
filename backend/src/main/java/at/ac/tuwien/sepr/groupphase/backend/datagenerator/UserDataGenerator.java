package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
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

        createUserIfNotExists("Max", "Mustermann", "max.mustermann@email.com", "password", false,
            77777);
        createUserIfNotExists("Lena", "Müller", "lena.mueller@email.com", "password", false, 6666);
        createUserIfNotExists("Tom", "Schmidt", "tom.schmidt@email.com", "password", false, 555);
        createUserIfNotExists("Anna", "Meier", "anna.meier@email.com", "password", false, 44);
        createUserIfNotExists("Felix", "Berger", "felix.berger@email.com", "password", false, 33);

        createUserIfNotExists("Herta", "Musterfrau", "herta.musterfrau@email.com", "password",
            true, 123456);
        createUserIfNotExists("Karl", "Admin", "karl.admin@email.com", "password", true, 77777);
        createUserIfNotExists("日向", "翔陽", "hinata.shoyo@email.com", "password", false, 10);
        createUserIfNotExists("أمل", "لماس", "لماس@email.com", "password", false, 10);
        createUserIfNotExists("Sophia", "Admin", "sophia.admin@email.com", "password", true, 50000);
        createUserIfNotExists("Liam", "Manager", "liam.manager@email.com", "password", true, 45000);
        createUserIfNotExists("Isabella", "Supervisor", "isabella.supervisor@email.com", "password",
            true, 40000);

        IntStream.range(1, 1500).forEach(i -> {
            String firstName = getRandomFirstName();
            String lastName = getRandomLastName();
            String email = String.format("%s.%s%d@email.com", firstName.toLowerCase(),
                lastName.toLowerCase(), i);
            int points = ThreadLocalRandom.current().nextInt(100000);
            createUserIfNotExists(firstName, lastName, email, "password", false, points);
        });

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

    private String getRandomFirstName() {
        List<String> firstNames = Arrays.asList("Emma", "Noah", "Olivia", "Liam", "Ava", "Ethan",
            "Sophia", "Mason", "Isabella", "Lucas", "Mia", "日向", "翔陽", "أمل", "لماس", "Carlos",
            "Marta", "Yuki", "Hinata", "Amal", "Marry", "Lisa", "Jonas", "Andreas", "Alex", "Anton",
            "Antonia", "Victoria", "Victor", "Johnny", "Alois", "Chloe", "Mohammed",
            "Anya", "Miguel", "Elena", "Aria", "Johan", "Livia");
        return firstNames.get(ThreadLocalRandom.current().nextInt(firstNames.size()));
    }

    private String getRandomLastName() {
        List<String> lastNames = Arrays.asList("Dvořák", "Łukasz", "O’Connor", "Mårtensson",
            "Nørgaard", "Sánchez", "Dubois", "García", "Hernández", "Kowalski", "Åström", "Černý",
            "Živković", "Öztürk", "İbrahim", "Grønn", "Leclerc", "Šimunović", "Đorđević", "Müller",
            "Grüber", "Vasquez", "Pérez", "Håkansson", "Kovačić", "Díaz", "Rām", "Özdemir",
            "Hofmann", "Łęcki", "Kužma", "Depp", "Mc Donald");
        return lastNames.get(ThreadLocalRandom.current().nextInt(lastNames.size()));
    }
}
