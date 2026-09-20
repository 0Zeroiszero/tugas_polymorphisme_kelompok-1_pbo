package id.kelompok1.pbo.polymorp_obj;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class PolymorphismApp extends Application {

    private static final double WINDOW_SCALE = 0.85;
    private static final double ASPECT_RATIO = 16.0 / 9.0;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                PolymorphismApp.class.getResource("main-view.fxml")
        );

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        double w = screen.getWidth() * WINDOW_SCALE;
        double h = w / ASPECT_RATIO;

        if (h > screen.getHeight() * WINDOW_SCALE) {
            h = screen.getHeight() * WINDOW_SCALE;
            w = h * ASPECT_RATIO;
        }

        Scene scene = new Scene(fxmlLoader.load(), w, h);

        stage.setTitle("Polymorphism Edu");
        stage.setScene(scene);
        stage.centerOnScreen();

        // F11 untuk toggle fullscreen
        scene.getAccelerators().put(
                KeyCombination.keyCombination("F11"),
                () -> stage.setFullScreen(!stage.isFullScreen())
        );

        // ESC untuk keluar fullscreen
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE && stage.isFullScreen()) {
                stage.setFullScreen(false);
            }
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}