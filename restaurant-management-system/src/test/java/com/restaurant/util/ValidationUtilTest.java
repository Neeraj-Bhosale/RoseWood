package com.restaurant.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @Test
    @DisplayName("Should validate non-empty strings")
    public void testIsNotEmpty() {
        assertTrue(ValidationUtil.isNotEmpty("Hello"));
        assertFalse(ValidationUtil.isNotEmpty(""));
        assertFalse(ValidationUtil.isNotEmpty("   "));
        assertFalse(ValidationUtil.isNotEmpty(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"chef@restaurant.com", "user.name@sub.domain.org", "test+tag@gmail.com"})
    @DisplayName("Should accept valid email addresses")
    public void testValidEmails(String email) {
        assertTrue(ValidationUtil.isValidEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "@no-user.com", "missing@domain", ""})
    @DisplayName("Should reject invalid email addresses")
    public void testInvalidEmails(String email) {
        assertFalse(ValidationUtil.isValidEmail(email));
    }

    @Test
    @DisplayName("Should validate phone numbers")
    public void testPhoneValidation() {
        assertTrue(ValidationUtil.isValidPhone("+919876543210"));
        assertTrue(ValidationUtil.isValidPhone("9876543210"));
        assertFalse(ValidationUtil.isValidPhone("123")); // too short
        assertFalse(ValidationUtil.isValidPhone("letters_not_allowed"));
    }

    @Test
    @DisplayName("Should check positive values correctly")
    public void testPositiveValues() {
        assertTrue(ValidationUtil.isPositive(10.5));
        assertFalse(ValidationUtil.isPositive(0.0));
        assertFalse(ValidationUtil.isPositive(-5.0));
    }
}
