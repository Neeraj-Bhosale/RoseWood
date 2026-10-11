package com.restaurant.service;

import com.restaurant.enums.UserRole;
import com.restaurant.model.User;
import com.restaurant.model.Waiter;
import com.restaurant.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationRegistrationTest {

    @Test
    @DisplayName("Public registration should reject admin role")
    public void shouldRejectAdminRegistration() {
        AuthenticationService service = new AuthenticationService(new UserRepository() {
            @Override
            public User findByUsername(String username) {
                return null;
            }

            @Override
            public boolean existsByUsername(String username) {
                return false;
            }

            @Override
            public boolean existsByEmail(String email) {
                return false;
            }

            @Override
            public boolean create(User user, String passwordHash, String salt) {
                return true;
            }
        });

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.registerUser("System Admin", "admin", "admin@example.com", "9876543210", "StrongPass123", UserRole.ADMIN));

        assertTrue(exception.getMessage().contains("Admin"));
    }

    @Test
    @DisplayName("Public registration should allow non-admin roles")
    public void shouldAllowNonAdminRegistration() {
        AuthenticationService service = new AuthenticationService(new UserRepository() {
            @Override
            public User findByUsername(String username) {
                return null;
            }

            @Override
            public boolean existsByUsername(String username) {
                return false;
            }

            @Override
            public boolean existsByEmail(String email) {
                return false;
            }

            @Override
            public boolean create(User user, String passwordHash, String salt) {
                return true;
            }
        });

        User user = service.registerUser("Asha Kumar", "asha_waiter", "asha@example.com", "9876543210", "StrongPass123", UserRole.WAITER);

        assertNotNull(user);
        assertEquals(UserRole.WAITER, user.getRole());
        assertEquals("asha_waiter", user.getUsername());
        assertNotNull(user.getPasswordHash());
        assertNotNull(user.getSalt());
        assertTrue(user instanceof Waiter);
    }
}
