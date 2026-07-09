package com.epam.gymapp.util;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * This utility component is used to generate random passwords.
 *
 * <p>
 *     The generated password has a fixed length of {@value #PASSWORD_LENGTH} characters. It is built from English
 *     alphabet letters, both uppercase and lowercase, alongside numbers and a predefined
 *     set of symbols.
 * </p>
 * <p>
 *     The generator uses {@link SecureRandom}, making it more suitable for secure password
 *     generation.
 * </p>
 */
@Component
@NoArgsConstructor
public class PasswordGenerator {

    private static final String ALLOWED_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "0123456789" +
                    ".!/,';:@#$%^&*()-_=+[]{}?";
    private static final int PASSWORD_LENGTH = 10;

    private final SecureRandom random = new SecureRandom();

    /**
     * Generates a random {@value #PASSWORD_LENGTH}-character password using letters from English alphabet,
     * digits, and symbols, as defined in the variable {@code ALLOWED_CHARS}.
     * @return a randomly generated password with a length of {@value #PASSWORD_LENGTH} characters
     */
    public String generate() {
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(ALLOWED_CHARS.length());
            password.append(ALLOWED_CHARS.charAt(index));
        }

        return password.toString();
    }
}
