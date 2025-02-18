package at.ac.tuwien.sepr.groupphase.backend.security;

import at.ac.tuwien.sepr.groupphase.backend.entity.EncryptedId;
import at.ac.tuwien.sepr.groupphase.backend.repository.EncryptedIdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Service
public class RandomStringGenerator {

    @Value("${security.jwt.secret}")
    private String secretKey;
    private final EncryptedIdRepository encryptedIdRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public RandomStringGenerator(EncryptedIdRepository encryptedIdRepository) {
        this.encryptedIdRepository = encryptedIdRepository;
    }

    public String generateRandomString(Long id) {
        String salt = secretKey;
        String input = id + salt;

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        encryptedIdRepository.save(new EncryptedId(id, Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes).substring(0, 32)));

        return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes).substring(0, 32);
    }

    public Optional<Long> retrieveOriginalId(String encryptedId) {
        return encryptedIdRepository.findAll().stream()
            .filter(entity -> entity.getEncryptedId().equals(encryptedId))
            .map(EncryptedId::getId)
            .findFirst();
    }
}
