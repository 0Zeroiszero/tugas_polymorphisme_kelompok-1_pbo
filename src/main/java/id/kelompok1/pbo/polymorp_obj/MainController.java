package id.kelompok1.pbo.polymorp_obj;

import id.kelompok1.pbo.polymorp_obj.ui.IsiBensinPage;
import id.kelompok1.pbo.polymorp_obj.model.*;
import id.kelompok1.pbo.polymorp_obj.ui.ClassCard;
import id.kelompok1.pbo.polymorp_obj.ui.DetailPage;
import id.kelompok1.pbo.polymorp_obj.util.SoundManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;

public class MainController {

    private static final String FONT = "Poppins";

    @FXML
    private StackPane rootPane;

    @FXML
    public void initialize() {
        tampilkanLanding();
    }

    // ──────────────────────────────────────────────────────────
    // LANDING PAGE
    // ──────────────────────────────────────────────────────────
    private void tampilkanLanding() {
        rootPane.getChildren().clear();

        Label judul = new Label("Polymorphisme");
        judul.setStyle(
                "-fx-font-family: '" + FONT + "';" +
                        "-fx-font-size: 56px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-text-fill: #1a1a1a;"
        );

        Label subJudul = new Label("(Banyak Bentuk)");
        subJudul.setStyle(
                "-fx-font-family: '" + FONT + "';" +
                        "-fx-font-size: 22px;" +
                        "-fx-text-fill: #555555;"
        );

        ClassCard cardHewan = new ClassCard(
                "Hewan",
                new String[]{"Hewan::bersuara"},
                new ClassCard.Item[]{
                        new ClassCard.Item("Kucing", "kucing.png",
                                () -> bukaDetailHewan("Kucing", "bagian-kucing.jpg")),
                        new ClassCard.Item("Burung", "burung.png",
                                () -> bukaDetailHewan("Burung", "bagian-burung.jpg"))
                }
        );

        ClassCard cardKendaraan = new ClassCard(
                "Kendaraan",
                new String[]{"Kendaraan::bergerak", "Kendaraan::isiBensin"},
                new ClassCard.Item[]{
                        new ClassCard.Item("Mobil", "mobil.png",
                                () -> bukaDetailKendaraan("Mobil",
                                        "background_mobil_motor.jpeg",
                                        "mobil_2d-point.png")),
                        new ClassCard.Item("Motor", "motor.png",
                                () -> bukaDetailKendaraan("Motor",
                                        "background_mobil_motor.jpeg",
                                        "motor-org.png"))
                }
        );

        HBox cards = new HBox(50, cardHewan, cardKendaraan);
        cards.setAlignment(Pos.CENTER);

        VBox konten = new VBox(28, judul, subJudul, cards);
        konten.setAlignment(Pos.CENTER);

        // ── Tombol Keluar (pojok kanan atas) ─────────────────
        Button btnExit = new Button("Keluar");
        btnExit.setStyle(
                "-fx-font-family: '" + FONT + "';" +
                        "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                        "-fx-text-fill: #b71c1c;" +
                        "-fx-font-weight: 600;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 22 8 22;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: rgba(183, 28, 28, 0.35);" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0.2, 0, 3);"
        );
        btnExit.setCursor(Cursor.HAND);
        btnExit.setOnMouseEntered(e -> btnExit.setStyle(
                "-fx-font-family: '" + FONT + "';" +
                        "-fx-background-color: #b71c1c;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: 600;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 22 8 22;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #b71c1c;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(183,28,28,0.5), 14, 0.3, 0, 5);"
        ));
        btnExit.setOnMouseExited(e -> btnExit.setStyle(
                "-fx-font-family: '" + FONT + "';" +
                        "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                        "-fx-text-fill: #b71c1c;" +
                        "-fx-font-weight: 600;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 22 8 22;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: rgba(183, 28, 28, 0.35);" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0.2, 0, 3);"
        ));

        btnExit.setOnAction(e -> {
            SoundManager.stopAll();
            Platform.exit();
        });

        StackPane.setAlignment(btnExit, Pos.TOP_RIGHT);
        StackPane.setMargin(btnExit, new Insets(20));

        rootPane.getChildren().addAll(konten, btnExit);
    }

    private void tampilInformasi(String jenis) {
        String pesan;

        if (jenis.equals("Kucing")) {
            pesan =
                    "Kucing adalah salah satu bentuk dari class Hewan.\n\n" +
                            "Ketika kita menulis:\n" +
                            "    Hewan h = new Kucing();\n" +
                            "    h.bersuara();\n\n" +
                            "Java akan memanggil method bersuara() milik Kucing, " +
                            "sehingga yang keluar adalah suara \"Meong\".\n\n" +
                            "Kalau kita menulis:\n" +
                            "    Hewan h = new Burung();\n" +
                            "    h.bersuara();\n\n" +
                            "Java akan memanggil method bersuara() milik Burung, " +
                            "sehingga yang keluar adalah suara \"Cuit cuit\".\n\n" +
                            "Referensinya sama-sama bertipe Hewan, tetapi " +
                            "method yang dijalankan berbeda tergantung objek aslinya.\n\n" +
                            "Inilah POLIMORFISME:\n" +
                            "satu method, banyak perilaku.";
        } else {
            pesan =
                    "Burung adalah salah satu bentuk dari class Hewan.\n\n" +
                            "Ketika kita menulis:\n" +
                            "    Hewan h = new Burung();\n" +
                            "    h.bersuara();\n\n" +
                            "Java akan memanggil method bersuara() milik Burung, " +
                            "sehingga yang keluar adalah suara \"Cuit cuit\".\n\n" +
                            "Kalau kita menulis:\n" +
                            "    Hewan h = new Kucing();\n" +
                            "    h.bersuara();\n\n" +
                            "Java akan memanggil method bersuara() milik Kucing, " +
                            "sehingga yang keluar adalah suara \"Meong\".\n\n" +
                            "Referensinya sama-sama bertipe Hewan, tetapi " +
                            "method yang dijalankan berbeda tergantung objek aslinya.\n\n" +
                            "Inilah POLIMORFISME:\n" +
                            "satu method, banyak perilaku.";
        }

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Polimorfisme: " + jenis);
        a.setHeaderText(jenis + " — bagian dari class Hewan");
        a.setContentText(pesan);
        a.initOwner(rootPane.getScene().getWindow());
        a.showAndWait();
    }

    // ──────────────────────────────────────────────────────────
    // DETAIL HEWAN
    // ──────────────────────────────────────────────────────────
    private void bukaDetailHewan(String jenis, String fileGambar) {
        String fileSuara = jenis.equals("Kucing") ? "kucing.wav" : "burung.wav";

        DetailPage detail = new DetailPage(fileGambar, fileSuara, this::tampilkanLanding);

        double x, y, w, h;
        boolean submenuKiri;

        if (jenis.equals("Kucing")) {
            x = 0.65;  y = 0.65;  w = 0.16;  h = 0.30;
            submenuKiri = true;
        } else {
            x = 0.40;  y = 0.43;  w = 0.22;  h = 0.40;
            submenuKiri = false;
        }

        // Tombol transparan
        Button anchor = detail.addOrangeButton("OPSI", x, y, w, h, true);

        detail.showSubmenuOn(anchor, submenuKiri,
                () -> {                                        // 🔊 Bersuara
                    detail.showToast("Memainkan suara...");
                    detail.playSound();                        // ← suara HANYA di sini
                },
                () -> {                                        // ℹ Informasi
                    tampilInformasi(jenis);                    // ← TIDAK ada playSound()
                }
        );

        rootPane.getChildren().setAll(detail);
    }

    private void bukaDetailKendaraan(String jenis, String fileGambar, String fileKendaraan) {
        // Suara berbeda untuk Mobil dan Motor
        String fileSuara = jenis.equals("Mobil") ? "porsche.wav" : "motor.wav";

        DetailPage detail = new DetailPage(fileGambar, fileSuara, this::tampilkanLanding);

        double yPersen, heightPersen, duration;
        boolean moveLeft;

        if (jenis.equals("Mobil")) {
            yPersen = 0.95;
            heightPersen = 0.5;
            duration = 1;
            moveLeft = true;     // mobil bergerak ke KIRI
        } else { // Motor
            yPersen = 0.90;
            heightPersen = 0.4;
            duration = 1;
            moveLeft = false;    // motor bergerak ke KANAN
        }

        detail.addMovingVehicle(fileKendaraan, yPersen, heightPersen, duration, moveLeft);

        Button anchor = detail.addOrangeButton("OPSI", 0.20, 0.85, 0.18, 0.10);

        detail.showVehicleSubmenuOn(anchor, false,
                () -> bukaIsiBensin(jenis, fileGambar, fileKendaraan),   // ← ganti dari tampilIsiBensin
                () -> tampilPolimorfismeKendaraan(jenis)
        );

        rootPane.getChildren().setAll(detail);
    }

    private void bukaIsiBensin(String jenis, String fileGambar, String fileKendaraan) {
        String fuelType = jenis.equals("Mobil") ? "Pertamax 92" : "Pertalite";

        IsiBensinPage page = new IsiBensinPage(
                "spbu.jpeg",
                fileKendaraan,
                jenis,
                fuelType,
                () -> bukaDetailKendaraan(jenis, fileGambar, fileKendaraan)
        );

        rootPane.getChildren().setAll(page);
    }

    private void tampilIsiBensin(String jenis) {
        String pesan;

        if (jenis.equals("Mobil")) {
            pesan =
                    "Mobil mengisi bahan bakar:\n\n" +
                            "    " + new Mobil().isiBensin() + "\n\n" +
                            "Method isiBensin() di-override oleh Mobil, " +
                            "sehingga hasilnya berbeda dengan Motor.";
        } else {
            pesan =
                    "Motor mengisi bahan bakar:\n\n" +
                            "    " + new Motor().isiBensin() + "\n\n" +
                            "Method isiBensin() di-override oleh Motor, " +
                            "sehingga hasilnya berbeda dengan Mobil.";
        }

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Isi Bensin: " + jenis);
        a.setHeaderText(jenis + " — cara mengisi bahan bakar");
        a.setContentText(pesan);
        a.initOwner(rootPane.getScene().getWindow());
        a.showAndWait();
    }

    private void tampilPolimorfismeKendaraan(String jenis) {
        String pesan;

        if (jenis.equals("Mobil")) {
            pesan =
                    "Mobil adalah salah satu bentuk dari class Kendaraan.\n\n" +
                            "Ketika kita menulis:\n" +
                            "    Kendaraan k = new Mobil();\n" +
                            "    k.bergerak();\n" +
                            "    k.isiBensin();\n\n" +
                            "Java akan memanggil method milik Mobil:\n" +
                            "    bergerak()  → \"" + new Mobil().bergerak() + "\"\n" +
                            "    isiBensin() → \"" + new Mobil().isiBensin() + "\"\n\n" +
                            "Kalau kita menulis:\n" +
                            "    Kendaraan k = new Motor();\n" +
                            "    k.bergerak();\n" +
                            "    k.isiBensin();\n\n" +
                            "Java akan memanggil method milik Motor:\n" +
                            "    bergerak()  → \"" + new Motor().bergerak() + "\"\n" +
                            "    isiBensin() → \"" + new Motor().isiBensin() + "\"\n\n" +
                            "Inilah POLIMORFISME:\n" +
                            "satu method, banyak perilaku.";
        } else {
            pesan =
                    "Motor adalah salah satu bentuk dari class Kendaraan.\n\n" +
                            "Ketika kita menulis:\n" +
                            "    Kendaraan k = new Motor();\n" +
                            "    k.bergerak();\n" +
                            "    k.isiBensin();\n\n" +
                            "Java akan memanggil method milik Motor:\n" +
                            "    bergerak()  → \"" + new Motor().bergerak() + "\"\n" +
                            "    isiBensin() → \"" + new Motor().isiBensin() + "\"\n\n" +
                            "Kalau kita menulis:\n" +
                            "    Kendaraan k = new Mobil();\n" +
                            "    k.bergerak();\n" +
                            "    k.isiBensin();\n\n" +
                            "Java akan memanggil method milik Mobil:\n" +
                            "    bergerak()  → \"" + new Mobil().bergerak() + "\"\n" +
                            "    isiBensin() → \"" + new Mobil().isiBensin() + "\"\n\n" +
                            "Inilah POLIMORFISME:\n" +
                            "satu method, banyak perilaku.";
        }

        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Polimorfisme: " + jenis);
        a.setHeaderText(jenis + " — bagian dari class Kendaraan");
        a.setContentText(pesan);
        a.initOwner(rootPane.getScene().getWindow());
        a.showAndWait();
    }
}