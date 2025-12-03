package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

import javafx.scene.input.MouseEvent;
import javafx.animation.*;

import java.io.IOException;


public class Menucontroller {

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
        if (DashboardHbox != null) {
            DashboardHbox.setVisible(!isWorker);
            DashboardHbox.setManaged(!isWorker);
        }
    }

    // ===== Dashboard Data =====
    private int totalRevenue = 0;
    private int totalCustomers = 0;
    private int totalOrders = 0;

    @FXML 
    private BarChart<String, Number> BarChart;

    @FXML 
    private Button DashboardButton, InventoryButton, MenuButton, goBack;
    @FXML 
    private HBox DashboardHbox;
    @FXML
    private HBox logOffBox;
    @FXML 
    private ImageView Dashboardicon, ProfilePicture; 

    @FXML
    private Label UserRole; // Displays user role (Manager/Worker)

    @FXML
    private Label Username; // Displays username

    //--------//
    // Least Selling Labels
    @FXML 
    private Label LeastSellingOne, LeastSellingTwo, LeastSellingThree;

    @FXML
    private Label LSO1, LSO2, LSO3;
    @FXML
    private Label LSE1, LSE2, LSE3;

    //--------//
    // Top Selling Labels
    @FXML 
    private Label TopSellingOne, TopSellingTwo, TopSellingThree;

    @FXML 
    private Label TSO1, TSO2, TSO3; //order amount
    @FXML 
    private Label TSE1, TSE2, TSE3; //earned
    @FXML 
    private Label TotalCostumersDB, TotalOrdersDB, TotalRevenueDB; 
    //---------//
    @FXML 
    private TextField SearchBarMenu; // Search Bar

    // ===== Week Buttons =====
    @FXML private Button Week1, Week2, Week3, Week4, LastMonth;

    
    

    // ===== Initialize =====
    @FXML
    private void initialize() {
        // Show default chart
        LastMonth();

        // Fade in chart
        FadeTransition fade = new FadeTransition(Duration.seconds(1.2), BarChart);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        updateDashboard();
    }

    // ===== Update BarChart =====
    private void updateBarChart(int[] values, String title, String[] label) {
        
        if (BarChart == null) {
            System.out.println("BarChart is not initialized.");
            return;
        }

        BarChart.getData().clear();
        BarChart.layout();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(title);

        

        for (int i = 0; i < values.length; i++) {
            series.getData().add(new XYChart.Data<>(label[i], values[i]));
        }

        BarChart.getData().add(series);
        BarChart.setAnimated(false);
        BarChart.layout(); // Ensure the chart updates and redraws
        
            

    }

    // ===== Week Button Handlers =====
    @FXML
    private void LastMonth() {
        System.out.println("Last Month button pressed!");
        int[] values = {823, 456, 1245, 1245};
        String[] label = {"Week 1","Week 2","Week 3","Week 4"}; 
        updateBarChart(values, "In the Last Month",label);
    }

    @FXML
    private void Week1() {
        System.out.println("Week 1 button pressed!");
        int[] values = {823, 0, 0, 0, 0, 0, 0};
        String[] label = {"Sunday","Monday","Tuesday","Wednesday","Thurday","Friday","Saturday"}; 
        updateBarChart(values, "Week 1",label);
    }

    @FXML
    private void Week2() {
         System.out.println("Week 2 button pressed!");
        int[] values = {0, 456, 0, 0, 0, 0, 0};
        String[] label = {"Sunday","Monday","Tuesday","Wednesday","Thurday","Friday","Saturday"}; 
        updateBarChart(values, "Week 2",label);
    }

    @FXML
    private void Week3() {
         System.out.println("Week 3 button pressed!");
        int[] values = {0, 0, 1245, 0, 0, 0, 0};
        String[] label = {"Sunday","Monday","Tuesday","Wednesday","Thurday","Friday","Saturday"}; 
        updateBarChart(values, "Week 3",label);
    }

    @FXML
    private void Week4() {
        System.out.println("Week 4 button pressed!");
        int[] values = {0, 0, 0, 1245, 0, 0, 0};
        String[] label = {"Sunday","Monday","Tuesday","Wednesday","Thurday","Friday","Saturday"}; 
        updateBarChart(values, "Week 4",label);
    }

    // ===== Dashboard Updates =====
    private void updateDashboard() {
        TotalRevenueDB.setText("₱ " + totalRevenue);
        TotalCostumersDB.setText(String.valueOf(totalCustomers));
        TotalOrdersDB.setText(String.valueOf(totalOrders));
    }

    public void addOrder(int amountPaid) {
        totalOrders++;
        totalRevenue += amountPaid;
        updateDashboard();
    }

    public void addCustomer() {
        totalCustomers++;
        updateDashboard();
    }

    // ===== Search Bar =====
    @FXML
    private void handleSearch() {
        String text = SearchBarMenu.getText();
        if (!text.isEmpty()) {
            System.out.println("Searching for: " + text);
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
            Stage stage = (Stage) InventoryButton.getScene().getWindow();
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
