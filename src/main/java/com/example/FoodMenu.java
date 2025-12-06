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

import java.util.ArrayList;
import java.sql.*;

public class FoodMenu {

    // ============================================================
    // USER + ROLE (needed for login passing)
    // ============================================================
    private String currentUser;
    private String currentRole;

    public void setCurrentUser(String user, String role) {
        this.currentUser = user;
        this.currentRole = role;

        boolean isWorker = "employee".equalsIgnoreCase(role);

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
    // FETCHING FOOD DATA
    // ============================================================

    // Food class to hold data (you can create it as a separate class)
    class Food {
        String name;
        double price;
        String imagePath; // path of the image

        Food(String name, double price, String imagePath) {
            this.name = name;
            this.price = price;
            this.imagePath = imagePath;
        }
    }

    private ArrayList<Food> fetchFoodData() {
        ArrayList<Food> foodList = new ArrayList<>();
        String sql = "SELECT * FROM meals"; // Update based on your SQL schema

        try (Connection conn = DatabaseConnector.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("meal_name");
                double price = rs.getDouble("price");
                String imagePath = "/resources/Images/" + name + ".png";

                System.out.println("Food: " + name + ", Price: " + price + ", ImagePath: " + imagePath);
                foodList.add(new Food(name, price, imagePath));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return foodList;
    }

    // ============================================================
    // DISPLAY FOOD DATA
    // ============================================================
    private void displayFoodItems() { //FoodMenu.displayFoodItems
        ArrayList<Food> foodList = fetchFoodData();
        
        for (Food food : foodList) {
            VBox card = createFoodCard(food);
            MenuFlowPane.getChildren().add(card);
        }
    }

    private VBox createFoodCard(Food food) {
        VBox card = new VBox();
        card.setStyle("-fx-padding: 10; -fx-margin: 10; -fx-background-color: #f2f2f2; -fx-border-radius: 8;");
        card.setId("foodCard");

        Image img = null;
        try {
            String resourcePath = food.imagePath;
            System.out.println("Attempting to load image from: " + resourcePath);
            img = new Image(getClass().getResourceAsStream(resourcePath));

            if (img.isError()) {
                System.err.println("Error loading image: " + resourcePath);
                img = new Image(getClass().getResourceAsStream("/resources/Images/default.png"));
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Failed to load image: " + food.imagePath + ", using default.");
            img = new Image(getClass().getResourceAsStream("/resources/Images/default.png"));
        }

        ImageView imgView = new ImageView(img); // Load image
        imgView.setFitWidth(240);
        imgView.setFitHeight(280);

        Label nameLabel = new Label(food.name);
        Label priceLabel = new Label("₱" + food.price);

        card.getChildren().addAll(imgView, nameLabel, priceLabel);
        card.setOnMouseClicked(this::handleAddToOrder); // Attach event handler for ordering
        return card;
    }

    @FXML
    void initializeFoodMenu() {
        displayFoodItems();
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
