package at.ac.tuwien.sepr.groupphase.backend.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RandomStringGenerator {

    public String generateRandomString(Long id) throws NoSuchAlgorithmException {
        String salt = "your_secret_salt";
        String input = id + salt;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes).substring(0, 32);
    }
}
