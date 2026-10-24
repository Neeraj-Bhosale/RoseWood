package com.restaurant.service;

// Java logging is used to print messages and record important events.
import java.util.logging.Logger;

// These imports bring in the data manager, user role enum, user model, repository, and utilities.
import com.restaurant.data.DataStructureManager;
import com.restaurant.enums.UserRole;
import com.restaurant.exception.InvalidLoginException;
import com.restaurant.model.User;
import com.restaurant.repository.UserRepository;
import com.restaurant.util.ActivityLogger;
import com.restaurant.util.PasswordUtil;
import com.restaurant.util.ValidationUtil;

/**
 * This class handles login, registration, logout, and role checks.
 * In simple terms: it controls who can enter the system and what they can do.
 */
public class AuthenticationService {
    // Logger writes messages to the console and to log files.
    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());
    // Repository is used to access user data from the database.
    private final UserRepository userRepository;
    // This stores the currently logged-in user.
    private static User currentUser;

    // Default constructor creates the repository automatically.
    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    // Constructor injection: we can pass a custom repository if needed.
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method checks username and password and logs the user in.
     */
    public User login(String username, String plainPassword) throws InvalidLoginException {
        // If username or password is empty, reject login.
        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(plainPassword)) {
            throw new InvalidLoginException("Username and password must not be empty.");
        }

        // Find the user in the database using username or email.
        User user = userRepository.findByUsernameOrEmail(username.trim());
        if (user == null) {
            LOGGER.warning("Authentication failed: User not found for username/email '" + username + "'");
            throw new InvalidLoginException("Invalid username/email or password.");
        }

        // Verify the plain-text password against the stored hash and salt.
        boolean match = PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash(), user.getSalt());
        if (!match) {
            LOGGER.warning("Authentication failed: Incorrect password for username '" + username + "'");
            throw new InvalidLoginException("Invalid username or password.");
        }

        // If login is successful, save the current user and start a session.
        currentUser = user;
        DataStructureManager.getInstance().registerSession(user.getUsername());
        LOGGER.info("User '" + username + "' logged in successfully as " + user.getRole());
        // Write login info to the app log.
        ActivityLogger.getInstance().logLogin(user.getUsername(), user.getRole().name());
        return user;
    }

    // This method creates a new user account.
    public User registerUser(String fullName, String username, String email, String phone, String plainPassword, UserRole role) {
        // Check all required values before creating the account.
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

        // Clean the input values before saving.
        String trimmedUsername = username.trim();
        String trimmedEmail = email.trim();
        if (userRepository.existsByUsername(trimmedUsername)) {
            throw new IllegalArgumentException("Username already exists. Please choose another one.");
        }
        if (userRepository.existsByEmail(trimmedEmail)) {
            throw new IllegalArgumentException("Email already exists. Please use a different email.");
        }

        // Create a user object for the selected role.
        User newUser = createUserForRole(role);
        newUser.setFullName(fullName.trim());
        newUser.setUsername(trimmedUsername);
        newUser.setEmail(trimmedEmail);
        newUser.setPhone(phone.trim());

        // Generate a salt and hash the password before saving.
        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(plainPassword, salt);
        newUser.setSalt(salt);
        newUser.setPasswordHash(passwordHash);

        // Save the user in the database.
        boolean saved = userRepository.create(newUser, passwordHash, salt);
        if (!saved) {
            throw new IllegalStateException("User registration failed. Please try again.");
        }

        LOGGER.info("User '" + trimmedUsername + "' registered successfully as " + role + ".");
        return newUser;
    }

    // This method creates a specific user object depending on the role.
    private User createUserForRole(UserRole role) {
        return switch (role) {
            case ADMIN -> throw new IllegalArgumentException("Admin accounts cannot be created through public registration.");
            case MANAGER -> new com.restaurant.model.Manager();
            case WAITER -> new com.restaurant.model.Waiter();
            case CASHIER -> new com.restaurant.model.Cashier();
            case KITCHEN_STAFF -> new com.restaurant.model.KitchenStaff();
        };
    }

    // Logout current user and end the session.
    public void logout() {
        if (currentUser != null) {
            LOGGER.info("User '" + currentUser.getUsername() + "' logged out.");
            // Write logout event to the activity log.
            ActivityLogger.getInstance().logLogout(currentUser.getUsername());
            DataStructureManager.getInstance().endSession(currentUser.getUsername());
            currentUser = null;
        }
    }

    // Get currently logged-in user.
    public static User getCurrentUser() {
        return currentUser;
    }

    // Check if a user is logged in.
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // Check whether the logged-in user has exactly this role.
    public static boolean hasRole(UserRole role) {
        return currentUser != null && currentUser.getRole() == role;
    }

    // Check if user has any of the given roles.
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

    // This method ensures the current user has the required role.
    public static void requireRole(UserRole role) {
        requireAnyRole(role);
    }

    // This method checks a list of roles and throws an exception if access is denied.
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
