package com.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

public class LoginController {


    @FXML
    private TextField username;

    @FXML
    private PasswordField Password;

    @FXML
    private Button LoginButton;

    @FXML
    private Button SignupButton;

    // simple user database
    private static Map<String, String> users = new HashMap<>(); // username -> password
    private static Map<String, String> roles = new HashMap<>(); // username -> role

    static { // ensures any new instance of LoginController or MenuController can access the same users/roles.
        // Sample Default users
        users.put("manager", "admin123"); //user: manager, pass: admin123
        roles.put("manager", "MANAGER"); 

        users.put("worker", "worker123");
        roles.put("worker", "WORKER");
    }

@FXML
private void handleLogin(ActionEvent event) throws IOException { 
    String user = username.getText();
    String pass = Password.getText();

    if(users.containsKey(user) && users.get(user).equals(pass)) { // valid login
        String role = roles.get(user); // get user role
        System.out.println("Login successful! User role: " + role); // for debugging

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Menu.fxml")); // load Menu.fxml
        Parent menuRoot = loader.load(); // actually load the FXML

        // Pass user info to Menucontroller
        Menucontroller menuController = loader.getController(); 
        menuController.setCurrentUser(user, role);

        Stage stage = (Stage) username.getScene().getWindow();
        stage.setScene(new Scene(menuRoot));
    } else {
        System.out.println("Invalid username or password!"); // for debugging
    }
}


    // Sign Up button
    @FXML
    private void handleSignup(ActionEvent event) {
        String user = username.getText();
        String pass = Password.getText();

        if(user.isEmpty() || pass.isEmpty()) {
            System.out.println("Please enter a username and password.");
            return;
        }

        if(users.containsKey(user)) {
            System.out.println("Username already exists! Choose another.");
        } else {
            users.put(user, pass);
            roles.put(user, "WORKER"); // New users default to WORKER
            System.out.println("Sign up successful! You can now login as: " + user);
        }
    }
}
