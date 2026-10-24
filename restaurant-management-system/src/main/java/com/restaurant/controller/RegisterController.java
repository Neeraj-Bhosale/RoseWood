package com.restaurant.controller;

import com.restaurant.Main;
import com.restaurant.enums.UserRole;
import com.restaurant.service.AuthenticationService;
import com.restaurant.util.AlertUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the Staff Registration screen.
 *
 * Handles input validation, password strength checking, duplicate detection,
 * and secure account creation via AuthenticationService → UserRepository → MySQL.
 *
 * Rules enforced:
 *  - Full name, username, email, phone, password are required.
 *  - Email must match a valid pattern.
 *  - Phone must match a 7–15 digit pattern.
 *  - Password must be at least 8 characters with strength hints.
 *  - Password and confirm password must match.
 *  - Username and email must be unique (checked in DB).
 *  - ADMIN role cannot be selected; only MANAGER, WAITER, CASHIER, KITCHEN_STAFF.
 *  - Passwords are stored as salted SHA-256 hashes — never plaintext.
 */
public class RegisterController {

    private static final Logger LOGGER = Logger.getLogger(RegisterController.class.getName());

    // Form fields
    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;

    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordVisibleField;

    // Toggle buttons
    @FXML private Button togglePasswordBtn;
    @FXML private Button toggleConfirmBtn;

    // Password strength bars
    @FXML private Region strengthBar1;
    @FXML private Region strengthBar2;
    @FXML private Region strengthBar3;
    @FXML private Region strengthBar4;

    // Status labels & containers
    @FXML private ScrollPane registerScrollPane;
    @FXML private Label strengthLabel;
    @FXML private Label passwordMatchLabel;
    @FXML private Label errorLabel;
    @FXML private Label bottomErrorLabel;
    @FXML private Label successLabel;
    @FXML private Button registerButton;

    private boolean passwordVisible = false;
    private boolean confirmVisible = false;

    private final AuthenticationService authService = new AuthenticationService();

    // Role names visible in the drop-down (ADMIN intentionally excluded)
    private static final String[] ROLES = {
            "Manager", "Waiter / Server", "Cashier", "Kitchen Staff"
    };

    @FXML
    public void initialize() {
        // Populate role combo box
        if (roleComboBox != null) {
            roleComboBox.getItems().addAll(ROLES);
        }

        // Sync visible-text fields with password fields (bidirectional)
        if (passwordField != null && passwordVisibleField != null) {
            passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        }
        if (confirmPasswordField != null && confirmPasswordVisibleField != null) {
            confirmPasswordVisibleField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
        }

        // Live password strength listener
        if (passwordField != null) {
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
                updateStrengthIndicator(newVal);
                checkPasswordMatch();
            });
        }

        // Live confirm-match listener
        if (confirmPasswordField != null) {
            confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> checkPasswordMatch());
        }
    }

    // -------------------------------------------------------------------------
    // Toggle password visibility
    // -------------------------------------------------------------------------

    @FXML
    public void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        applyToggle(passwordVisible, passwordField, passwordVisibleField);
    }

    @FXML
    public void toggleConfirmVisibility() {
        confirmVisible = !confirmVisible;
        applyToggle(confirmVisible, confirmPasswordField, confirmPasswordVisibleField);
    }

    private void applyToggle(boolean show, PasswordField masked, TextField plain) {
        if (masked == null || plain == null) return;
        if (show) {
            plain.setManaged(true);
            plain.setVisible(true);
            masked.setManaged(false);
            masked.setVisible(false);
        } else {
            masked.setManaged(true);
            masked.setVisible(true);
            plain.setManaged(false);
            plain.setVisible(false);
        }
    }

    // -------------------------------------------------------------------------
    // Password strength indicator
    // -------------------------------------------------------------------------

    /**
     * Calculates a 0–4 strength score and updates bar colours + label.
     * Criteria (one point each):
     *   1. At least 8 characters
     *   2. Contains uppercase letter
     *   3. Contains digit
     *   4. Contains special character
     */
    private void updateStrengthIndicator(String pw) {
        if (strengthBar1 == null) return;

        int score = 0;
        if (pw != null && pw.length() >= 8)                      score++;
        if (pw != null && pw.matches(".*[A-Z].*"))               score++;
        if (pw != null && pw.matches(".*[0-9].*"))               score++;
        if (pw != null && pw.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?].*")) score++;

        String[] colors = { "#1e293b", "#ef4444", "#f59e0b", "#3b82f6", "#10b981" };
        String[] labels = { "", "Weak", "Fair", "Good", "Strong" };
        String color = colors[score];

        Region[] bars = { strengthBar1, strengthBar2, strengthBar3, strengthBar4 };
        for (int i = 0; i < bars.length; i++) {
            if (bars[i] != null) {
                bars[i].setStyle("-fx-background-color: " + (i < score ? color : "#1e293b") + "; -fx-background-radius: 2;");
            }
        }
        if (strengthLabel != null) {
            strengthLabel.setText(score > 0 ? "Strength: " + labels[score] : "");
            String labelColor = switch (score) {
                case 1 -> "#ef4444";
                case 2 -> "#f59e0b";
                case 3 -> "#3b82f6";
                case 4 -> "#10b981";
                default -> "#64748b";
            };
            strengthLabel.setStyle("-fx-text-fill: " + labelColor + "; -fx-font-size: 10px;");
        }
    }

    private void checkPasswordMatch() {
        if (passwordMatchLabel == null || passwordField == null || confirmPasswordField == null) return;
        String pw  = passwordField.getText();
        String cpw = confirmPasswordField.getText();
        if (cpw.isEmpty()) {
            passwordMatchLabel.setText("");
            return;
        }
        if (pw.equals(cpw)) {
            passwordMatchLabel.setText("✓ Passwords match");
            passwordMatchLabel.setStyle("-fx-text-fill: #10b981; -fx-font-size: 10px;");
        } else {
            passwordMatchLabel.setText("✗ Passwords do not match");
            passwordMatchLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 10px;");
        }
    }

    // -------------------------------------------------------------------------
    // Registration handler
    // -------------------------------------------------------------------------

    @FXML
    public void handleRegister() {
        clearMessages();

        // Collect values
        String fullName    = fullNameField  != null ? fullNameField.getText().trim() : "";
        String username    = usernameField  != null ? usernameField.getText().trim() : "";
        String email       = emailField     != null ? emailField.getText().trim() : "";
        String phone       = phoneField     != null ? phoneField.getText().trim() : "";
        String roleDisplay = roleComboBox   != null ? roleComboBox.getValue() : null;
        String password    = passwordField  != null ? passwordField.getText() : "";
        String confirm     = confirmPasswordField != null ? confirmPasswordField.getText() : "";

        // ---- Client-side validation ----
        if (fullName.isEmpty()) { showError("Full name is required."); return; }
        if (username.isEmpty()) { showError("Username is required."); return; }
        if (username.length() < 3) { showError("Username must be at least 3 characters."); return; }
        if (!username.matches("[A-Za-z0-9_]+")) { showError("Username may only contain letters, digits, and underscores."); return; }
        if (email.isEmpty()) { showError("Email address is required."); return; }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) { showError("Please enter a valid email address."); return; }
        if (phone.isEmpty()) { showError("Phone number is required."); return; }
        if (!phone.matches("^[+]?[0-9\\s-]{7,15}$")) { showError("Please enter a valid phone number (7–15 digits)."); return; }
        if (roleDisplay == null || roleDisplay.isBlank()) { showError("Please select a role."); return; }
        if (password.isEmpty()) { showError("Password is required."); return; }
        if (password.length() < 8) { showError("Password must be at least 8 characters long."); return; }
        if (!password.equals(confirm)) { showError("Passwords do not match. Please re-enter them."); return; }

        // Map display name → UserRole enum
        UserRole role = mapDisplayToRole(roleDisplay);
        if (role == null) { showError("Invalid role selected."); return; }

        // ---- Submit to service / database ----
        try {
            authService.registerUser(fullName, username, email, phone, password, role);
            AlertUtil.showInfo("Registration Successful",
                    "✓ Account created successfully for " + fullName + " (" + role.name() + ")!\n\nClick OK to proceed to Sign In.");
            clearForm();
            Main.loadLoginScreen(username);
        } catch (IllegalArgumentException e) {
            // Duplicate username/email, validation errors from service layer
            showError(e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Registration error", e);
            showError("Registration failed: " + e.getMessage());
            AlertUtil.showError("Registration Failed", "Database connection or server error:\n" + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    @FXML
    public void goToLogin() {
        Main.loadLoginScreen();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Map the ComboBox display string to the UserRole enum value.
     * ADMIN is intentionally not in the map — it cannot be self-registered.
     */
    private UserRole mapDisplayToRole(String display) {
        if (display == null) return null;
        return switch (display) {
            case "Manager"        -> UserRole.MANAGER;
            case "Waiter / Server" -> UserRole.WAITER;
            case "Cashier"        -> UserRole.CASHIER;
            case "Kitchen Staff"  -> UserRole.KITCHEN_STAFF;
            default               -> null;
        };
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
        if (bottomErrorLabel != null) {
            bottomErrorLabel.setText(message);
            bottomErrorLabel.setVisible(true);
            bottomErrorLabel.setManaged(true);
        }
        if (successLabel != null) {
            successLabel.setVisible(false);
            successLabel.setManaged(false);
        }
    }

    private void showSuccess(String message) {
        if (successLabel != null) {
            successLabel.setText(message);
            successLabel.setVisible(true);
            successLabel.setManaged(true);
        }
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
        if (bottomErrorLabel != null) {
            bottomErrorLabel.setVisible(false);
            bottomErrorLabel.setManaged(false);
        }
    }

    private void clearMessages() {
        if (errorLabel != null) { errorLabel.setVisible(false); errorLabel.setManaged(false); }
        if (bottomErrorLabel != null) { bottomErrorLabel.setVisible(false); bottomErrorLabel.setManaged(false); }
        if (successLabel != null) { successLabel.setVisible(false); successLabel.setManaged(false); }
    }

    private void clearForm() {
        if (fullNameField  != null) fullNameField.clear();
        if (usernameField  != null) usernameField.clear();
        if (emailField     != null) emailField.clear();
        if (phoneField     != null) phoneField.clear();
        if (passwordField  != null) passwordField.clear();
        if (confirmPasswordField != null) confirmPasswordField.clear();
        if (roleComboBox   != null) roleComboBox.setValue(null);
        if (strengthLabel  != null) strengthLabel.setText("");
        if (passwordMatchLabel != null) passwordMatchLabel.setText("");
        Region[] bars = { strengthBar1, strengthBar2, strengthBar3, strengthBar4 };
        for (Region bar : bars) {
            if (bar != null) bar.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 2;");
        }
    }
}
