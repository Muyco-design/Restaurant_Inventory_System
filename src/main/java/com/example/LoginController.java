package com.example;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.animation.*;
import javafx.scene.Node;

import java.io.IOException;

public class LoginController {

    @FXML
    private ImageView BGmenu;

    @FXML
    private Button LoginButton;

    @FXML
    private PasswordField Password;

    @FXML
    private Button SignupButton;

    @FXML
    private Label alertMessage;

    @FXML
    private TextField username;

    @FXML
    void handleLogin(ActionEvent event) {
        String user = username.getText();
        String pass = Password.getText();

        try {
            Stage stage = (Stage) username.getScene().getWindow();
            FXMLLoader loader;
            Parent root;

            // Hardcoded login for testing
            if ("manager".equals(user) && "admin123".equals(pass)) {
                loader = new FXMLLoader(getClass().getResource("/com/example/Menu.fxml"));
                root = loader.load();

                Menucontroller controller = loader.getController();
                controller.setCurrentUser(user, "MANAGER");

                stage.setScene(new Scene(root));
            } 
            else if ("worker".equals(user) && "worker123".equals(pass)) {
                loader = new FXMLLoader(getClass().getResource("/com/example/MenuFoodList.fxml"));
                root = loader.load();

                FoodMenu controller = loader.getController();
                controller.setCurrentUser(user, "WORKER");

                stage.setScene(new Scene(root));
            } 
            else {
                showAlert("Invalid username or password!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSignup(ActionEvent event) {

        System.out.println("Signup button clicked! (Not implemented yet)");
        
    }

    private void showAlert(String message) {
        alertMessage.setText(message);
        FadeTransition ft = new FadeTransition(Duration.millis(3000), alertMessage);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
    }

@FXML
void handleButtonHover(MouseEvent event) {
    Node button = (Node) event.getSource(); // Get the button that fired the event
    ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
    
    if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
        button.setStyle("-fx-cursor: hand;");
        st.setToX(1.1); // Scale X to 110%
        st.setToY(1.1); // Scale Y to 110%
    } else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
        st.setToX(1.0); // Scale back to original X
        st.setToY(1.0); // Scale back to original Y
    }
    
    st.play();
}

}
