package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class Menucontroller {
  
  @FXML
    private void goBack() throws Exception {
        App.setRoot("LoginPage"); // optional: go back to primary page
    }

    @FXML
    private TextField SearchBarMenu;{
      if (SearchBarMenu.getText().isEmpty()) {
        System.out.println("Please enter a search term.");
      } else {
        String searchTerm = SearchBarMenu.getText();
        System.out.println("Searching for: " + searchTerm);
        // Implement search functionality here
      }
    }
    
}
