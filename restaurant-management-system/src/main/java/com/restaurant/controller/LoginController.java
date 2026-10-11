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

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    public void initialize() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            User user = authService.login(username, password);
            if (user != null) {
                Main.loadMainDashboard();
            }
        } catch (InvalidLoginException e) {
            if (errorLabel != null) {
                errorLabel.setText(e.getMessage());
            } else {
                AlertUtil.showError("Login Failed", e.getMessage());
            }
        } catch (Exception e) {
            AlertUtil.showError("System Error", "Database or connection error: " + e.getMessage());
        }
    }

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
}
