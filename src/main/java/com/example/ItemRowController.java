

package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemRowController {

    @FXML private Button btnMinus;
    @FXML private Button btnPlus;
    @FXML private Label lblStock;
    @FXML private Label lblName;
    @FXML private Label lblDesc;
    @FXML private Label lblPrice;
    @FXML private ImageView foodImage;

    private int stock = 0;

    public void setData(String name, String desc, int stock, double price, String imgPath) {
        lblName.setText(name);
        lblDesc.setText(desc);
        lblPrice.setText("₱ " + price);
        lblStock.setText(String.valueOf(stock));
        this.stock = stock;

        foodImage.setImage(new Image(getClass().getResourceAsStream(imgPath)));
    }

    @FXML
    private void increaseStock() {
        stock++;
        lblStock.setText(String.valueOf(stock));
    }

    @FXML
    private void decreaseStock() {
        if (stock > 0) stock--;
        lblStock.setText(String.valueOf(stock));
    }
}

