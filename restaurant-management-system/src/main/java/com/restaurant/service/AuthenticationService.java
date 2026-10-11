package com.restaurant.service;

import java.util.logging.Logger;

import com.restaurant.data.DataStructureManager;
import com.restaurant.enums.UserRole;
import com.restaurant.exception.InvalidLoginException;
import com.restaurant.model.User;
import com.restaurant.repository.UserRepository;
import com.restaurant.util.ActivityLogger;
import com.restaurant.util.PasswordUtil;
import com.restaurant.util.ValidationUtil;

/**
 * Service managing user authentication, sessions, and role verification.
 */
public class AuthenticationService {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());
    private final UserRepository userRepository;
    private static User currentUser;

    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates user against stored salted SHA-256 hash.
     */
    public User login(String username, String plainPassword) throws InvalidLoginException {
        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(plainPassword)) {
            throw new InvalidLoginException("Username and password must not be empty.");
        }

        User user = userRepository.findByUsername(username.trim());
        if (user == null) {
            LOGGER.warning("Authentication failed: User not found for username '" + username + "'");
            throw new InvalidLoginException("Invalid username or password.");
        }

        boolean match = PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash(), user.getSalt());
        if (!match) {
            LOGGER.warning("Authentication failed: Incorrect password for username '" + username + "'");
            throw new InvalidLoginException("Invalid username or password.");
        }

        currentUser = user;
        DataStructureManager.getInstance().registerSession(user.getUsername());
        LOGGER.info("User '" + username + "' logged in successfully as " + user.getRole());
        // FILE HANDLING — write login event to activity log
        ActivityLogger.getInstance().logLogin(user.getUsername(), user.getRole().name());
        return user;
    }

    public User registerUser(String fullName, String username, String email, String phone, String plainPassword, UserRole role) {
        if (!ValidationUtil.isNotEmpty(fullName)) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (!ValidationUtil.isNotEmpty(username)) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("A valid email is required.");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new IllegalArgumentException("A valid phone number is required.");
        }
        if (!ValidationUtil.isNotEmpty(plainPassword)) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (role == null) {
            throw new IllegalArgumentException("User role is required.");
        }
        if (role == UserRole.ADMIN) {
            throw new IllegalArgumentException("Admin accounts cannot be created through public registration.");
        }

        String trimmedUsername = username.trim();
        String trimmedEmail = email.trim();
        if (userRepository.existsByUsername(trimmedUsername)) {
            throw new IllegalArgumentException("Username already exists. Please choose another one.");
        }
        if (userRepository.existsByEmail(trimmedEmail)) {
            throw new IllegalArgumentException("Email already exists. Please use a different email.");
        }

        User newUser = createUserForRole(role);
        newUser.setFullName(fullName.trim());
        newUser.setUsername(trimmedUsername);
        newUser.setEmail(trimmedEmail);
        newUser.setPhone(phone.trim());

        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(plainPassword, salt);
        newUser.setSalt(salt);
        newUser.setPasswordHash(passwordHash);

        boolean saved = userRepository.create(newUser, passwordHash, salt);
        if (!saved) {
            throw new IllegalStateException("User registration failed. Please try again.");
        }

        LOGGER.info("User '" + trimmedUsername + "' registered successfully as " + role + ".");
        return newUser;
    }

    private User createUserForRole(UserRole role) {
        return switch (role) {
            case ADMIN -> throw new IllegalArgumentException("Admin accounts cannot be created through public registration.");
            case MANAGER -> new com.restaurant.model.Manager();
            case WAITER -> new com.restaurant.model.Waiter();
            case CASHIER -> new com.restaurant.model.Cashier();
            case KITCHEN_STAFF -> new com.restaurant.model.KitchenStaff();
        };
    }

    public void logout() {
        if (currentUser != null) {
            LOGGER.info("User '" + currentUser.getUsername() + "' logged out.");
            // FILE HANDLING — write logout event to activity log
            ActivityLogger.getInstance().logLogout(currentUser.getUsername());
            DataStructureManager.getInstance().endSession(currentUser.getUsername());
            currentUser = null;
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean hasRole(UserRole role) {
        return currentUser != null && currentUser.getRole() == role;
    }

    public static boolean hasAnyRole(UserRole... roles) {
        if (currentUser == null || roles == null || roles.length == 0) {
            return false;
        }
        for (UserRole requiredRole : roles) {
            if (currentUser.getRole() == requiredRole) {
                return true;
            }
        }
        return false;
    }

    public static void requireRole(UserRole role) {
        requireAnyRole(role);
    }

    public static void requireAnyRole(UserRole... roles) {
        if (!hasAnyRole(roles)) {
            String requiredRoles = (roles == null || roles.length == 0)
                    ? "any valid role"
                    : java.util.Arrays.stream(roles)
                    .map(Enum::name)
                    .reduce((a, b) -> a + " or " + b)
                    .orElse("any valid role");
            throw new SecurityException("Access denied. Required role: " + requiredRoles + ".");
        }
    }
}
