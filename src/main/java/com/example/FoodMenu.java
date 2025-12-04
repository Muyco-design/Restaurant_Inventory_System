package com.example;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class FoodMenu {

    // ============================================================
    // USER + ROLE (needed for login passing)
    // ============================================================
    private String currentUser;
    private String currentRole;

    public void setCurrentUser(String user, String role) {
        this.currentUser = user;
        this.currentRole = role;

        boolean isWorker = "WORKER".equalsIgnoreCase(role);

        if (DashboardButton != null) {
            DashboardButton.setVisible(!isWorker);
            DashboardButton.setManaged(!isWorker);
        }
        if (DashboardIcon != null) {
            DashboardIcon.setVisible(!isWorker);
            DashboardIcon.setManaged(!isWorker);
        }
    }

    // ============================================================
    // FXML UI ELEMENTS
    // ============================================================

    // Navigation
    @FXML private Button DashboardButton;
    @FXML private ImageView DashboardIcon;
    @FXML private Button InventoryButton;
    @FXML private Button MenuButton;
    @FXML private Button goBack;
    @FXML private Button OrderButton;

    // Menu list / food cards
    @FXML private FlowPane MenuFlowPane;
    @FXML private ScrollPane MenuListPane;

    @FXML private ImageView FoodImage;
    @FXML private Label foodNameLabel;
    @FXML private Label foodPriceLabel;

    @FXML private TextField SearchBarMenu;
    @FXML private CheckBox VegInvButton;

    @FXML private Button filterAll;
    @FXML private Button filterBreakfast;
    @FXML private Button filterLunch;
    @FXML private Button filterDinner;

    @FXML private HBox logOffBox;

    // ORDER PANEL ELEMENTS
    @FXML private VBox orderListVBox;       // dynamic list
    @FXML private Label orderTotalLabel;    // total string label

    private double total = 0;


    // ============================================================
    // NAVIGATION
    // ============================================================

    @FXML
    void goBack(ActionEvent event) {
        loadPage("/com/example/LoginPage.fxml", event);
    }

    @FXML
    void handleDashboard(ActionEvent event) {
        loadPage("/com/example/Menu.fxml", event);
    }

    @FXML
    void handleInventoryButton(ActionEvent event) {
        loadPage("/com/example/Inventory.fxml", event);
    }

    @FXML
    void handleOrderButton(ActionEvent event) {
        loadPage("/com/example/OrderPage.fxml", event);
    }


    /** Universal navigation loader */
    private void loadPage(String fxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            // Pass user + role to next page (if supported)
            try {
                Object controller = loader.getController();
                controller.getClass().getMethod("setCurrentUser", String.class, String.class)
                        .invoke(controller, currentUser, currentRole);
            } catch (Exception ignored) {}

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ============================================================
    // ADD ITEM TO ORDER LIST
    // ============================================================

    @FXML
    void handleAddToOrder(MouseEvent event) {
        VBox card = (VBox) event.getSource();

        Label nameLabel = (Label) card.lookup("#foodNameLabel");
        Label priceLabel = (Label) card.lookup("#foodPriceLabel");
        ImageView imgView = (ImageView) card.lookup("#FoodImage");

        if (nameLabel == null || priceLabel == null || imgView == null) {
            System.out.println("ERROR: Some FXIDs not found inside card!");
            return;
        }

        String name = nameLabel.getText();
        double price = Double.parseDouble(priceLabel.getText().replace("₱", "").trim());
        Image img = imgView.getImage();

        addItemToOrder(name, price, img);
    }


    private void addItemToOrder(String name, double price, Image img) {

        HBox row = new HBox(10);
        row.setStyle("-fx-padding: 10; -fx-background-color: #FFFFFF; -fx-background-radius: 8;");

        // Thumbnail
        ImageView thumb = new ImageView(img);
        thumb.setFitWidth(45);
        thumb.setFitHeight(45);

        // Name + qty + price
        VBox info = new VBox(2);
        Label nameLabel = new Label(name);
        Label qty = new Label("x1");
        Label priceLabel = new Label("₱" + (int) price);
        info.getChildren().addAll(nameLabel, qty, priceLabel);

        // Quantity buttons
        Button minus = new Button("-");
        Button plus = new Button("+");

        row.getChildren().addAll(thumb, info, minus, plus);
        orderListVBox.getChildren().add(row);

        // Update total
        total += price;
        orderTotalLabel.setText("₱" + (int) total);
    }


    // ============================================================
    // HOVER ANIMATION
    // ============================================================

    @FXML
    void handleButtonHover(MouseEvent event) {
        Node node = (Node) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(120), node);

        if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
            node.setStyle("-fx-cursor: hand;");
            st.setToX(1.07);
            st.setToY(1.07);
        } else {
            st.setToX(1.0);
            st.setToY(1.0);
        }

        st.play();
    }
}
