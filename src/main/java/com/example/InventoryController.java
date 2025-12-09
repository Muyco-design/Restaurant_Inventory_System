package com.example;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InventoryController {

    private String currentUser;
    private String currentRole;

    @FXML private Button DashboardButton;
    @FXML private ImageView Dashboardicon;
    @FXML private Button InventoryButton;
    @FXML private Button MenuButton;
    @FXML private Button OrderButton;
    @FXML private TextField SearchBarMenu;

    @FXML private CheckBox VegInvButton;

    @FXML private Button filterAll;
    @FXML private Button filterBreakfast;
    @FXML private Button filterLunch;
    @FXML private Button filterDinner;

    @FXML private ListView<HBox> inventoryList;

    private String selectedCategory = "All";

    private final List<InventoryItem> masterList = new ArrayList<>();

    private static class InventoryItem {
        int mealId;
        String name;
        String desc;
        int stock;
        double price;
        String category;
        boolean vegetarian;
        String imagePath;

        InventoryItem(int mealId, String name, String desc, int stock,
                      double price, String category, boolean vegetarian, String imagePath) {
            this.mealId = mealId;
            this.name = name;
            this.desc = desc;
            this.stock = stock;
            this.price = price;
            this.category = category;
            this.vegetarian = vegetarian;
            this.imagePath = imagePath;
        }
    }

    public void setCurrentUser(String user, String role) {
        this.currentUser = user;
        this.currentRole = role;

        boolean isWorker = "employee".equalsIgnoreCase(role);

        DashboardButton.setVisible(!isWorker);
        DashboardButton.setManaged(!isWorker);

        Dashboardicon.setVisible(!isWorker);
        Dashboardicon.setManaged(!isWorker);
    }

    // --------------------
    // INITIALIZE
    // --------------------
    @FXML
    public void initialize() {
        loadMealsFromDatabase();
        applyFilters();

        SearchBarMenu.textProperty().addListener((a, b, c) -> applyFilters());
        VegInvButton.selectedProperty().addListener((a, b, c) -> applyFilters());
    }

    // --------------------
    // LOAD DATABASE
    // --------------------
    private void loadMealsFromDatabase() {
        masterList.clear();

        String sql = "SELECT * FROM meals";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                int id = rs.getInt("meal_id");
                String name = rs.getString("meal_name");
                String desc = rs.getString("description");
                int stock = rs.getInt("amount_stock");
                double price = rs.getDouble("price");
                int catId = rs.getInt("category_id");
                boolean veg = rs.getInt("is_vegetarian") == 1;

                String category = switch (catId) {
                    case 1 -> "Breakfast";
                    case 2 -> "Lunch";
                    case 3 -> "Dinner";
                    default -> "Other";
                };

                String imagePath = "/Images/" + name + ".png";

                masterList.add(new InventoryItem(id, name, desc, stock, price, category, veg, imagePath));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------
    // FILTER + SEARCH
    // --------------------
    private void applyFilters() {

        inventoryList.getItems().clear();

        String search = SearchBarMenu.getText() == null ? "" : SearchBarMenu.getText().toLowerCase();
        boolean vegOnly = VegInvButton.isSelected();

        for (InventoryItem item : masterList) {

            if (!selectedCategory.equals("All") &&
                !item.category.equalsIgnoreCase(selectedCategory)) continue;

            if (vegOnly && !item.vegetarian) continue;

            if (!item.name.toLowerCase().contains(search) &&
                !item.desc.toLowerCase().contains(search)) continue;

            addRow(item);
        }
    }

    // --------------------
    // ADD ROW
    // --------------------
    private void addRow(InventoryItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ItemRowInventoryPage.fxml"));
            HBox row = loader.load();

            ItemRowController controller = loader.getController();
            row.setUserData(controller);

            controller.setParentController(this);
            controller.setData(item.mealId, item.name, item.desc, item.stock, item.price, item.imagePath);

            inventoryList.getItems().add(row);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --------------------
    // LIVE UPDATE
    // --------------------
    public void refreshSingleRow(int mealId, int newStock) {
        Platform.runLater(() -> {
            for (HBox row : inventoryList.getItems()) {

                ItemRowController ctrl = (ItemRowController) row.getUserData();
                if (ctrl != null && ctrl.getMealId() == mealId) {

                    ctrl.updateDisplayedStock();

                    for (InventoryItem it : masterList)
                        if (it.mealId == mealId)
                            it.stock = newStock;

                    return;
                }
            }
        });
    }

    // --------------------
    // CATEGORY FILTER BUTTONS
    // --------------------
    @FXML private void filterAll() { selectedCategory = "All"; applyFilters(); }
    @FXML private void filterBreakfast() { selectedCategory = "Breakfast"; applyFilters(); }
    @FXML private void filterLunch() { selectedCategory = "Lunch"; applyFilters(); }
    @FXML private void filterDinner() { selectedCategory = "Dinner"; applyFilters(); }

    // --------------------
    // NAVIGATION BUTTONS
    // --------------------
    @FXML
    private void handleDashboard(ActionEvent event) {
        loadPage("/com/example/MenuDashboard.fxml", event);
    }

    @FXML
    private void handleMenuButton(ActionEvent event) {
        loadPage("/com/example/MenuFoodList.fxml", event);
    }

    @FXML
    private void handleOrderButton(ActionEvent event) {
        loadPage("/com/example/OrderPage.fxml", event);
    }

    @FXML
    private void goBack(ActionEvent event) {
        loadPage("/com/example/LoginPage.fxml", event);
    }

    // --------------------
    // UNIVERSAL NAVIGATION LOADER
    // --------------------
    private void loadPage(String fxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --------------------
    // HOVER ANIMATIONS
    // --------------------
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
