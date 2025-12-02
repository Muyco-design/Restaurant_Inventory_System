package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.control.ListView;
import javafx.scene.control.CheckBox;

public class InventoryController {

    private String currentUser;
    private String currentRole;

    @FXML private Button DashboardButton;
    @FXML private ImageView Dashboardicon;
    @FXML private Button InventoryButton;
    @FXML private Button MenuButton;
    @FXML private AnchorPane Inventory;
    @FXML private TextField SearchBarMenu;
    @FXML private ImageView ProfilePicture;
    @FXML private Button goBack;

    @FXML private CheckBox VegInvButton;
    @FXML private Button filterAll;
    @FXML private Button filterBreakfast;
    @FXML private Button filterLunch;
    @FXML private Button filterDinner;

    @FXML private ListView<HBox> inventoryList;
    @FXML private VBox ItemContainer;

    // ---- NEW ----
    private String selectedCategory = "All";          // Stores the active filter category
    private List<InventoryItem> masterList = new ArrayList<>();  // Full item list for filtering


    // ======================================================
    // USER INITIALIZATION
    // ======================================================
    public void setCurrentUser(String user, String role) {
        this.currentUser = user;
        this.currentRole = role;

        boolean isWorker = "WORKER".equalsIgnoreCase(role);

        DashboardButton.setVisible(!isWorker);
        DashboardButton.setManaged(!isWorker);
        Dashboardicon.setVisible(!isWorker);
        Dashboardicon.setManaged(!isWorker);
    }


    // ======================================================
    // INITIALIZE
    // ======================================================
    @FXML
    public void initialize() {

        loadPlaceholderData();  // loads into masterList
        applyFilters();         // populates ListView

        // live search
        SearchBarMenu.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
    }

    
    // ======================================================
    // DATA STRUCTURE (internal)
    // ======================================================
    private static class InventoryItem {
        String name;
        String desc;
        int stock;
        double price;
        String imagePath;
        String category;
        boolean vegetarian;

        InventoryItem(String name, String desc, int stock, double price,
                      String imagePath, String category, boolean vegetarian) {
            this.name = name;
            this.desc = desc;
            this.stock = stock;
            this.price = price;
            this.imagePath = imagePath;
            this.category = category;
            this.vegetarian = vegetarian;
        }
    }


    // ======================================================
    // LOAD PLACEHOLDER ITEMS
    // ======================================================
    private void loadPlaceholderData() {
        masterList.clear();

        masterList.add(new InventoryItem(
                "Tapsilog", "Beef tapa with garlic rice",
                150, 95, "/Images/Filipino Beef Tapa for an Easy and Hearty Breakfast.png",
                "Breakfast", false
        ));

        masterList.add(new InventoryItem(
                "Chicken Adobo", "Soy-vinegar chicken stew",
                180, 120, "/Images/Filipino Beef Tapa for an Easy and Hearty Breakfast.png",
                "Lunch", false
        ));

        masterList.add(new InventoryItem(
                "Champorado", "Chocolate rice porridge",
                200, 50, "/Images/Filipino Beef Tapa for an Easy and Hearty Breakfast.png",
                "Breakfast", true
        ));
    }


    // ======================================================
    // APPLY FILTERS (ALL / CATEGORY / VEGETARIAN / SEARCH)
    // ======================================================
    private void applyFilters() {

        inventoryList.getItems().clear();

        String search = SearchBarMenu.getText().toLowerCase();
        boolean vegOnly = VegInvButton.isSelected();

        for (InventoryItem item : masterList) {

            // category filter
            if (!selectedCategory.equals("All") &&
                !item.category.equalsIgnoreCase(selectedCategory)) {
                continue;
            }

            // vegetarian filter
            if (vegOnly && !item.vegetarian) {
                continue;
            }

            // search filter
            if (!item.name.toLowerCase().contains(search) &&
                !item.desc.toLowerCase().contains(search)) {
                continue;
            }

            addRow(item);
        }
    }


    // ======================================================
    // ADD ROW TO LISTVIEW
    // ======================================================
    private void addRow(InventoryItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ItemRowInventoryPage.fxml"));
            HBox row = loader.load();

            ItemRowController controller = loader.getController();
            controller.setData(item.name, item.desc, item.stock, item.price, item.imagePath);

            inventoryList.getItems().add(row);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ======================================================
    // FILTER BUTTON HANDLERS
    // ======================================================
    @FXML
    private void filterAll() {
        selectedCategory = "All";
        applyFilters();
    }

    @FXML
    private void filterBreakfast() {
        selectedCategory = "Breakfast";
        applyFilters();
    }

    @FXML
    private void filterLunch() {
        selectedCategory = "Lunch";
        applyFilters();
    }

    @FXML
    private void filterDinner() {
        selectedCategory = "Dinner";
        applyFilters();
    }

    @FXML
    private void filterVegetarian() {
        applyFilters();
    }


    // ======================================================
    // NAVIGATION
    // ======================================================
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
        } catch (IOException e) { e.printStackTrace(); }
    }
}
