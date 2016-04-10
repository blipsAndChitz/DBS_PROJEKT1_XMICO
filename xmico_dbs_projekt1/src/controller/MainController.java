package controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class MainController {
    
    @FXML
    private StackPane screenHolder;

    public void setScreen(Node node) {
        screenHolder.getChildren().setAll(node);
    }

}