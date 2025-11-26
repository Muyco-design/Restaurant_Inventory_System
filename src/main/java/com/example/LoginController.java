package com.example;

import java.io.IOException;

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
    private ImageView BGmenu;

    @FXML
    private Label LamontaMenu;

    @FXML
    private Button LoginButton;

    @FXML
    private PasswordField Password;

    @FXML
    private Button SignupButton;

    @FXML
    private VBox TitleGr;

    @FXML
    private Label WelcomeBackTo;

    @FXML
    private TextField username;
    
    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String user = username.getText();
        String pass = Password.getText();

        if(user.equals("admin") && pass.equals("1234")) {
            Parent menuRoot = FXMLLoader.load(getClass().getResource("Menu.fxml"));
            Stage stage = (Stage) username.getScene().getWindow();
            stage.setScene(new Scene(menuRoot));
        } else {
            System.out.println("Invalid username or password!");
        }
    }
}
