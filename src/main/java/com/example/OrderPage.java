package com.example;

import java.io.IOException;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class OrderPage {

  
    @FXML
    private Button DashboardButton;

    @FXML
    private ImageView Dashboardicon;

    @FXML
    private AnchorPane Inventory;

    @FXML
    private Button InventoryButton;

    @FXML
    private Button MenuButton;

    @FXML
    private ImageView ProfilePicture;

    @FXML
    private TextField SearchBarMenu;

    @FXML
    private Button confirmOrderButton;

    @FXML
    private Button goBack;

    @FXML
    private HBox logOffBox, DashBoardHBox, InventoryHBox, MenuHBox;

    @FXML
    private VBox orderContainer;

    @FXML
    private AnchorPane orderPanel;

    @FXML
    private ScrollPane orderScrollPane;

    @FXML
    private Label orderTitle;

    @FXML
    private HBox totalCont;

    @FXML
    private Label totalPriceLabel;

    // ===== User Info =====
    private String currentUser;
    private String currentRole;


    public void setCurrentUser(String user, String role) { //Hides dashboard for workers
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
        if (DashBoardHBox != null) {
            DashBoardHBox.setVisible(!isWorker);
            DashBoardHBox.setManaged(!isWorker);
        }
    }

    // ==================================================================================
    // / Navigation Button Handlers
    // ==================================================================================
    @FXML
    void goBack(ActionEvent event) {

        try {
            Stage stage = (Stage) goBack.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/LoginPage.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }

    }

    @FXML
    void handleDashboard(ActionEvent event) {

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
    void handleMenuButton(ActionEvent event) {
         try {
            Stage stage = (Stage) MenuButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/MenuFoodList.fxml"));
            Parent root = loader.load();
            FoodMenu controller = loader.getController();
            controller.setCurrentUser(currentUser, currentRole);
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }


    }

    @FXML
    private void handleInventoryButton(ActionEvent event) {
        try {
            Stage stage = (Stage) InventoryButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/Inventory.fxml"));
            Parent root = loader.load();

            InventoryController controller = loader.getController();
            controller.setCurrentUser(currentUser, currentRole);

            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
  

    //Plays Hover Animation on Buttons
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
