package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ItemRowController {

    @FXML private Button btnMinus;
    @FXML private Button btnPlus;
    @FXML private Label lblStock;
    @FXML private Label lblName;
    @FXML private Label lblDesc;
    @FXML private Label lblPrice;
    @FXML private ImageView foodImage;

    private int stock;
    private int mealId;
    private String imagePath;

    private InventoryController parentController;

    // REQUIRED so InventoryController can retrieve ID
    public int getMealId() {
        return mealId;
    }

    // REQUIRED for updating UI
    public void updateDisplayedStock() {
        lblStock.setText(String.valueOf(stock));
    }

    public void setParentController(InventoryController parent) {
        this.parentController = parent;
    }

    public void setData(int mealId, String name, String desc, int stock, double price, String imagePath) {
        this.mealId = mealId;
        this.stock = stock;
        this.imagePath = imagePath;

        lblName.setText(name);
        lblDesc.setText(desc);
        lblPrice.setText("₱ " + price);
        lblStock.setText(String.valueOf(stock));

        try {
            foodImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) {
            foodImage.setImage(new Image(getClass().getResourceAsStream("/Images/default.png")));
        }
    }

    @FXML
    private void increaseStock() {
        stock++;
        updateDisplayedStock();
        updateStockInDatabase();
    }

    @FXML
    private void decreaseStock() {
        if (stock > 0) {
            stock--;
            updateDisplayedStock();
            updateStockInDatabase();
        }
    }

    private void updateStockInDatabase() {
        String sql = "UPDATE meals SET amount_stock = ? WHERE meal_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, stock);
            pstmt.setInt(2, mealId);
            pstmt.executeUpdate();

            if (parentController != null) {
                parentController.refreshSingleRow(mealId, stock);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
