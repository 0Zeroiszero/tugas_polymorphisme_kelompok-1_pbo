package id.kelompok1.pbo.polymorp_obj.ui;

import javafx.scene.layout.StackPane;

public class BackgroundContainer extends StackPane {

    private static final String BG_PATH =
            "/id/kelompok1/pbo/polymorp_obj/gambar/taman_kota.jpg";

    public BackgroundContainer() {
        // Ambil URL lengkap dari resource
        String url = getClass().getResource(BG_PATH).toExternalForm();

        setStyle(
                "-fx-background-image: url('" + url + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;"
        );
    }

    public void addOverlay(javafx.scene.Node node) {
        getChildren().add(node);
    }
}