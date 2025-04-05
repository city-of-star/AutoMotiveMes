package com.automotivemes;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UtilsTest {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void fun() {
        System.out.println(passwordEncoder.encode("123456"));
    }
}
