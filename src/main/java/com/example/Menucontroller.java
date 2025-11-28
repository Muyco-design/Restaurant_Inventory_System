package com.example;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.util.Duration;

public class Menucontroller {

    // ===== USER INFO =====
    private String currentUser;
    private String currentRole;

    // ===== DASHBOARD DATA =====
    private int totalRevenue = 0;
    private int totalCustomers = 0;
    private int totalOrders = 0;

    // ===== FXML ELEMENTS =====
    @FXML
    private BarChart<String, Number> BarChart;

    @FXML
    private Button MenuButton;

        @FXML
    private Button DashboardButton;

    @FXML
    private Button InventoryButton;

    @FXML
    private TextField SearchBarMenu;

    @FXML
    private Label TotalCostumersDB;

    @FXML
    private Label TotalOrdersDB;

    @FXML
    private Label TotalRevenueDB;


    // ===== BACK BUTTON =====
    @FXML
    private void goBack() throws Exception {
        App.setRoot("LoginPage");
    }


    // ===== INITIALIZE PAGE =====
@FXML
private void initialize() {

    // ====== Setup chart data ======
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("In the Last Month");

    int[] values = {823, 456, 1245, 1245, 596, 800};
    String[] weeks = {"1st week", "2nd week", "3rd week", "4th week", "5th week", "6th week"};

    for (String week : weeks) {
        // start from a small value to show animation
        XYChart.Data<String, Number> data = new XYChart.Data<>(week, 0.1);
        series.getData().add(data);
    }

    BarChart.getData().add(series);

    // Apply CSS
    BarChart.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

    // ====== Animate bars with delay ======
    for (int i = 0; i < series.getData().size(); i++) {
        XYChart.Data<String, Number> data = series.getData().get(i);
        int finalValue = values[i];

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1.5),
                new KeyValue(data.YValueProperty(), finalValue, Interpolator.EASE_BOTH)
            )
        );

        timeline.setDelay(Duration.millis(i * 150));

        // Ensure animation runs after scene is rendered
        final Timeline tl = timeline;
        Platform.runLater(tl::play);
    }

    // ===== Update dashboard numbers at start =====
    updateDashboard();
}



    // ===== REFRESH LABELS =====
    private void updateDashboard() {
        TotalRevenueDB.setText("₱ " + totalRevenue);
        TotalCostumersDB.setText(String.valueOf(totalCustomers));
        TotalOrdersDB.setText(String.valueOf(totalOrders));
    }


    // ===== MODIFY VALUES =====
    public void addOrder(int amountPaid) {
        totalOrders++;
        totalRevenue += amountPaid;
        updateDashboard();
    }

    public void addCustomer() {
        totalCustomers++;
        updateDashboard();
    }


    // ===== SEARCH BAR =====
    @FXML
    private void handleSearch() {
        String text = SearchBarMenu.getText();

        if (text.isEmpty()) {
            System.out.println("Please enter a search.");
        } else {
            System.out.println("Searching for: " + text);
        }
    }


    // ===== USER ROLES =====
    public void setCurrentUser(String user, String role) {
        this.currentUser = user;
        this.currentRole = role;

        System.out.println("Current user: " + currentUser + ", Role: " + currentRole);

        if ("WORKER".equalsIgnoreCase(role)) {
            // Example: hide admin buttons here
            // MenuButton.setDisable(true);
        }
    }
}
