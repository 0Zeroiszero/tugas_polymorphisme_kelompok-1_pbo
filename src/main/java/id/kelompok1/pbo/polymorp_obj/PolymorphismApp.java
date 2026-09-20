package id.kelompok1.pbo.polymorp_obj;

import id.kelompok1.pbo.polymorp_obj.util.FontLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.IOException;

public class PolymorphismApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Muat font sebelum UI dibuat
        FontLoader.loadAll();

        FXMLLoader loader = new FXMLLoader(
                PolymorphismApp.class.getResource("main-view.fxml"));

        // Ukuran awal (dipakai sebelum maximize)
        Scene scene = new Scene(loader.load(), 1280, 720);

        stage.setTitle("Polymorphism Edu");
        stage.setScene(scene);

        // ── Kunci utama: langsung maximize saat start ──
        stage.setMaximized(true);

        // F11 toggle fullscreen
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