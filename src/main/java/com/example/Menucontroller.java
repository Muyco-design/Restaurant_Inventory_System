package com.example;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Menucontroller {

    // ===== User Info =====
    private String currentUser;
    private String currentRole;

    public void setCurrentUser(String user, String role) {
        this.currentUser = user;
        this.currentRole = role;

        boolean isWorker = "WORKER".equalsIgnoreCase(role);

        // Hide dashboard button if worker
        if (DashboardButton != null) {
            DashboardButton.setVisible(!isWorker);
            DashboardButton.setManaged(!isWorker);
        }

        if (Dashboardicon != null) {
            Dashboardicon.setVisible(!isWorker);
            Dashboardicon.setManaged(!isWorker);
        }

        if (DashboardHbox != null) {
            DashboardHbox.setVisible(!isWorker);
            DashboardHbox.setManaged(!isWorker);
        }

        // ===== SQL PLACEHOLDER =====
        // TODO: Fetch user-specific data from SQL database here (profile, role-specific settings)
    }

    // ===== Dashboard Data =====
    private int totalRevenue = 0;
    private int totalCustomers = 0;
    private int totalOrders = 0;

    @FXML private BarChart<String, Number> BarChart;
    @FXML private Button DashboardButton;
    @FXML private HBox DashboardHbox;
    @FXML private ImageView Dashboardicon;
    @FXML private Button InventoryButton;
    @FXML private Button MenuButton;
    @FXML private Label LeastSellingOne;
    @FXML private Label LeastSellingTwo;
    @FXML private Label LeastSellingThree;
    @FXML private Label TopSellingOne;
    @FXML private Label TopSellingTwo;
    @FXML private Label TopSellingThree;
    @FXML private Label TSO1;
    @FXML private Label TSO2;
    @FXML private Label TSO3;
    @FXML private Label TSE1;
    @FXML private Label TSE2;
    @FXML private Label TSE3;
    @FXML private Label TotalCostumersDB;
    @FXML private Label TotalOrdersDB;
    @FXML private Label TotalRevenueDB;
    @FXML private TextField SearchBarMenu;
    @FXML private ImageView ProfilePicture;
    @FXML private Button goBack;

    @FXML
    private void initialize() {
        // ===== BarChart Setup =====
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("In the Last Month");

        int[] values = {823, 456, 1245, 1245, 596, 800};
        String[] weeks = {"1st week", "2nd week", "3rd week", "4th week", "5th week", "6th week"};

        for (String week : weeks) {
            series.getData().add(new XYChart.Data<>(week, 0.1));
        }

        BarChart.getData().add(series);

        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<String, Number> data = series.getData().get(i);
            int finalValue = values[i];

            Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1.5),
                    new KeyValue(data.YValueProperty(), finalValue, Interpolator.EASE_BOTH)
                )
            );
            timeline.setDelay(Duration.millis(i * 150));
            final Timeline tl = timeline;
            Platform.runLater(tl::play);
        }

        // ===== SQL PLACEHOLDER =====
        // TODO: Load BarChart data dynamically from SQL database

        updateDashboard();
    }

    private void updateDashboard() {
        TotalRevenueDB.setText("₱ " + totalRevenue);
        TotalCostumersDB.setText(String.valueOf(totalCustomers));
        TotalOrdersDB.setText(String.valueOf(totalOrders));

        // ===== SQL PLACEHOLDER =====
        // TODO: Fetch updated totals from SQL database instead of static counters
    }

    public void addOrder(int amountPaid) {
        totalOrders++;
        totalRevenue += amountPaid;
        updateDashboard();

        // ===== SQL PLACEHOLDER =====
        // TODO: Insert new order into SQL database
    }

    public void addCustomer() {
        totalCustomers++;
        updateDashboard();

        // ===== SQL PLACEHOLDER =====
        // TODO: Insert new customer into SQL database
    }

    // ===== Search Bar =====
    @FXML
    private void handleSearch() {
        String text = SearchBarMenu.getText();
        if (text.isEmpty()) {
            System.out.println("Please enter a search term.");
        } else {
            System.out.println("Searching for: " + text);

            // ===== SQL PLACEHOLDER =====
            // TODO: Perform SQL query to fetch search results and update UI labels/images
        }
    }

    // ===== Navigation Methods =====
    @FXML
    private void handleDashboard() {
        try {
            Stage stage = (Stage) DashboardButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/MenuDashboard.fxml"));
            Parent root = loader.load();

            Menucontroller controller = loader.getController();
            controller.setCurrentUser(currentUser, currentRole);

            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleMenuButton(ActionEvent event) {
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

    // ===== SQL CONNECTION PLACEHOLDER =====
    // TODO: Add a method to establish SQL connection (Connection object)
    // Example:
    // private Connection connectDatabase() { ... }
}
