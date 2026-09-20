package id.kelompok1.pbo.polymorp_obj;

import id.kelompok1.pbo.polymorp_obj.ui.BackgroundContainer;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class MainController {

    @FXML
    private BorderPane rootPane;

    @FXML
    public void initialize() {
        BackgroundContainer bg = new BackgroundContainer();
        rootPane.setCenter(bg);
    }
}