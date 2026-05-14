package com.epam.gymapp.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordGeneratorTest {

    private final PasswordGenerator passwordGenerator = new PasswordGenerator();

    @Test
    void generate_shouldReturnPasswordWithLength10() {
        String password = passwordGenerator.generate();

        assertThat(password).hasSize(10);
    }

    @Test
    void generate_shouldReturnDifferentPasswordsUsually() {
        String first = passwordGenerator.generate();
        String second = passwordGenerator.generate();

        assertThat(first).isNotEqualTo(second);
    }
}