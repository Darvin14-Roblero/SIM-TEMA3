package com.darvinsistems.app.variablesaleatorias;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MianController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
