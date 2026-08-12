package com.supremecourt.studentgradingsystem.utils;

import java.security.SecureRandom;
import java.util.UUID;

public class OTPCodeGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generateCode() {
        Integer code = 100000 + random.nextInt(900000); // Generates a random 6-digit code
        return String.valueOf(code);
    }

    public static String generateResetPasswordToken() {
        return UUID.randomUUID().toString();
    }
}