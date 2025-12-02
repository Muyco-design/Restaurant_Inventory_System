package com.example;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class FoodMenu {

    private String currentUser;
    private String currentRole;

    @FXML
    private Button DashboardButton;

    @FXML
    private Button InventoryButton;

    @FXML
    private Button MenuButton;

    @FXML
    private AnchorPane MenuFoodList;

    @FXML
    private TextField SearchBarMenu;

    @FXML
    private Button goBack;

    @FXML
    private ImageView DashboardIcon; //house icon for dashboard

    // -------------------------------
    // Set user & role, hide dashboard for workers 
    // -------------------------------
    public void setCurrentUser(String user, String role) {
        this.currentUser = user;
        this.currentRole = role;

        boolean isWorker = "WORKER".equalsIgnoreCase(role);

        if (DashboardButton != null) {
            DashboardButton.setVisible(!isWorker);
            DashboardButton.setManaged(!isWorker);
        }

        if(DashboardIcon != null) {
            DashboardIcon.setVisible(!isWorker);
            DashboardIcon.setManaged(!isWorker);
        }
    }

    @FXML
    private void handleDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) MenuButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Menu.fxml"));
            Parent root = loader.load();

            Menucontroller controller = loader.getController();
            controller.setCurrentUser(currentUser, currentRole);

            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }


    @FXML
    private void handleInventoryButton(ActionEvent event) {
        try {
            Stage stage = (Stage) MenuButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Inventory.fxml"));
            Parent root = loader.load();

            InventoryController controller = loader.getController();
            controller.setCurrentUser(currentUser, currentRole);

            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
private void goBack() {
    try {
        Stage stage = (Stage) goBack.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/LoginPage.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        } catch (IOException e) {
        e.printStackTrace();
        }
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
