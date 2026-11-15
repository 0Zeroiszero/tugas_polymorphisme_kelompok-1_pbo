package id.kelompok1.pbo.polymorp_obj.ui;

import id.kelompok1.pbo.polymorp_obj.util.SoundManager;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class IsiBensinPage extends StackPane {

    private static final String IMG_BASE = "/id/kelompok1/pbo/polymorp_obj/gambar/";
    private static final String SOUND_BASE = "/id/kelompok1/pbo/polymorp_obj/sounds/";
    private static final String FONT_MAIN = "Poppins";

    private final DoubleProperty progress = new SimpleDoubleProperty(0);
    private Timeline fillTimeline;

    public IsiBensinPage(String fileBackground,
                         String fileKendaraan,
                         String jenis,
                         String fuelType,
                         double liter,
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
        vehicle.layoutXProperty().bind(overlay.widthProperty().multiply(0.08));
        vehicle.layoutYProperty().bind(
                overlay.heightProperty().multiply(1.1)
                        .subtract(vehicle.fitHeightProperty()));
        // ────────────────────────────────────────────────────

        overlay.getChildren().add(vehicle);

        // ── Panel progress ──────────────────────────────────
        VBox panel = buatPanel(jenis, fuelType, liter);

        // Suara kendaraan mengerem sebelum pengisian dimulai
        String soundKendaraan = jenis.equals("Mobil")
                ? "car-slow-down.wav" : "motor-slowing-and-brake-sound.wav";

        // ── Tombol kembali ──────────────────────────────────
        Button btnBack = new Button("← Kembali");
        btnBack.setStyle(
                "-fx-font-family: '" + FONT_MAIN + "';" +
                        "-fx-background-color: rgba(255, 255, 255, 0.95);" +
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
            SoundManager.stop(SOUND_BASE + soundKendaraan);
            SoundManager.stop(SOUND_BASE + "water-pour.wav");
            onBack.run();
        });

        StackPane.setAlignment(panel, Pos.CENTER_RIGHT);
        StackPane.setMargin(panel, new Insets(0, 48, 0, 0));
        StackPane.setAlignment(btnBack, Pos.TOP_LEFT);
        StackPane.setMargin(btnBack, new Insets(20));

        // Urutan penting: overlay dulu (paling belakang),
        // lalu panel, lalu tombol kembali
        getChildren().addAll(overlay, panel, btnBack);

        // ── Animasi isi bensin: kendaraan mengerem, lalu 0 liter → penuh ──
        SoundManager.playOnce(SOUND_BASE + soundKendaraan);
        fillTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        e -> SoundManager.playOnce(SOUND_BASE + "water-pour.wav"),
                        new KeyValue(progress, 0.0)),
                new KeyFrame(Duration.seconds(4), new KeyValue(progress, 1.0))
        );
        fillTimeline.setDelay(Duration.seconds(1.2));
        fillTimeline.play();
    }

    private VBox buatPanel(String jenis, String fuelType, double liter) {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setPadding(new Insets(20, 24, 20, 24));
        panel.setMaxWidth(340);
        panel.setMaxHeight(Region.USE_PREF_SIZE);
        panel.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.97);" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-color: rgba(0, 0, 0, 0.12);" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 22, 0.2, 0, 8);"
        );

        Label kicker = new Label("SPBU  •  SELF-SERVICE");
        kicker.setFont(Font.font(FONT_MAIN, FontWeight.BOLD, 11));
        kicker.setTextFill(Color.web("#ef6c00"));

        Label judul = new Label("Mengisi Bahan Bakar");
        judul.setFont(Font.font(FONT_MAIN, FontWeight.BOLD, 20));
        judul.setTextFill(Color.web("#1a1a1a"));

        Label sub = new Label(jenis + "  •  " + fuelType);
        sub.setFont(Font.font(FONT_MAIN, FontWeight.SEMI_BOLD, 13));
        sub.setTextFill(Color.web("#5f6368"));

        double barWidth = 292;
        double barHeight = 16;

        Region bgBar = new Region();
        bgBar.setPrefSize(barWidth, barHeight);
        bgBar.setMinSize(barWidth, barHeight);
        bgBar.setMaxSize(barWidth, barHeight);
        bgBar.setStyle(
                "-fx-background-color: rgba(0,0,0,0.10);" +
                        "-fx-background-radius: " + (barHeight / 2) + ";"
        );

        Pane barStack = new Pane();
        barStack.setPrefSize(barWidth, barHeight);
        barStack.setMinSize(barWidth, barHeight);
        barStack.setMaxSize(barWidth, barHeight);

        Region fillBar = new Region();
        fillBar.setPrefHeight(barHeight);
        fillBar.setMinHeight(barHeight);
        fillBar.setMaxHeight(barHeight);
        fillBar.prefWidthProperty().bind(progress.multiply(barWidth));
        fillBar.setStyle(
                "-fx-background-color: linear-gradient(to right, #66bb6a, #2e7d32);" +
                        "-fx-background-radius: " + (barHeight / 2) + ";"
        );

        barStack.getChildren().addAll(bgBar, fillBar);

        Label literLabel = new Label("0,0 liter");
        literLabel.setFont(Font.font(FONT_MAIN, FontWeight.BOLD, 30));
        literLabel.setTextFill(Color.web("#2e7d32"));

        Label kapasitas = new Label("dari " + formatLiter(liter) + " liter");
        kapasitas.setFont(Font.font(FONT_MAIN, FontWeight.NORMAL, 13));
        kapasitas.setTextFill(Color.web("#5f6368"));

        HBox barisLiter = new HBox(10, literLabel, kapasitas);
        barisLiter.setAlignment(Pos.BASELINE_LEFT);

        Label harga = new Label("Rp 13.900 / liter");
        harga.setFont(Font.font(FONT_MAIN, FontWeight.SEMI_BOLD, 12));
        harga.setTextFill(Color.web("#5f6368"));

        progress.addListener((obs, old, val) ->
                literLabel.setText(String.format("%.1f liter", val.doubleValue() * liter)));

        panel.getChildren().addAll(kicker, judul, sub, barStack, barisLiter, harga);
        return panel;
    }

    private static String formatLiter(double liter) {
        return liter == Math.rint(liter)
                ? String.format("%.0f", liter)
                : String.format("%.1f", liter);
    }
}
