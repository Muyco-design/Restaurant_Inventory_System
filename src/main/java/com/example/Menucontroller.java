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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.io.IOException;

// SQL
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Menucontroller {

    // ============================================================
    // FAKE DATE TO MAKE NOV 2024 DATA "RECENT"
    // ============================================================
    private static final String FAKE_NOW = "2024-12-15";


    // ============================================================
    // USER INFO
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
        if (Dashboardicon != null) {
            Dashboardicon.setVisible(!isWorker);
            Dashboardicon.setManaged(!isWorker);
        }
        if (DashboardHbox != null) {
            DashboardHbox.setVisible(!isWorker);
            DashboardHbox.setManaged(!isWorker);
        }
    }

    // ============================================================
    // FXML COMPONENTS
    // ============================================================

    @FXML private BarChart<String, Number> BarChart;

    @FXML private Button DashboardButton;
    @FXML private HBox DashboardHbox;
    @FXML private ImageView Dashboardicon;

    @FXML private Button InventoryButton;
    @FXML private HBox InventoryHbox;

    @FXML private Label LSE1, LSE2, LSE3;
    @FXML private Label LSO1, LSO2, LSO3;

    @FXML private Button LastMonth;
    @FXML private Label LeastSellingOne, LeastSellingTwo, LeastSellingThree;

    @FXML private Button MenuButton;
    @FXML private HBox MenuHBox;

    @FXML private ImageView ProfilePicture;

    @FXML private TextField SearchBarMenu;

    @FXML private Label TSE1, TSE2, TSE3;
    @FXML private Label TSO1, TSO2, TSO3;

    @FXML private Label TopSellingOne, TopSellingTwo, TopSellingThree;

    @FXML private Label TotalCostumersDB;
    @FXML private Label TotalOrdersDB;
    @FXML private Label TotalRevenueDB;

    @FXML private Label UserRole;
    @FXML private Label Username;

    @FXML private Button Week1, Week2, Week3, Week4;

    @FXML private Button goBack;
    @FXML private HBox logOffBox;

    @FXML private Label NameOOS1, NameOOS2, NameOOS3;

    @FXML private Label QtyOOS1, QtyOOS2, QtyOOS3;

    @FXML private ImageView ImageOOS1, ImageOOS2, ImageOOS3;
    // ============================================================
    // LOCAL VARIABLES
    // ============================================================
    private int totalRevenue = 0;
    private int totalCustomers = 0;
    private int totalOrders = 0;


    // ============================================================
    // INITIALIZE
    // ============================================================
    @FXML
    private void initialize() {

        LastMonthInternal(); // BarChart default

        FadeTransition fade = new FadeTransition(Duration.seconds(1.2), BarChart);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        loadDashboardFromDatabase();
        updateDashboard();

        loadTopAndLeastSelling();
    }


    // ============================================================
    // SQL — DASHBOARD TOTALS
    // ============================================================

    private int fetchTotalCustomers() {
        String sql = "SELECT COUNT(DISTINCT order_id) AS total FROM orders;";

        try (Connection conn = DatabaseConnector.connect();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            return rs.getInt("total");
        } catch (Exception e) { e.printStackTrace(); }

        return 0;
    }

    private int fetchTotalOrders() {
        String sql = "SELECT IFNULL(SUM(quantity), 0) AS total FROM order_item;";
        try (Connection conn = DatabaseConnector.connect();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            return rs.getInt("total");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private double fetchTotalRevenue() {
        String sql =
            "SELECT IFNULL(SUM(oi.quantity * m.price), 0) AS total " +
            "FROM order_item oi " +
            "JOIN meals m ON oi.meal_id = m.meal_id;";

        try (Connection conn = DatabaseConnector.connect();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            return rs.getDouble("total");
        } catch (Exception e) { e.printStackTrace(); }

        return 0.0;
    }

    private void loadDashboardFromDatabase() {
        totalCustomers = fetchTotalCustomers();
        totalOrders = fetchTotalOrders();
        totalRevenue = (int) fetchTotalRevenue();
    }

    private void updateDashboard() {
        TotalRevenueDB.setText("₱ " + totalRevenue);
        TotalCostumersDB.setText(String.valueOf(totalCustomers));
        TotalOrdersDB.setText(String.valueOf(totalOrders));
    }


    // ============================================================
    // SQL — TOP & LEAST SELLING
    // ============================================================

    private ResultSet fetchTopSelling() throws SQLException {
        String sql =
            "SELECT m.meal_name, " +
            "SUM(oi.quantity) AS total_orders, " +
            "SUM(oi.quantity * m.price) AS earned " +
            "FROM order_item oi " +
            "JOIN meals m ON oi.meal_id = m.meal_id " +
            "JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE date(o.order_date) >= date('" + FAKE_NOW + "', '-1 month') " +
            "GROUP BY oi.meal_id " +
            "ORDER BY total_orders DESC " +
            "LIMIT 3;";

        Connection conn = DatabaseConnector.connect();
        return conn.createStatement().executeQuery(sql);
    }

    private ResultSet fetchLeastSelling() throws SQLException {
        String sql =
            "SELECT m.meal_name, " +
            "SUM(oi.quantity) AS total_orders, " +
            "SUM(oi.quantity * m.price) AS earned " +
            "FROM order_item oi " +
            "JOIN meals m ON oi.meal_id = m.meal_id " +
            "JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE date(o.order_date) >= date('" + FAKE_NOW + "', '-1 month') " +
            "GROUP BY oi.meal_id " +
            "ORDER BY total_orders ASC " +
            "LIMIT 3;";

        Connection conn = DatabaseConnector.connect();
        return conn.createStatement().executeQuery(sql);
        
    }

    private void loadTopAndLeastSelling() {
        try {
            // TOP
            ResultSet top = fetchTopSelling();

            Label[] topNames = {TopSellingOne, TopSellingTwo, TopSellingThree};
            Label[] topQty = {TSO1, TSO2, TSO3};
            Label[] topEarn = {TSE1, TSE2, TSE3};

            int i = 0;
            while (top.next() && i < 3) {
                topNames[i].setText(top.getString("meal_name"));
                topQty[i].setText(top.getInt("total_orders") + " orders");
                topEarn[i].setText("+₱" + top.getDouble("earned") + " Earned");
                i++;
            }
            while (i < 3) {
                topNames[i].setText("No data");
                topQty[i].setText("0 orders");
                topEarn[i].setText("+₱0 Earned");
                i++;
            }

            // LEAST
            ResultSet least = fetchLeastSelling();
            Label[] leastNames = {LeastSellingOne, LeastSellingTwo, LeastSellingThree};
            Label[] leastQty = {LSO1, LSO2, LSO3};
            Label[] leastEarn = {LSE1, LSE2, LSE3};

            i = 0;
            while (least.next() && i < 3) {
                leastNames[i].setText(least.getString("meal_name"));
                leastQty[i].setText(least.getInt("total_orders") + " orders");
                leastEarn[i].setText("+₱" + least.getDouble("earned") + " Earned");
                i++;
            }
            while (i < 3) {
                leastNames[i].setText("No data");
                leastQty[i].setText("0 orders");
                leastEarn[i].setText("+₱0 Earned");
                i++;
            }

        } catch (Exception e) { e.printStackTrace(); }
    }


    // ============================================================
    // BAR CHART
    // ============================================================
    private void updateBarChart(int[] values, String title, String[] labels) {

        BarChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(title);

        for (int i = 0; i < values.length; i++) {
            series.getData().add(new XYChart.Data<>(labels[i], values[i]));
        }

        BarChart.getData().add(series);
        BarChart.setAnimated(false);
    }


    // ============================================================
    // WEEK BUTTONS
    // ============================================================

    private void LastMonthInternal() {
        int[] v = {823, 456, 1245, 1245};
        String[] l = {"Week 1", "Week 2", "Week 3", "Week 4"};
        updateBarChart(v, "In the Last Month", l);
    }
    private void Week1Internal() {
        int[] v = {823,0,0,0,0,0,0};
        String[] l = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        updateBarChart(v, "Week 1", l);
    }
    private void Week2Internal() {
        int[] v = {0,456,0,0,0,0,0};
        String[] l = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        updateBarChart(v, "Week 2", l);
    }
    private void Week3Internal() {
        int[] v = {0,0,1245,0,0,0,0};
        String[] l = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        updateBarChart(v, "Week 3", l);
    }
    private void Week4Internal() {
        int[] v = {0,0,0,1245,0,0,0};
        String[] l = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        updateBarChart(v, "Week 4", l);
    }

    @FXML void LastMonth(ActionEvent e) { LastMonthInternal(); }
    @FXML void Week1(ActionEvent e) { Week1Internal(); }
    @FXML void Week2(ActionEvent e) { Week2Internal(); }
    @FXML void Week3(ActionEvent e) { Week3Internal(); }
    @FXML void Week4(ActionEvent e) { Week4Internal(); }


    // ============================================================
    // SEARCH
    // ============================================================

    @FXML
    void handleSearch(ActionEvent event) {
        String text = SearchBarMenu.getText();
        if (!text.isEmpty())
            System.out.println("Searching for: " + text);
    }


    // ============================================================
    // NAVIGATION
    // ============================================================

    @FXML
    void handleDashboard(ActionEvent event) {
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
    void handleMenuButton(ActionEvent event) {
        try {
            Stage stage = (Stage) MenuButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/MenuFoodList.fxml"));
            Parent root = loader.load();

            FoodMenu controller = loader.getController();
            controller.setCurrentUser(currentUser, currentRole);
            controller.initializeFoodMenu();
            
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    void handleInventoryButton(ActionEvent event) {
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
    void goBack(ActionEvent event) {
        try {
            Stage stage = (Stage) goBack.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/LoginPage.fxml"));
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }


    // ============================================================
    // HOVER ANIMATION
    // ============================================================

    @FXML
    void handleButtonHover(MouseEvent event) {

        Node node = (Node) event.getSource();
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);

        if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
            node.setStyle("-fx-cursor: hand;");
            st.setToX(1.1);
            st.setToY(1.1);
        } else {
            st.setToX(1.0);
            st.setToY(1.0);
        }

        st.play();
    }
}
