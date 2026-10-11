package com.restaurant.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility for salted SHA-256 password hashing and verification.
 */
public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a random cryptographic salt.
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes plain text password with a provided salt.
     */
    public static String hashPassword(String password, String salt) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            if (salt != null) {
                md.update(salt.getBytes(StandardCharsets.UTF_8));
            }
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies plain text password against stored hash and salt.
     */
    public static boolean verifyPassword(String plainPassword, String storedHash, String salt) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        String computedHash = hashPassword(plainPassword, salt);
        return computedHash.equals(storedHash);
    }
}
