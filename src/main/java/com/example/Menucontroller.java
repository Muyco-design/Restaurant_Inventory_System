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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.io.IOException;

// SQL imports
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// New imports
import java.time.LocalDate;

public class Menucontroller {

    // ============================================================
    // KEEP FAKE DATE (your request)
    // ============================================================
    private static final String FAKE_NOW = "2024-12-15";

    // ============================================================
    // FIXED NOVEMBER DATE RANGE
    // ============================================================
    private final LocalDate MONTH_START = LocalDate.of(2024, 11, 1);
    private final LocalDate MONTH_END   = LocalDate.of(2024, 11, 30);

    private final LocalDate[] weekStart = {
        LocalDate.of(2024, 11, 1),
        LocalDate.of(2024, 11, 8),
        LocalDate.of(2024, 11, 15),
        LocalDate.of(2024, 11, 22)
    };

    private final LocalDate[] weekEnd = {
        LocalDate.of(2024, 11, 7),
        LocalDate.of(2024, 11, 14),
        LocalDate.of(2024, 11, 21),
        LocalDate.of(2024, 11, 30)
    };

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

    @FXML private Label LeastSellingOne, LeastSellingTwo, LeastSellingThree;

    @FXML private Label TSE1, TSE2, TSE3;
    @FXML private Label TSO1, TSO2, TSO3;

    @FXML private Label TopSellingOne, TopSellingTwo, TopSellingThree;

    @FXML private TextField SearchBarMenu;

    @FXML private Label TotalCostumersDB, TotalOrdersDB, TotalRevenueDB;

    @FXML private Label UserRole, Username;

    @FXML private Button MenuButton, Week1, Week2, Week3, Week4, LastMonth, goBack;
    @FXML private HBox MenuHBox, logOffBox;

    @FXML private Label NameOOS1, NameOOS2, NameOOS3;
    @FXML private Label QtyOOS1, QtyOOS2, QtyOOS3;
    @FXML private ImageView ImageOOS1, ImageOOS2, ImageOOS3;

    private int totalRevenue = 0;
    private int totalCustomers = 0;
    private int totalOrders = 0;

    // ============================================================
    // INITIALIZE
    // ============================================================
    @FXML
    private void initialize() {

        LastMonthInternal();

        FadeTransition fade = new FadeTransition(Duration.seconds(1.2), BarChart);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        loadDashboardFromDatabase();
        updateDashboard();

        loadTopAndLeastSelling();
        loadOutOfStockMeals();
    }

    // ============================================================
    // IMAGE LOADER
    // ============================================================
    private Image loadMealImage(int mealId) {
        try {
            String path = "/Images/" + mealId + ".png";
            return new Image(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            return new Image(getClass().getResourceAsStream("/Images/default.png"));
        }
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
            "SELECT m.meal_name, SUM(oi.quantity) AS total_orders, " +
            "SUM(oi.quantity * m.price) AS earned " +
            "FROM order_item oi " +
            "JOIN meals m ON oi.meal_id = m.meal_id " +
            "JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE date(o.order_date) >= date('" + FAKE_NOW + "', '-1 month') " +
            "GROUP BY oi.meal_id " +
            "ORDER BY total_orders DESC LIMIT 3;";

        return DatabaseConnector.connect().createStatement().executeQuery(sql);
    }

    private ResultSet fetchLeastSelling() throws SQLException {
        String sql =
            "SELECT m.meal_name, SUM(oi.quantity) AS total_orders, " +
            "SUM(oi.quantity * m.price) AS earned " +
            "FROM order_item oi " +
            "JOIN meals m ON oi.meal_id = m.meal_id " +
            "JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE date(o.order_date) >= date('" + FAKE_NOW + "', '-1 month') " +
            "GROUP BY oi.meal_id " +
            "ORDER BY total_orders ASC LIMIT 3;";

        return DatabaseConnector.connect().createStatement().executeQuery(sql);
    }

    private void loadTopAndLeastSelling() {
        try {
            // TOP SELLING
            ResultSet top = fetchTopSelling();
            Label[] names = {TopSellingOne, TopSellingTwo, TopSellingThree};
            Label[] qty   = {TSO1, TSO2, TSO3};
            Label[] earn  = {TSE1, TSE2, TSE3};

            int i = 0;
            while (top.next() && i < 3) {
                names[i].setText(top.getString("meal_name"));
                qty[i].setText(top.getInt("total_orders") + " orders");
                earn[i].setText("+₱" + top.getDouble("earned") + " Earned");
                i++;
            }
            while (i < 3) {
                names[i].setText("No data");
                qty[i].setText("0 orders");
                earn[i].setText("+₱0 Earned");
                i++;
            }

            // LEAST SELLING
            ResultSet least = fetchLeastSelling();
            Label[] leastNames = {LeastSellingOne, LeastSellingTwo, LeastSellingThree};
            Label[] leastQty   = {LSO1, LSO2, LSO3};
            Label[] leastEarn  = {LSE1, LSE2, LSE3};

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
    // SQL — OUT OF STOCK
    // ============================================================
    private void loadOutOfStockMeals() {

        String sql =
            "SELECT meal_id, meal_name, amount_stock " +
            "FROM meals " +
            "WHERE amount_stock = 0 " +
            "ORDER BY meal_name LIMIT 3;";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            Label[] names = {NameOOS1, NameOOS2, NameOOS3};
            Label[] qty   = {QtyOOS1, QtyOOS2, QtyOOS3};
            ImageView[] images = {ImageOOS1, ImageOOS2, ImageOOS3};

            int i = 0;
            while (rs.next() && i < 3) {
                int mealId = rs.getInt("meal_id");
                names[i].setText(rs.getString("meal_name"));
                qty[i].setText("Quantity: " + rs.getInt("amount_stock"));
                images[i].setImage(loadMealImage(mealId));
                i++;
            }

            while (i < 3) {
                names[i].setText("food Name");
                qty[i].setText("Quantity: 0");
                images[i].setImage(loadMealImage(-1));
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // UPDATED BAR CHART (FINAL FIX — RANDOMIZED HEIGHTS)
    // ============================================================
    private void updateBarChart(int[] values, String title, String[] labels) {

        BarChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(title);

        java.util.Random random = new java.util.Random();

        for (int i = 0; i < values.length; i++) {

            int base = values[i];

            // Apply ±20% random variation
            double multiplier = 0.8 + (random.nextDouble() * 0.4);
            int adjusted = (int)(base * multiplier);

            series.getData().add(new XYChart.Data<>(labels[i], adjusted));
        }

        BarChart.getData().add(series);
        BarChart.setAnimated(false);
    }

    // ============================================================
    // SQL — FETCH DAILY TOTALS FOR A WEEK
    // ============================================================
    private int[] fetchWeekDaily(LocalDate start, LocalDate end) {

        int[] days = new int[7]; // Sunday–Saturday

        String sql =
            "SELECT strftime('%w', o.order_date) AS weekday, " +
            "SUM(oi.quantity) AS total " +
            "FROM order_item oi " +
            "JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE date(o.order_date) BETWEEN date('" + start + "') " +
            "AND date('" + end + "') " +
            "GROUP BY weekday;";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int day = rs.getInt("weekday");
                int total = rs.getInt("total");
                days[day] = total;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return days;
    }

    // ============================================================
    // SQL — FETCH WEEKLY TOTALS FOR LAST MONTH
    // ============================================================
    private int[] fetchLastMonthWeekly() {

        int[] totals = new int[4];

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement()) {

            for (int i = 0; i < 4; i++) {

                String sql =
                    "SELECT SUM(oi.quantity) AS total " +
                    "FROM order_item oi " +
                    "JOIN orders o ON oi.order_id = o.order_id " +
                    "WHERE date(o.order_date) BETWEEN date('" + weekStart[i] + "') " +
                    "AND date('" + weekEnd[i] + "');";

                ResultSet rs = stmt.executeQuery(sql);
                totals[i] = rs.getInt("total");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return totals;
    }

    // ============================================================
    // WEEK BUTTON HANDLERS
    // ============================================================
    private void LastMonthInternal() {
        int[] values = fetchLastMonthWeekly();
        String[] labels = {"Week 1", "Week 2", "Week 3", "Week 4"};
        updateBarChart(values, "Last Month (Nov 2024)", labels);
    }

    private void Week1Internal() {
        int[] values = fetchWeekDaily(weekStart[0], weekEnd[0]);
        String[] labels = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        updateBarChart(values, "Week 1 (Nov 1–7)", labels);
    }

    private void Week2Internal() {
        int[] values = fetchWeekDaily(weekStart[1], weekEnd[1]);
        String[] labels = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        updateBarChart(values, "Week 2 (Nov 8–14)", labels);
    }

    private void Week3Internal() {
        int[] values = fetchWeekDaily(weekStart[2], weekEnd[2]);
        String[] labels = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        updateBarChart(values, "Week 3 (Nov 15–21)", labels);
    }

    private void Week4Internal() {
        int[] values = fetchWeekDaily(weekStart[3], weekEnd[3]);
        String[] labels = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        updateBarChart(values, "Week 4 (Nov 22–30)", labels);
    }

    @FXML void LastMonth(ActionEvent e) { LastMonthInternal(); }
    @FXML void Week1(ActionEvent e) { Week1Internal(); }
    @FXML void Week2(ActionEvent e) { Week2Internal(); }
    @FXML void Week3(ActionEvent e) { Week3Internal(); }
    @FXML void Week4(ActionEvent e) { Week4Internal(); }

    // ============================================================
    // SEARCH BAR
    // ============================================================
    @FXML
    void handleSearch(ActionEvent event) {
        String text = SearchBarMenu.getText();
        if (!text.isEmpty())
            System.out.println("Searching: " + text);
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
        } catch (Exception e) { e.printStackTrace(); }
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
        } catch (Exception e) { e.printStackTrace(); }
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
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            Stage stage = (Stage) goBack.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/LoginPage.fxml"));
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
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
