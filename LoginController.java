package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class LoginController {

    @FXML
    private Label WelcomeBackTo;

    @FXML
    private Label LamontaMenu;

    @FXML
    private Button loginButton; // example if you have a button

    // Example method for button action
    @FXML
    private void handleLogin() {
        System.out.println("Login button clicked");
    }
}
