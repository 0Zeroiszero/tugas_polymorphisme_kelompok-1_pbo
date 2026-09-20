package id.kelompok1.pbo.polymorp_obj.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class IsiBensinPage extends StackPane {

    private static final String IMG_BASE = "/id/kelompok1/pbo/polymorp_obj/gambar/";
    private static final String FONT_MAIN = "Poppins";

    private final DoubleProperty progress = new SimpleDoubleProperty(0);
    private Timeline fillTimeline;

    public IsiBensinPage(String fileBackground,
                         String fileKendaraan,
                         String jenis,
                         String fuelType,
                         Runnable onBack) {

        // ── Background SPBU ─────────────────────────────────
        String bgUrl = getClass().getResource(IMG_BASE + fileBackground).toExternalForm();
        setStyle(
                "-fx-background-image: url('" + bgUrl + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-color: #f0f0f0;"
        );

        // ── Pane overlay (kosong, untuk kendaraan) ──────────
        Pane overlay = new Pane();
        overlay.setPickOnBounds(false);

        // ── Kendaraan diam (dalam overlay Pane) ─────────────
        Image img = new Image(getClass().getResourceAsStream(IMG_BASE + fileKendaraan));
        ImageView vehicle = new ImageView(img);
        vehicle.setPreserveRatio(true);
        vehicle.fitHeightProperty().bind(overlay.heightProperty().multiply(0.6));

        // ── SESUAIKAN POSISI KENDARAAN DI SINI ──────────────
        vehicle.layoutXProperty().bind(overlay.widthProperty().multiply(0.10));
        vehicle.layoutYProperty().bind(
                overlay.heightProperty().multiply(1.1)
                        .subtract(vehicle.fitHeightProperty()));
        // ────────────────────────────────────────────────────

        overlay.getChildren().add(vehicle);

        // ── Panel progress ──────────────────────────────────
        VBox panel = buatPanel(jenis, fuelType);

        // ── Tombol kembali ──────────────────────────────────
        Button btnBack = new Button("← Kembali");
        btnBack.setStyle(
                "-fx-font-family: '" + FONT_MAIN + "';" +
                        "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                        "-fx-text-fill: #2b2b2b;" +
                        "-fx-font-weight: 600;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 18 8 18;" +
                        "-fx-background-radius: 12;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0.2, 0, 3);"
        );
        btnBack.setCursor(Cursor.HAND);
        btnBack.setOnAction(e -> {
            if (fillTimeline != null) fillTimeline.stop();
            onBack.run();
        });

        StackPane.setAlignment(panel, Pos.TOP_CENTER);
        StackPane.setMargin(panel, new Insets(40, 0, 0, 0));
        StackPane.setAlignment(btnBack, Pos.TOP_LEFT);
        StackPane.setMargin(btnBack, new Insets(20));

        // Urutan penting: overlay dulu (paling belakang),
        // lalu panel, lalu tombol kembali
        getChildren().addAll(overlay, panel, btnBack);

        // ── Animasi isi bensin: 0% → 100% ───────────────────
        fillTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progress, 0.0)),
                new KeyFrame(Duration.seconds(3), new KeyValue(progress, 1.0))
        );
        fillTimeline.setDelay(Duration.millis(600));
        fillTimeline.play();
    }

    private VBox buatPanel(String jenis, String fuelType) {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(14, 24, 14, 24));    // ← lebih tipis
        panel.setMaxWidth(360);                           // ← batasi lebar panel
        panel.setMaxHeight(Region.USE_PREF_SIZE);
        panel.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(0, 0, 0, 0.10);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.22), 16, 0.2, 0, 5);"
        );

        Label judul = new Label("⛽  Mengisi Bahan Bakar");
        judul.setStyle(
                "-fx-font-family: '" + FONT_MAIN + "';" +
                        "-fx-font-size: 15px;" +                      // ← dari 20px
                        "-fx-font-weight: 700;" +
                        "-fx-text-fill: #1a1a1a;"
        );

        Label sub = new Label(jenis + "  •  " + fuelType);
        sub.setStyle(
                "-fx-font-family: '" + FONT_MAIN + "';" +
                        "-fx-font-size: 11px;" +                      // ← dari 13px
                        "-fx-text-fill: #666;"
        );

        double barWidth = 260;                            // ← dari 380
        double barHeight = 14;                            // ← dari 22

        Region bgBar = new Region();
        bgBar.setPrefSize(barWidth, barHeight);
        bgBar.setMinSize(barWidth, barHeight);
        bgBar.setMaxSize(barWidth, barHeight);
        bgBar.setStyle(
                "-fx-background-color: rgba(0,0,0,0.10);" +
                        "-fx-background-radius: " + (barHeight / 2) + ";"
        );

        // Background bar (pakai HBox langsung)
        HBox barStack = new HBox();
        barStack.setPrefSize(barWidth, barHeight);
        barStack.setMaxSize(barWidth, barHeight);
        barStack.setAlignment(Pos.CENTER_LEFT);
        barStack.setStyle(
                "-fx-background-color: rgba(0,0,0,0.10);" +
                        "-fx-background-radius: " + (barHeight / 2) + ";"
        );

// Isi bar
        Region fillBar = new Region();
        fillBar.setPrefHeight(barHeight);
        fillBar.prefWidthProperty().bind(progress.multiply(barWidth));
        fillBar.setStyle(
                "-fx-background-color: linear-gradient(to right, #66bb6a, #2e7d32);" +
                        "-fx-background-radius: " + (barHeight / 2) + ";"
        );

        barStack.getChildren().add(fillBar);

        Label persen = new Label("0%");
        persen.setStyle(
                "-fx-font-family: '" + FONT_MAIN + "';" +
                        "-fx-font-size: 20px;" +                      // ← dari 28px
                        "-fx-font-weight: 700;" +
                        "-fx-text-fill: #2e7d32;"
        );

        progress.addListener((obs, old, val) ->
                persen.setText(String.format("%.0f%%", val.doubleValue() * 100)));

        panel.getChildren().addAll(judul, sub, barStack, persen);
        return panel;
    }
}