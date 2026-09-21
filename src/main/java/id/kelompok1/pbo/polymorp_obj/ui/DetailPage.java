package id.kelompok1.pbo.polymorp_obj.ui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.function.BooleanSupplier;

public class DetailPage extends StackPane {

    private static final String IMG_BASE = "/id/kelompok1/pbo/polymorp_obj/gambar/";
    private static final String FONT_MAIN = "Poppins";

    private final Pane overlay;
    private ImageView movingVehicle;
    private TranslateTransition vehicleAnimation;

    public DetailPage(String fileGambar, Runnable onBack) {
        // ── Background ────────────────────────────────────────
        String url = getClass().getResource(IMG_BASE + fileGambar).toExternalForm();
        setStyle(
                "-fx-background-image: url('" + url + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-color: #f0f0f0;"
        );

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
        btnBack.setOnAction(e -> onBack.run());

        StackPane.setAlignment(btnBack, Pos.TOP_LEFT);
        StackPane.setMargin(btnBack, new Insets(20));

        getChildren().addAll(overlay, btnBack);
    }

    // ══════════════════════════════════════════════════════════
    // KENDARAAN BERGERAK
    // ══════════════════════════════════════════════════════════

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

        // Setup animasi setelah overlay punya ukuran
        Runnable setup = () -> {
            if (vehicleAnimation != null) return;
            double w = overlay.getWidth();
            if (w <= 0) return;

            double startX, endX;
            if (moveLeft) {
                startX = w * 0.75;
                endX = -w * 0.30;
            } else {
                startX = w * 0.15;
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

        setup.run();

        if (vehicleAnimation == null) {
            ChangeListener<Number> listener = new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> obs,
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

    public void startVehicle() {
        if (vehicleAnimation != null) vehicleAnimation.play();
    }

    public void pauseVehicle() {
        if (vehicleAnimation != null) vehicleAnimation.pause();
    }

    public void stopVehicle() {
        if (vehicleAnimation != null) vehicleAnimation.stop();
    }

    // ══════════════════════════════════════════════════════════
    // TOMBOL OPSI
    // ══════════════════════════════════════════════════════════

    public Button addOrangeButton(String label,
                                  double xPersen, double yPersen,
                                  double wPersen, double hPersen) {
        return addOrangeButton(label, xPersen, yPersen, wPersen, hPersen, false);
    }

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

    // ══════════════════════════════════════════════════════════
    // SUBMENU HEWAN (Bersuara + Informasi)
    // ══════════════════════════════════════════════════════════

    public void showSubmenuOn(Button anchor, boolean leftSide,
                              Runnable onBersuara,
                              Runnable onInformasi) {
        VBox submenu = buatSubmenuBox();
        Button bBersuara = buatSubmenuButton("🔊  Bersuara", onBersuara);
        Button bInfo     = buatSubmenuButton("ℹ  Informasi", onInformasi);
        submenu.getChildren().addAll(bBersuara, bInfo);

        pasangSubmenu(anchor, leftSide, submenu, onBersuara, onInformasi);

        bBersuara.setOnAction(e -> { hideSubmenu(submenu, leftSide); onBersuara.run(); });
        bInfo.setOnAction(e -> { hideSubmenu(submenu, leftSide); onInformasi.run(); });

        overlay.getChildren().add(submenu);
    }

    // ══════════════════════════════════════════════════════════
    // SUBMENU KENDARAAN (Bergerak + Isi Bensin + Informasi)
    // ══════════════════════════════════════════════════════════

    public void showVehicleSubmenuOn(Button anchor, boolean leftSide,
                                     BooleanSupplier onToggleBergerak,
                                     Runnable onIsiBensin,
                                     Runnable onInformasi) {
        VBox submenu = buatSubmenuBox();

        // ── Tombol "Bergerak" merah/hijau ────────────────────
        Button bBergerak = new Button("Bergerak");
        bBergerak.setPrefWidth(180);
        bBergerak.setCursor(Cursor.HAND);
        applyBergerakStyle(bBergerak, false);   // default merah

        bBergerak.setOnAction(e -> {
            boolean nowMoving = onToggleBergerak.getAsBoolean();
            applyBergerakStyle(bBergerak, nowMoving);
        });

        Button bIsiBensin = buatSubmenuButton("⛽  Isi Bensin", onIsiBensin);
        Button bInformasi = buatSubmenuButton("ℹ  Informasi", onInformasi);

        submenu.getChildren().addAll(bBergerak, bIsiBensin, bInformasi);

        // Positioning + animasi show/hide
        pasangSubmenuKendaraan(anchor, leftSide, submenu);

        bIsiBensin.setOnAction(e -> { hideSubmenu(submenu, leftSide); onIsiBensin.run(); });
        bInformasi.setOnAction(e -> { hideSubmenu(submenu, leftSide); onInformasi.run(); });

        overlay.getChildren().add(submenu);
    }

    // ══════════════════════════════════════════════════════════
    // TOAST
    // ══════════════════════════════════════════════════════════

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

        toast.layoutXProperty().bind(
                overlay.widthProperty().multiply(0.5)
                        .subtract(toast.widthProperty().divide(2)));
        toast.layoutYProperty().bind(
                overlay.heightProperty().multiply(0.88)
                        .subtract(toast.heightProperty().divide(2)));

        overlay.getChildren().add(toast);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(180), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        javafx.animation.PauseTransition hold =
                new javafx.animation.PauseTransition(Duration.millis(1500));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        javafx.animation.SequentialTransition seq =
                new javafx.animation.SequentialTransition(fadeIn, hold, fadeOut);
        seq.setOnFinished(e -> overlay.getChildren().remove(toast));
        seq.play();
    }

    // ══════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════

    private VBox buatSubmenuBox() {
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
        return submenu;
    }

    private void pasangSubmenu(Button anchor, boolean leftSide, VBox submenu,
                               Runnable onBersuara, Runnable onInformasi) {
        setupSubmenuBehavior(anchor, leftSide, submenu);
    }

    private void pasangSubmenuKendaraan(Button anchor, boolean leftSide, VBox submenu) {
        setupSubmenuBehavior(anchor, leftSide, submenu);
    }

    /** Logika bersama: positioning, listener, toggle anchor, animasi show/hide. */
    private void setupSubmenuBehavior(Button anchor, boolean leftSide, VBox submenu) {
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

        anchor.setOnAction(e -> {
            if (submenu.isVisible()) {
                hideSubmenu(submenu, leftSide);
            } else {
                showSubmenu(submenu, leftSide, updatePos);
            }
        });
    }

    private void showSubmenu(VBox submenu, boolean leftSide, Runnable updatePos) {
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
    }

    private void hideSubmenu(VBox submenu, boolean leftSide) {
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
    }

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
        return b;
    }

    private void applyBergerakStyle(Button b, boolean moving) {
        String base =
                "-fx-font-family: '" + FONT_MAIN + "';" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: 700;" +
                        "-fx-padding: 10 22 10 22;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-alignment: center-left;" +
                        "-fx-cursor: hand;";
        if (moving) {
            b.setStyle(base +
                    "-fx-background-color: rgba(76, 175, 80, 0.22);" +
                    "-fx-text-fill: #1b5e20;" +
                    "-fx-border-color: #4caf50;");
        } else {
            b.setStyle(base +
                    "-fx-background-color: rgba(244, 67, 54, 0.18);" +
                    "-fx-text-fill: #b71c1c;" +
                    "-fx-border-color: #e53935;");
        }
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