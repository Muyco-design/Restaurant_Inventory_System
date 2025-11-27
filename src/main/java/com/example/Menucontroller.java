package com.example;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Menucontroller {
  
  @FXML
    private void goBack() throws Exception {
        App.setRoot("LoginPage"); // optional: go back to primary page
    }

    
    @FXML
    private BarChart<String, Number> BarChart;

    @FXML
    private void initialize() {
        XYChart.Series<String, Number> sample = new XYChart.Series<>();
        sample.setName("Example Data");
        sample.getData().add(new XYChart.Data<>("Jan", 50));
        sample.getData().add(new XYChart.Data<>("Feb", 80));
        sample.getData().add(new XYChart.Data<>("Mar", 30));

        BarChart.getData().add(sample);
        BarChart.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

    }

    @FXML
    private Button MenuButton;

    @FXML
    private TextField SearchBarMenu;

    @FXML
    private void handleSearch() {
        String text = SearchBarMenu.getText();

        if (text.isEmpty()) {
            System.out.println("Please enter a search.");
        } else {
            System.out.println("Searching for: " + text);
        }
    }
    
}
