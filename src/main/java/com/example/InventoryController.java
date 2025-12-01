package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;  // <-- add this
import java.io.IOException;


import javafx.fxml.FXMLLoader;   
import javafx.scene.Parent;      
import javafx.scene.Scene; 
import javafx.scene.image.ImageView;  

public class InventoryController {

    private String currentUser;
    private String currentRole;

    @FXML
    private Button DashboardButton;

    @FXML
    private ImageView Dashboardicon;

    @FXML
    private Button InventoryButton;

    @FXML
    private Button MenuButton;

    @FXML
    private AnchorPane Inventory;

    @FXML
    private TextField SearchBarMenu;

    @FXML
    private Button goBack;

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
        if (Dashboardicon != null) {
            Dashboardicon.setVisible(!isWorker);
            Dashboardicon.setManaged(!isWorker);
        }

      
    }

    // ---------------- NAVIGATION ---------------- //

    @FXML
    private void handleDashboard() {
        try {
            Stage stage = (Stage) DashboardButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Menu.fxml"));
            Parent root = loader.load();

            Menucontroller controller = loader.getController();
            controller.setCurrentUser(currentUser, currentRole);

            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleMenuButton() {
        try {
            Stage stage = (Stage) InventoryButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/MenuFoodList.fxml"));
            Parent root = loader.load();

            FoodMenu controller = loader.getController();
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

}
