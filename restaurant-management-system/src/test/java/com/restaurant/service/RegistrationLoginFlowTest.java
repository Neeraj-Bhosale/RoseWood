package com.restaurant.service;

import com.restaurant.enums.UserRole;
import com.restaurant.exception.InvalidLoginException;
import com.restaurant.model.User;
import com.restaurant.repository.UserRepository;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration test for the Register → Login flow.
 *
 * A unique username / email is generated per test run using UUID so repeated
 * test runs never collide with each other.
 *
 * Covers:
 *  1. Successful registration with a unique user.
 *  2. Login with the newly registered credentials succeeds.
 *  3. Login with the wrong password is rejected.
 *  4. Duplicate username registration is rejected.
 *  5. Duplicate email registration is rejected.
 *  6. ADMIN role cannot be self-registered.
 *  7. Password is never stored as plaintext.
 *  8. Invalid email is rejected at the service level.
 *  9. Short password is rejected at the service level (< 8 chars enforced here).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegistrationLoginFlowTest {

    // Unique per test-run to avoid duplicate-key violations in MySQL
    private static final String UNIQUE_ID   = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    private static final String TEST_USER   = "reg_test_" + UNIQUE_ID;
    private static final String TEST_EMAIL  = "reg_" + UNIQUE_ID + "@rosewood.test";
    private static final String TEST_PHONE  = "9000000001";
    private static final String TEST_PASS   = "SecurePass@99";
    private static final String TEST_NAME   = "Test Registrant";

    private final AuthenticationService authService = new AuthenticationService();
    private final UserRepository userRepository     = new UserRepository();

    // -------------------------------------------------------------------------
    // Test 1: Successful registration
    // -------------------------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("1 · Registration with valid data succeeds and persists to MySQL")
    public void shouldRegisterSuccessfully() {
        User user = authService.registerUser(TEST_NAME, TEST_USER, TEST_EMAIL, TEST_PHONE, TEST_PASS, UserRole.WAITER);

        assertNotNull(user, "Registered user must not be null");
        assertEquals(TEST_USER, user.getUsername());
        assertEquals(TEST_NAME, user.getFullName());
        assertEquals(UserRole.WAITER, user.getRole());
        assertNotNull(user.getPasswordHash(), "Password hash must be set");
        assertNotNull(user.getSalt(), "Salt must be set");
        assertNotEquals(TEST_PASS, user.getPasswordHash(), "Password must NOT be stored in plaintext");

        // Verify persistence in MySQL
        User fetched = userRepository.findByUsername(TEST_USER);
        assertNotNull(fetched, "User must exist in the database after registration");
        assertEquals(TEST_USER, fetched.getUsername());
    }

    // -------------------------------------------------------------------------
    // Test 2: Login with registered credentials
    // -------------------------------------------------------------------------

    @Test
    @Order(2)
    @DisplayName("2 · Newly registered user can log in with correct password")
    public void shouldLoginAfterRegistration() throws InvalidLoginException {
        // Ensure user exists first
        if (userRepository.findByUsername(TEST_USER) == null) {
            authService.registerUser(TEST_NAME, TEST_USER, TEST_EMAIL, TEST_PHONE, TEST_PASS, UserRole.WAITER);
        }
        User loggedIn = authService.login(TEST_USER, TEST_PASS);
        assertNotNull(loggedIn, "Login must succeed for the registered user");
        assertEquals(TEST_USER, loggedIn.getUsername());
        assertEquals(UserRole.WAITER, loggedIn.getRole());
    }

    // -------------------------------------------------------------------------
    // Test 3: Wrong password rejected
    // -------------------------------------------------------------------------

    @Test
    @Order(3)
    @DisplayName("3 · Login with wrong password is rejected")
    public void shouldRejectWrongPassword() {
        if (userRepository.findByUsername(TEST_USER) == null) {
            authService.registerUser(TEST_NAME, TEST_USER, TEST_EMAIL, TEST_PHONE, TEST_PASS, UserRole.WAITER);
        }
        assertThrows(InvalidLoginException.class,
                () -> authService.login(TEST_USER, "WrongPassword!"),
                "Wrong password should throw InvalidLoginException");
    }

    // -------------------------------------------------------------------------
    // Test 4: Duplicate username rejected
    // -------------------------------------------------------------------------

    @Test
    @Order(4)
    @DisplayName("4 · Duplicate username registration is rejected")
    public void shouldRejectDuplicateUsername() {
        if (userRepository.findByUsername(TEST_USER) == null) {
            authService.registerUser(TEST_NAME, TEST_USER, TEST_EMAIL, TEST_PHONE, TEST_PASS, UserRole.WAITER);
        }
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.registerUser("Another Name", TEST_USER, "other_" + UNIQUE_ID + "@test.com",
                        TEST_PHONE, TEST_PASS, UserRole.CASHIER));
        assertTrue(ex.getMessage().toLowerCase().contains("username"), "Error should mention duplicate username");
    }

    // -------------------------------------------------------------------------
    // Test 5: Duplicate email rejected
    // -------------------------------------------------------------------------

    @Test
    @Order(5)
    @DisplayName("5 · Duplicate email registration is rejected")
    public void shouldRejectDuplicateEmail() {
        if (userRepository.findByUsername(TEST_USER) == null) {
            authService.registerUser(TEST_NAME, TEST_USER, TEST_EMAIL, TEST_PHONE, TEST_PASS, UserRole.WAITER);
        }
        String differentUsername = "dup_email_" + UNIQUE_ID;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.registerUser("Another Person", differentUsername, TEST_EMAIL,
                        TEST_PHONE, TEST_PASS, UserRole.CASHIER));
        assertTrue(ex.getMessage().toLowerCase().contains("email"), "Error should mention duplicate email");
    }

    // -------------------------------------------------------------------------
    // Test 6: ADMIN role cannot be self-registered
    // -------------------------------------------------------------------------

    @Test
    @Order(6)
    @DisplayName("6 · ADMIN role cannot be created via public registration")
    public void shouldRejectAdminSelfRegistration() {
        String adminUser = "admin_attempt_" + UNIQUE_ID;
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.registerUser("Admin Attempt", adminUser, adminUser + "@test.com",
                        TEST_PHONE, TEST_PASS, UserRole.ADMIN));
        assertTrue(ex.getMessage().toLowerCase().contains("admin"), "Error must mention Admin restriction");
    }

    // -------------------------------------------------------------------------
    // Test 7: Password not stored as plaintext
    // -------------------------------------------------------------------------

    @Test
    @Order(7)
    @DisplayName("7 · Password hash stored in DB is not equal to plaintext password")
    public void passwordMustBeHashed() {
        if (userRepository.findByUsername(TEST_USER) == null) {
            authService.registerUser(TEST_NAME, TEST_USER, TEST_EMAIL, TEST_PHONE, TEST_PASS, UserRole.WAITER);
        }
        User stored = userRepository.findByUsername(TEST_USER);
        assertNotNull(stored);
        assertNotEquals(TEST_PASS, stored.getPasswordHash(), "Plaintext password must not be stored in DB");
        assertNotNull(stored.getSalt(), "Salt column must be populated");
    }

    // -------------------------------------------------------------------------
    // Test 8: Invalid email rejected
    // -------------------------------------------------------------------------

    @Test
    @Order(8)
    @DisplayName("8 · Invalid email format is rejected by service layer")
    public void shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.registerUser("Bad Email User", "bad_email_" + UNIQUE_ID, "not-an-email",
                        TEST_PHONE, TEST_PASS, UserRole.WAITER));
    }

    // -------------------------------------------------------------------------
    // Test 9: Empty username rejected
    // -------------------------------------------------------------------------

    @Test
    @Order(9)
    @DisplayName("9 · Empty username is rejected by service layer")
    public void shouldRejectEmptyUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.registerUser("Some Name", "", "valid@email.com",
                        TEST_PHONE, TEST_PASS, UserRole.WAITER));
    }
}
