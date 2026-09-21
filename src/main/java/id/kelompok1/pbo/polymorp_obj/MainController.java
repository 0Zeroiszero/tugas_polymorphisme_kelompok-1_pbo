package id.kelompok1.pbo.polymorp_obj;

import id.kelompok1.pbo.polymorp_obj.model.*;
import id.kelompok1.pbo.polymorp_obj.ui.ClassCard;
import id.kelompok1.pbo.polymorp_obj.ui.DetailPage;
import id.kelompok1.pbo.polymorp_obj.ui.IsiBensinPage;
import id.kelompok1.pbo.polymorp_obj.util.SoundManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController {

    private static final String FONT = "Poppins";

    @FXML
    private StackPane rootPane;

    @FXML
    public void initialize() {
        tampilkanLanding();
    }

    // ══════════════════════════════════════════════════════════
    // LANDING
    // ══════════════════════════════════════════════════════════
    private void tampilkanLanding() {
        rootPane.getChildren().clear();
        SoundManager.stopAll();     // hentikan semua suara

        Label judul = new Label("Polymorphisme");
        judul.setStyle("-fx-font-family:'" + FONT + "';-fx-font-size:56px;" +
                "-fx-font-weight:600;-fx-text-fill:#1a1a1a;");
        Label subJudul = new Label("(Banyak Bentuk)");
        subJudul.setStyle("-fx-font-family:'" + FONT + "';-fx-font-size:22px;" +
                "-fx-text-fill:#555;");

        ClassCard cardHewan = new ClassCard(
                "Hewan",
                new String[]{"Hewan::bersuara"},
                new ClassCard.Item[]{
                        new ClassCard.Item("Kucing", "kucing.png",
                                () -> bukaDetailHewan(new Kucing(), "bagian-kucing.jpg")),
                        new ClassCard.Item("Burung", "burung.png",
                                () -> bukaDetailHewan(new Burung(), "bagian-burung.jpg"))
                }
        );

        ClassCard cardKendaraan = new ClassCard(
                "Kendaraan",
                new String[]{"Kendaraan::bergerak", "Kendaraan::isiBensin"},
                new ClassCard.Item[]{
                        new ClassCard.Item("Mobil", "mobil.png",
                                () -> bukaDetailKendaraan(new Mobil(),
                                        "background_mobil_motor.jpeg", "mobil_2d-point.png")),
                        new ClassCard.Item("Motor", "motor.png",
                                () -> bukaDetailKendaraan(new Motor(),
                                        "background_mobil_motor.jpeg", "motor.png"))
                }
        );

        HBox cards = new HBox(50, cardHewan, cardKendaraan);
        cards.setAlignment(Pos.CENTER);

        VBox konten = new VBox(28, judul, subJudul, cards);
        konten.setAlignment(Pos.CENTER);

        Button btnExit = new Button("Keluar");
        String exitNormal = "-fx-font-family:'" + FONT + "';" +
                "-fx-background-color:rgba(255,255,255,0.9);-fx-text-fill:#b71c1c;" +
                "-fx-font-weight:600;-fx-font-size:14px;-fx-padding:8 22 8 22;" +
                "-fx-background-radius:12;-fx-border-color:rgba(183,28,28,0.35);" +
                "-fx-border-radius:12;-fx-border-width:1;-fx-cursor:hand;";
        String exitHover = "-fx-font-family:'" + FONT + "';" +
                "-fx-background-color:#b71c1c;-fx-text-fill:white;" +
                "-fx-font-weight:600;-fx-font-size:14px;-fx-padding:8 22 8 22;" +
                "-fx-background-radius:12;-fx-border-color:#b71c1c;" +
                "-fx-border-radius:12;-fx-border-width:1;-fx-cursor:hand;";
        btnExit.setStyle(exitNormal);
        btnExit.setCursor(Cursor.HAND);
        btnExit.setOnMouseEntered(e -> btnExit.setStyle(exitHover));
        btnExit.setOnMouseExited(e -> btnExit.setStyle(exitNormal));
        btnExit.setOnAction(e -> {
            SoundManager.stopAll();
            Platform.exit();
        });

        StackPane.setAlignment(btnExit, Pos.TOP_RIGHT);
        StackPane.setMargin(btnExit, new Insets(20));

        rootPane.getChildren().addAll(konten, btnExit);
    }

    // ══════════════════════════════════════════════════════════
    // DETAIL HEWAN
    // ══════════════════════════════════════════════════════════
    private void bukaDetailHewan(Hewan hewan, String fileGambar) {
        String jenis = hewan.getClass().getSimpleName();
        DetailPage detail = new DetailPage(fileGambar, this::tampilkanLanding);

        double x, y, w, h;
        boolean submenuKiri;
        if (jenis.equals("Kucing")) {
            x = 0.65; y = 0.65; w = 0.16; h = 0.30; submenuKiri = true;
        } else {
            x = 0.40; y = 0.43; w = 0.22; h = 0.40; submenuKiri = false;
        }

        Button anchor = detail.addOrangeButton("OPSI", x, y, w, h, true);

        detail.showSubmenuOn(anchor, submenuKiri,
                () -> {                                        // 🔊 Bersuara
                    detail.showToast("Memainkan suara...");
                    String suara = hewan.bersuara();           // ← play WAV + return
                    tampilSuara(jenis, suara);
                },
                () -> tampilInformasiHewan(hewan)
        );

        rootPane.getChildren().setAll(detail);
    }

    private void tampilSuara(String jenis, String suara) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Suara " + jenis);
        a.setHeaderText(null);
        a.setContentText("Hewan h = new " + jenis + "();\n" +
                "h.bersuara();\n\nOutput: \"" + suara + "\"");
        a.initOwner(rootPane.getScene().getWindow());
        a.showAndWait();
    }

    private void tampilInformasiHewan(Hewan hewan) {
        String nama = hewan.getClass().getSimpleName();
        String lain = nama.equals("Kucing") ? "Burung" : "Kucing";

        String suaraSaya = hewan.getSuaraKhas();           // ← dari objek ini
        Hewan objekLain = nama.equals("Kucing") ? new Burung() : new Kucing();
        String suaraLain = objekLain.getSuaraKhas();       // ← dari objek lain

        String pesan =
                nama + " adalah bagian dari class Hewan.\n\n" +
                        "    Hewan h = new " + nama + "();\n" +
                        "    h.bersuara();   → \"" + suaraSaya + "\"\n\n" +
                        "    Hewan h2 = new " + lain + "();\n" +
                        "    h2.bersuara();  → \"" + suaraLain + "\"\n\n" +
                        "Satu method, banyak perilaku. Itulah POLIMORFISME.";

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Polimorfisme: " + nama);
        a.setHeaderText(nama + " — bagian dari class Hewan");
        a.setContentText(pesan);
        a.initOwner(rootPane.getScene().getWindow());
        a.showAndWait();
    }

    // ══════════════════════════════════════════════════════════
    // DETAIL KENDARAAN
    // ══════════════════════════════════════════════════════════
    private void bukaDetailKendaraan(Kendaraan kendaraan,
                                     String fileGambar,
                                     String fileKendaraan) {
        String jenis = kendaraan.getClass().getSimpleName();
        DetailPage detail = new DetailPage(fileGambar, this::tampilkanLanding);

        double yPersen, heightPersen, duration;
        boolean moveLeft;
        if (jenis.equals("Mobil")) {
            yPersen = 0.95; heightPersen = 0.5; duration = 1; moveLeft = true;
        } else {
            yPersen = 0.90; heightPersen = 0.4; duration = 2; moveLeft = false;
        }

        detail.addMovingVehicle(fileKendaraan, yPersen, heightPersen, duration, moveLeft);

        // Sambungkan animasi UI ke model
        kendaraan.setMoveCallbacks(
                () -> detail.startVehicle(),
                () -> detail.pauseVehicle()
        );

        Button anchor = detail.addOrangeButton("OPSI", 0.20, 0.85, 0.18, 0.10);

        detail.showVehicleSubmenuOn(anchor, false,
                () -> kendaraan.bergerak(),                    // toggle + animasi + suara
                () -> bukaIsiBensin(kendaraan, fileGambar, fileKendaraan),
                () -> tampilInformasiKendaraan(kendaraan)
        );

        rootPane.getChildren().setAll(detail);
    }

    private void bukaIsiBensin(Kendaraan kendaraan,
                               String fileGambar,
                               String fileKendaraan) {
        kendaraan.stopMoving();    // pastikan berhenti dulu

        IsiBensinPage page = new IsiBensinPage(
                "spbu.jpeg",
                fileKendaraan,
                kendaraan.getClass().getSimpleName(),
                kendaraan.getJenisBBM(),                // ← dari private atribut
                () -> bukaDetailKendaraan(kendaraan, fileGambar, fileKendaraan)
        );

        rootPane.getChildren().setAll(page);
    }

    private void tampilInformasiKendaraan(Kendaraan kendaraan) {
        String nama = kendaraan.getClass().getSimpleName();
        String lain = nama.equals("Mobil") ? "Motor" : "Mobil";

        String gerakSaya = kendaraan.bergerak() == kendaraan.isMoving()
                ? null : null; // placeholder (hindari memicu toggle tak sengaja)
        // Baca info tanpa memicu toggle:
        String isiSaya = kendaraan.isiBensin();
        Kendaraan objekLain = nama.equals("Mobil") ? new Motor() : new Mobil();
        String isiLain = objekLain.isiBensin();

        String pesan =
                nama + " adalah bagian dari class Kendaraan.\n\n" +
                        "    Kendaraan k = new " + nama + "();\n" +
                        "    k.isiBensin();   → \"" + isiSaya + "\"\n\n" +
                        "    Kendaraan k2 = new " + lain + "();\n" +
                        "    k2.isiBensin();  → \"" + isiLain + "\"\n\n" +
                        "Satu method, banyak perilaku. Itulah POLIMORFISME.";

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Polimorfisme: " + nama);
        a.setHeaderText(nama + " — bagian dari class Kendaraan");
        a.setContentText(pesan);
        a.initOwner(rootPane.getScene().getWindow());
        a.showAndWait();
    }
}