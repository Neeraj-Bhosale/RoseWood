package com.restaurant.controller;

import com.restaurant.Main;
import com.restaurant.exception.InvalidLoginException;
import com.restaurant.model.User;
import com.restaurant.service.AuthenticationService;
import com.restaurant.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the Login screen.
 * Handles credential validation, show/hide password toggle, demo autofill, and navigation.
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private Label errorLabel;

    private boolean passwordVisible = false;

    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    public void initialize() {
        // Clear error on startup
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setManaged(false);
            errorLabel.setVisible(false);
        }

        // Keep the visible text field in sync with the password field (two-way binding)
        if (passwordField != null && passwordVisibleField != null) {
            passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        }
    }

    /**
     * Toggle the password field between masked and unmasked display.
     */
    @FXML
    public void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordField != null && passwordVisibleField != null) {
            if (passwordVisible) {
                // Show plain text
                passwordVisibleField.setManaged(true);
                passwordVisibleField.setVisible(true);
                passwordField.setManaged(false);
                passwordField.setVisible(false);
            } else {
                // Show dots
                passwordField.setManaged(true);
                passwordField.setVisible(true);
                passwordVisibleField.setManaged(false);
                passwordVisibleField.setVisible(false);
            }
        }
    }

    /**
     * Attempt login using the entered credentials against the MySQL database.
     */
    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Clear prior error
        showError("");

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        try {
            User user = authService.login(username, password);
            if (user != null) {
                Main.loadMainDashboard();
            }
        } catch (InvalidLoginException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("System Error", "Database or connection error: " + e.getMessage());
        }
    }

    /**
     * Navigate to the staff registration screen.
     */
    @FXML
    public void goToRegister() {
        Main.loadRegisterScreen();
    }

    // -------------------------------------------------------------------------
    // Demo autofill helpers (shown as small compact buttons in the left panel)
    // -------------------------------------------------------------------------

    @FXML
    public void fillAdminDemo() {
        usernameField.setText("admin");
        passwordField.setText("admin123");
    }

    @FXML
    public void fillWaiterDemo() {
        usernameField.setText("waiter1");
        passwordField.setText("waiter123");
    }

    @FXML
    public void fillCashierDemo() {
        usernameField.setText("cashier1");
        passwordField.setText("cashier123");
    }

    @FXML
    public void fillChefDemo() {
        usernameField.setText("chef1");
        passwordField.setText("kitchen123");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void showError(String message) {
        if (errorLabel == null) return;
        if (message == null || message.isBlank()) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        } else {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }
}
