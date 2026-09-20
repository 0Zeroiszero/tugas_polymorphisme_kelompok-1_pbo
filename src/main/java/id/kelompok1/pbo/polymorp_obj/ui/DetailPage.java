package id.kelompok1.pbo.polymorp_obj.ui;

import id.kelompok1.pbo.polymorp_obj.util.SoundManager;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Animation;
import javafx.scene.image.ImageView;

public class DetailPage extends StackPane {

    private static final String IMG_BASE   = "/id/kelompok1/pbo/polymorp_obj/gambar/";
    private static final String SOUND_BASE = "/id/kelompok1/pbo/polymorp_obj/sounds/";
    private static final String FONT_MAIN  = "Poppins";

    private final Pane overlay;
    private final String soundPath;
    private boolean vehicleMoving = false;

    // Field baru
    private ImageView movingVehicle;
    private TranslateTransition vehicleAnimation;

    /**
     * Tambahkan kendaraan yang bergerak sepanjang jalan.
     * @param moveLeft true = bergerak ke kiri (untuk mobil), false = ke kanan (untuk motor)
     */
    public void addMovingVehicle(String imageFile,
                                 double yPersen,
                                 double heightPersen,
                                 double durationSec,
                                 boolean moveLeft) {
        Image img = new Image(getClass().getResourceAsStream(IMG_BASE + imageFile));
        movingVehicle = new ImageView(img);
        movingVehicle.setPreserveRatio(true);
        movingVehicle.setSmooth(true);

        movingVehicle.fitHeightProperty().bind(
                overlay.heightProperty().multiply(heightPersen));
        movingVehicle.layoutYProperty().bind(
                overlay.heightProperty().multiply(yPersen)
                        .subtract(movingVehicle.fitHeightProperty()));

        overlay.getChildren().add(movingVehicle);

        // Setup animasi — hanya jalan setelah overlay punya ukuran
        Runnable setup = () -> {
            if (vehicleAnimation != null) return;   // sudah di-setup
            double w = overlay.getWidth();
            if (w <= 0) return;                     // belum ada ukuran, tunda

            double startX, endX;
            if (moveLeft) {
                startX = w * 0.70;
                endX = -w * 0.30;
            } else {
                startX = w * 0.01;
                endX = w * 1.30;
            }

            vehicleAnimation = new TranslateTransition(
                    Duration.seconds(durationSec), movingVehicle);
            vehicleAnimation.setFromX(startX);
            vehicleAnimation.setToX(endX);
            vehicleAnimation.setCycleCount(Animation.INDEFINITE);
            vehicleAnimation.setInterpolator(Interpolator.LINEAR);

            movingVehicle.setTranslateX(startX);
        };

        // Coba langsung — kalau sudah punya ukuran, langsung jadi
        setup.run();

        // Kalau belum, tunggu sampai ukuran tersedia
        if (vehicleAnimation == null) {
            javafx.beans.value.ChangeListener<Number> listener =
                    new javafx.beans.value.ChangeListener<Number>() {
                        @Override
                        public void changed(
                                javafx.beans.value.ObservableValue<? extends Number> obs,
                                Number old, Number val) {
                            if (val.doubleValue() > 0) {
                                setup.run();
                                overlay.widthProperty().removeListener(this);
                            }
                        }
                    };
            overlay.widthProperty().addListener(listener);
        }
    }

    /** Toggle bergerak/berhenti. Return status baru: true = bergerak. */
    public boolean toggleVehicle() {
        if (vehicleAnimation == null) return false;
        if (vehicleMoving) {
            vehicleAnimation.pause();
            vehicleMoving = false;
        } else {
            vehicleAnimation.play();
            vehicleMoving = true;
        }
        return vehicleMoving;
    }

    public boolean isVehicleMoving() {
        return vehicleMoving;
    }

    public void stopVehicle() {
        if (vehicleAnimation != null) {
            vehicleAnimation.stop();
            vehicleMoving = false;
        }
    }

    /**
     * Tampilkan toast singkat di bawah layar.
     * Muncul dengan fade-in, bertahan, lalu fade-out.
     */
    public void showToast(String pesan) {
        Label toast = new Label(pesan);
        toast.setStyle(
                "-fx-font-family: '" + FONT_MAIN + "';" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: rgba(30, 30, 30, 0.92);" +
                        "-fx-background-radius: 24;" +
                        "-fx-padding: 12 28 12 28;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 16, 0.25, 0, 6);"
        );
        toast.setOpacity(0);
        toast.setMouseTransparent(true);

        // Posisi: bawah tengah layar
        toast.layoutXProperty().bind(
                overlay.widthProperty().multiply(0.5)
                        .subtract(toast.widthProperty().divide(2)));
        toast.layoutYProperty().bind(
                overlay.heightProperty().multiply(0.88)
                        .subtract(toast.heightProperty().divide(2)));

        overlay.getChildren().add(toast);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(180), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Tahan
        PauseTransition hold = new PauseTransition(Duration.millis(1500));

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition seq = new SequentialTransition(fadeIn, hold, fadeOut);
        seq.setOnFinished(e -> overlay.getChildren().remove(toast));
        seq.play();
    }

    public DetailPage(String fileGambar, String fileSuara, Runnable onBack) {
        // ── Background ────────────────────────────────────────
        String url = getClass().getResource(IMG_BASE + fileGambar).toExternalForm();
        setStyle(
                "-fx-background-image: url('" + url + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-color: #f0f0f0;"
        );

        // ── MediaPlayer untuk suara ───────────────────────────
        MediaPlayer created = null;
        if (fileSuara != null) {
            try {
                java.net.URL soundResource = getClass().getResource(SOUND_BASE + fileSuara);
                System.out.println("[SOUND] Load: " + SOUND_BASE + fileSuara);
                System.out.println("[SOUND] URL : " + soundResource);

                if (soundResource != null) {
                    Media media = new Media(soundResource.toExternalForm());
                    created = new MediaPlayer(media);

                    final MediaPlayer fp = created;
                    created.setOnError(() ->
                            System.err.println("[SOUND] ERROR: " + fp.getError()));
                    created.setOnReady(() ->
                            System.out.println("[SOUND] READY, durasi " + media.getDuration()));
                    System.out.println("[SOUND] MediaPlayer dibuat");
                } else {
                    System.err.println("[SOUND] resource NULL");
                }
            } catch (Exception e) {
                System.err.println("[SOUND] Exception: " + e);
                e.printStackTrace();
            }
        }
        this.soundPath = (fileSuara != null)
                ? SOUND_BASE + fileSuara
                : null;

        // ── Overlay ───────────────────────────────────────────
        overlay = new Pane();
        overlay.setPickOnBounds(false);

        // ── Tombol kembali ────────────────────────────────────
        Button btnBack = new Button("← Kembali");
        btnBack.setStyle(
                "-fx-font-family: '" + FONT_MAIN + "';" +
                        "-fx-background-color: rgba(255, 255, 255, 0.85);" +
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
            stopSound();
            stopVehicle();// ← lepas MediaPlayer sebelum keluar
            onBack.run();
        });
        StackPane.setAlignment(btnBack, Pos.TOP_LEFT);
        StackPane.setMargin(btnBack, new Insets(20));

        getChildren().addAll(overlay, btnBack);
    }

    // ──────────────────────────────────────────────────────────
    // KONTROL SUARA
    // ──────────────────────────────────────────────────────────

    /** Putar suara dari awal. Kalau sedang diputar, akan restart. */
    public void playSound() {
        if (soundPath != null) SoundManager.playOnce(soundPath);
    }

    public void playSoundLoop() {
        if (soundPath != null) SoundManager.playLoop(soundPath);
    }

    public void stopSound() {
        if (soundPath != null) SoundManager.stop(soundPath);
    }

// dispose() tidak perlu lagi — MediaPlayer di-cache global

    // ──────────────────────────────────────────────────────────
    // TOMBOL ORANYE
    // ──────────────────────────────────────────────────────────

    /**
     * Versi default (5 argumen) — tombol oranye seperti biasa.
     * Dipakai oleh KENDARAAN.
     */
    public Button addOrangeButton(String label,
                                  double xPersen, double yPersen,
                                  double wPersen, double hPersen) {
        return addOrangeButton(label, xPersen, yPersen, wPersen, hPersen, false);
    }

    /**
     * Versi dengan opsi transparan (6 argumen).
     * @param transparent true = tombol tak terlihat (hanya area klik),
     *                    false = tombol oranye seperti biasa.
     * Dipakai oleh HEWAN (transparent = true).
     */
    public Button addOrangeButton(String label,
                                  double xPersen, double yPersen,
                                  double wPersen, double hPersen,
                                  boolean transparent) {
        Button btn = new Button(label);

        if (transparent) {
            btn.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-border-color: transparent;" +
                            "-fx-text-fill: transparent;" +
                            "-fx-cursor: hand;"
            );
            btn.setCursor(Cursor.HAND);
        } else {
            applyOrangeNormal(btn);
            btn.setCursor(Cursor.HAND);
            btn.setOnMouseEntered(e -> applyOrangeHover(btn));
            btn.setOnMouseExited(e -> applyOrangeNormal(btn));
        }

        btn.prefWidthProperty().bind(overlay.widthProperty().multiply(wPersen));
        btn.prefHeightProperty().bind(overlay.heightProperty().multiply(hPersen));

        btn.layoutXProperty().bind(
                overlay.widthProperty().multiply(xPersen)
                        .subtract(btn.widthProperty().divide(2)));
        btn.layoutYProperty().bind(
                overlay.heightProperty().multiply(yPersen)
                        .subtract(btn.heightProperty().divide(2)));

        overlay.getChildren().add(btn);
        return btn;
    }

    // ──────────────────────────────────────────────────────────
    // SUBMENU (dengan animasi)
    // ──────────────────────────────────────────────────────────

    public void showSubmenuOn(Button anchor, boolean leftSide,
                              Runnable onBersuara, Runnable onInformasi) {
        VBox submenu = new VBox(8);
        submenu.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.97);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(0, 0, 0, 0.10);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 18, 0.22, 0, 6);"
        );
        submenu.setVisible(false);
        submenu.setOpacity(0);
        submenu.setScaleX(0.85);
        submenu.setScaleY(0.85);

        Button bBersuara = buatSubmenuButton("🔊  Bersuara", onBersuara);
        Button bInfo     = buatSubmenuButton("ℹ  Informasi", onInformasi);
        submenu.getChildren().addAll(bBersuara, bInfo);

        Runnable updatePos = () -> {
            double ax = anchor.getLayoutX();
            double ay = anchor.getLayoutY();
            double aw = anchor.getWidth();
            double ah = anchor.getHeight();
            double sw = submenu.getWidth();
            double sh = submenu.getHeight();

            double x = leftSide ? (ax - sw - 14) : (ax + aw + 14);
            double y = ay + ah / 2.0 - sh / 2.0;

            submenu.setLayoutX(x);
            submenu.setLayoutY(y);
        };

        anchor.layoutXProperty().addListener((o, a, b) -> updatePos.run());
        anchor.layoutYProperty().addListener((o, a, b) -> updatePos.run());
        anchor.widthProperty()  .addListener((o, a, b) -> updatePos.run());
        anchor.heightProperty() .addListener((o, a, b) -> updatePos.run());
        submenu.widthProperty() .addListener((o, a, b) -> updatePos.run());
        submenu.heightProperty().addListener((o, a, b) -> updatePos.run());

        Runnable showSubmenu = () -> {
            updatePos.run();
            double offsetX = leftSide ? 30 : -30;

            submenu.setVisible(true);
            submenu.setOpacity(0);
            submenu.setScaleX(0.85);
            submenu.setScaleY(0.85);
            submenu.setTranslateX(offsetX);

            Duration d = Duration.millis(220);
            FadeTransition fade = new FadeTransition(d, submenu);
            fade.setFromValue(0); fade.setToValue(1);
            ScaleTransition scale = new ScaleTransition(d, submenu);
            scale.setFromX(0.85); scale.setFromY(0.85);
            scale.setToX(1.0);   scale.setToY(1.0);
            TranslateTransition slide = new TranslateTransition(d, submenu);
            slide.setFromX(offsetX); slide.setToX(0);

            ParallelTransition pt = new ParallelTransition(fade, scale, slide);
            pt.setInterpolator(Interpolator.EASE_OUT);
            pt.play();
        };

        Runnable hideSubmenu = () -> {
            double offsetX = leftSide ? 20 : -20;
            Duration d = Duration.millis(150);

            FadeTransition fade = new FadeTransition(d, submenu);
            fade.setFromValue(1); fade.setToValue(0);
            ScaleTransition scale = new ScaleTransition(d, submenu);
            scale.setFromX(1.0); scale.setFromY(1.0);
            scale.setToX(0.9);   scale.setToY(0.9);
            TranslateTransition slide = new TranslateTransition(d, submenu);
            slide.setFromX(0); slide.setToX(offsetX);

            ParallelTransition pt = new ParallelTransition(fade, scale, slide);
            pt.setInterpolator(Interpolator.EASE_IN);
            pt.setOnFinished(e -> submenu.setVisible(false));
            pt.play();
        };

        anchor.setOnAction(e -> {
            if (submenu.isVisible()) hideSubmenu.run();
            else showSubmenu.run();
        });

        bBersuara.setOnAction(e -> {
            hideSubmenu.run();
            onBersuara.run();
        });
        bInfo.setOnAction(e -> {
            System.out.println(">>> bInfo diklik");
            hideSubmenu.run();
            onInformasi.run();
        });

        overlay.getChildren().add(submenu);
    }

    /**
     * Submenu untuk kendaraan: "Bergerak" (merah/hijau) + "Isi Bensin" + "Informasi".
     */
    public void showVehicleSubmenuOn(Button anchor, boolean leftSide,
                                     Runnable onIsiBensin,
                                     Runnable onInformasi) {
        VBox submenu = new VBox(8);
        submenu.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.97);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(0, 0, 0, 0.10);" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 18, 0.22, 0, 6);"
        );
        submenu.setVisible(false);
        submenu.setOpacity(0);
        submenu.setScaleX(0.85);
        submenu.setScaleY(0.85);

        // ── 1. Tombol "Bergerak" — merah/hijau ───────────────
        Button bBergerak = new Button("Bergerak");
        bBergerak.setPrefWidth(180);
        bBergerak.setCursor(Cursor.HAND);

        Runnable updateBergerakStyle = () -> {
            if (vehicleMoving) {
                // HIJAU
                bBergerak.setStyle(
                        "-fx-font-family: '" + FONT_MAIN + "';" +
                                "-fx-background-color: rgba(76, 175, 80, 0.22);" +
                                "-fx-text-fill: #1b5e20;" +
                                "-fx-font-size: 14px;" +
                                "-fx-font-weight: 700;" +
                                "-fx-padding: 10 22 10 22;" +
                                "-fx-background-radius: 10;" +
                                "-fx-border-color: #4caf50;" +
                                "-fx-border-radius: 10;" +
                                "-fx-border-width: 1.5;" +
                                "-fx-alignment: center-left;" +
                                "-fx-cursor: hand;"
                );
            } else {
                // MERAH (default)
                bBergerak.setStyle(
                        "-fx-font-family: '" + FONT_MAIN + "';" +
                                "-fx-background-color: rgba(244, 67, 54, 0.18);" +
                                "-fx-text-fill: #b71c1c;" +
                                "-fx-font-size: 14px;" +
                                "-fx-font-weight: 700;" +
                                "-fx-padding: 10 22 10 22;" +
                                "-fx-background-radius: 10;" +
                                "-fx-border-color: #e53935;" +
                                "-fx-border-radius: 10;" +
                                "-fx-border-width: 1.5;" +
                                "-fx-alignment: center-left;" +
                                "-fx-cursor: hand;"
                );
            }
        };
        updateBergerakStyle.run();

        bBergerak.setOnAction(e -> {
            toggleVehicle();
            updateBergerakStyle.run();

            // ── Suara mengikuti status gerak ─────────────────
            if (vehicleMoving) {
                playSoundLoop();     // mulai bergerak → putar suara (loop)
            } else {
                stopSound();         // berhenti → hentikan suara
            }
        });

        // ── 2. Tombol "Isi Bensin" ──────────────────────────
        Button bIsiBensin = buatSubmenuButton("⛽  Isi Bensin", onIsiBensin);

        // ── 3. Tombol "Informasi" ───────────────────────────
        Button bInformasi = buatSubmenuButton("ℹ  Informasi", onInformasi);

        submenu.getChildren().addAll(bBergerak, bIsiBensin, bInformasi);

        // ── Positioning ──────────────────────────────────────
        Runnable updatePos = () -> {
            double ax = anchor.getLayoutX();
            double ay = anchor.getLayoutY();
            double aw = anchor.getWidth();
            double ah = anchor.getHeight();
            double sw = submenu.getWidth();
            double sh = submenu.getHeight();
            double x = leftSide ? (ax - sw - 14) : (ax + aw + 14);
            double y = ay + ah / 2.0 - sh / 2.0;
            submenu.setLayoutX(x);
            submenu.setLayoutY(y);
        };

        anchor.layoutXProperty().addListener((o, a, b) -> updatePos.run());
        anchor.layoutYProperty().addListener((o, a, b) -> updatePos.run());
        anchor.widthProperty()  .addListener((o, a, b) -> updatePos.run());
        anchor.heightProperty() .addListener((o, a, b) -> updatePos.run());
        submenu.widthProperty() .addListener((o, a, b) -> updatePos.run());
        submenu.heightProperty().addListener((o, a, b) -> updatePos.run());

        // ── Animasi show/hide ────────────────────────────────
        Runnable showSubmenu = () -> {
            updatePos.run();
            double offsetX = leftSide ? 30 : -30;
            submenu.setVisible(true);
            submenu.setOpacity(0);
            submenu.setScaleX(0.85);
            submenu.setScaleY(0.85);
            submenu.setTranslateX(offsetX);

            Duration d = Duration.millis(220);
            FadeTransition fade = new FadeTransition(d, submenu);
            fade.setFromValue(0); fade.setToValue(1);
            ScaleTransition scale = new ScaleTransition(d, submenu);
            scale.setFromX(0.85); scale.setFromY(0.85);
            scale.setToX(1.0);   scale.setToY(1.0);
            TranslateTransition slide = new TranslateTransition(d, submenu);
            slide.setFromX(offsetX); slide.setToX(0);

            ParallelTransition pt = new ParallelTransition(fade, scale, slide);
            pt.setInterpolator(Interpolator.EASE_OUT);
            pt.play();
        };

        Runnable hideSubmenu = () -> {
            double offsetX = leftSide ? 20 : -20;
            Duration d = Duration.millis(150);
            FadeTransition fade = new FadeTransition(d, submenu);
            fade.setFromValue(1); fade.setToValue(0);
            ScaleTransition scale = new ScaleTransition(d, submenu);
            scale.setFromX(1.0); scale.setFromY(1.0);
            scale.setToX(0.9);   scale.setToY(0.9);
            TranslateTransition slide = new TranslateTransition(d, submenu);
            slide.setFromX(0); slide.setToX(offsetX);
            ParallelTransition pt = new ParallelTransition(fade, scale, slide);
            pt.setInterpolator(Interpolator.EASE_IN);
            pt.setOnFinished(e -> submenu.setVisible(false));
            pt.play();
        };

        anchor.setOnAction(e -> {
            if (submenu.isVisible()) hideSubmenu.run();
            else showSubmenu.run();
        });

        bIsiBensin.setOnAction(e -> {
            hideSubmenu.run();
            stopSound();
            stopVehicle();// ← lepas MediaPlayer agar tidak lock file
            onIsiBensin.run();
        });

        bInformasi.setOnAction(e -> {
            hideSubmenu.run();
            onInformasi.run();
        });

        overlay.getChildren().add(submenu);
    }
    // ──────────────────────────────────────────────────────────
    // HELPER
    // ──────────────────────────────────────────────────────────

    private Button buatSubmenuButton(String label, Runnable aksi) {
        Button b = new Button(label);
        String normal =
                "-fx-font-family: '" + FONT_MAIN + "';" +
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: #2b2b2b;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-padding: 10 22 10 22;" +
                        "-fx-background-radius: 10;" +
                        "-fx-alignment: center-left;" +
                        "-fx-cursor: hand;";
        String hover =
                "-fx-font-family: '" + FONT_MAIN + "';" +
                        "-fx-background-color: rgba(255, 152, 0, 0.18);" +
                        "-fx-text-fill: #e65100;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-padding: 10 22 10 22;" +
                        "-fx-background-radius: 10;" +
                        "-fx-alignment: center-left;" +
                        "-fx-cursor: hand;";

        b.setStyle(normal);
        b.setCursor(Cursor.HAND);
        b.setPrefWidth(180);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e -> b.setStyle(normal));
        b.setOnAction(e -> aksi.run());
        return b;
    }

    private void applyOrangeNormal(Button btn) {
        btn.setStyle(
                "-fx-font-family: '" + FONT_MAIN + "';" +
                        "-fx-background-color: rgba(255, 152, 0, 0.28);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: rgba(255, 152, 0, 0.95);" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-width: 2;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(245,124,0,0.45), 14, 0.25, 0, 5);"
        );
    }

    private void applyOrangeHover(Button btn) {
        btn.setStyle(
                "-fx-font-family: '" + FONT_MAIN + "';" +
                        "-fx-background-color: rgba(255, 152, 0, 0.55);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: #f57c00;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-width: 2;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(245,124,0,0.7), 18, 0.3, 0, 6);"
        );
    }

    public Pane getOverlay() { return overlay; }

    public void place(Node node, double xPersen, double yPersen) {
        node.layoutXProperty().bind(overlay.widthProperty().multiply(xPersen));
        node.layoutYProperty().bind(overlay.heightProperty().multiply(yPersen));
        overlay.getChildren().add(node);
    }
}