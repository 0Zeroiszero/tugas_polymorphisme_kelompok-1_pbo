package id.kelompok1.pbo.polymorp_obj;

import id.kelompok1.pbo.polymorp_obj.ui.CeritaPage;
import id.kelompok1.pbo.polymorp_obj.util.FontLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utilitas screenshot (khusus pengujian, tidak ikut dalam JAR utama).
 * Menjalankan halaman cerita secara langsung dan menyimpan snapshot
 * pada detik-detik tertentu ke /tmp/shots/.
 *
 * Dipakai: xvfb-run java -cp target/classes:<deps> ...SnapshotRunner
 */
public class SnapshotRunner extends Application {

    /** Mode jepret: "cerita" (default) atau "pages" (halaman aplikasi). */
    private static String MODE = "cerita";

    /** Titik jepret (detik sejak cerita dimulai, nama file). */
    private static final Object[][] TITIK = {
            { 2.5,  "a1-taman-awal" },
            { 6.5,  "a1-taman-meong" },
            { 11.8, "a1-burung-melintas" },
            { 16.5, "a2-burung-ramai" },
            { 21.5, "a2-burung-coba-terbang" },
            { 24.5, "a2-burung-naik" },
            { 28.6, "a3-motor-laju" },
            { 33.4, "a3-mobil-lewat" },
            { 41.5, "a4-spbu-masuk" },
            { 44.8, "a4-pengendara-turun" },
            { 47.5, "a4-isi-bersama" },
            { 52.0, "a5-struk" },
            { 55.0, "a6-selesai" },
    };

    @Override
    public void start(Stage stage) {
        FontLoader.loadAll();

        File dir = new File("/tmp/shots");
        if (!dir.exists()) dir.mkdirs();

        if ("pages".equals(MODE)) {
            jalankanModeHalaman(stage);
        } else {
            jalankanModeCerita(stage);
        }
    }

    /** Mode "cerita": tampilkan CeritaPage dan jepret tiap adegan. */
    private void jalankanModeCerita(Stage stage) {
        CeritaPage cerita = new CeritaPage(Platform::exit);
        Scene scene = new Scene(cerita, 1400, 800);
        stage.setTitle("Snapshot Runner");
        stage.setScene(scene);
        stage.show();

        cerita.putar();

        Timeline jadwal = new Timeline();
        for (Object[] t : TITIK) {
            final String nama = (String) t[1];
            jadwal.getKeyFrames().add(new KeyFrame(Duration.seconds((Double) t[0]),
                    e -> jepret(scene, nama)));
        }
        // tutup aplikasi setelah snapshot terakhir
        jadwal.getKeyFrames().add(new KeyFrame(Duration.seconds(58),
                e -> {
                    System.out.println("[SNAPSHOT] selesai — keluar");
                    Platform.exit();
                }));
        jadwal.play();

        // pengaman: keluar paksa setelah 70 detik
        Timeline pengaman = new Timeline(new KeyFrame(Duration.seconds(70),
                e -> Platform.exit()));
        pengaman.play();
    }

    /**
     * Mode "pages": buka aplikasi lengkap (FXML) dan navigasi ke setiap
     * halaman via refleksi, untuk screenshot landing/detail/isi bensin.
     */
    private void jalankanModeHalaman(Stage stage) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/id/kelompok1/pbo/polymorp_obj/main-view.fxml"));
            javafx.scene.layout.StackPane root = loader.load();
            MainController mc = loader.getController();

            Scene scene = new Scene(root, 1400, 800);
            stage.setTitle("Snapshot Runner — Halaman");
            stage.setScene(scene);
            stage.show();

            java.lang.reflect.Method mHewan = MainController.class
                    .getDeclaredMethod("bukaDetailHewan", String.class, String.class);
            mHewan.setAccessible(true);
            java.lang.reflect.Method mKendaraan = MainController.class
                    .getDeclaredMethod("bukaDetailKendaraan",
                            String.class, String.class, String.class);
            mKendaraan.setAccessible(true);
            java.lang.reflect.Method mIsi = MainController.class
                    .getDeclaredMethod("bukaIsiBensin",
                            String.class, String.class, String.class);
            mIsi.setAccessible(true);
            java.lang.reflect.Method mLanding = MainController.class
                    .getDeclaredMethod("tampilkanLanding");
            mLanding.setAccessible(true);

            Timeline jadwal = new Timeline(
                    new KeyFrame(Duration.seconds(0.8), e -> jepret(scene, "pg-landing")),
                    new KeyFrame(Duration.seconds(1.6), e ->
                            panggil(mHewan, mc, "Kucing", "bagian-kucing.jpg")),
                    new KeyFrame(Duration.seconds(2.8), e -> jepret(scene, "pg-kucing")),
                    new KeyFrame(Duration.seconds(3.4), e ->
                            panggil(mHewan, mc, "Burung", "bagian-burung.jpg")),
                    new KeyFrame(Duration.seconds(4.6), e -> jepret(scene, "pg-burung")),
                    new KeyFrame(Duration.seconds(5.2), e ->
                            panggil(mKendaraan, mc, "Mobil",
                                    "background_mobil_motor.jpeg", "mobil_2d-point.png")),
                    new KeyFrame(Duration.seconds(6.4), e -> jepret(scene, "pg-mobil")),
                    new KeyFrame(Duration.seconds(7.0), e ->
                            panggil(mIsi, mc, "Mobil",
                                    "background_mobil_motor.jpeg", "mobil_2d-point.png")),
                    new KeyFrame(Duration.seconds(9.6), e -> jepret(scene, "pg-isibensin-mobil")),
                    new KeyFrame(Duration.seconds(10.2), e ->
                            panggil(mKendaraan, mc, "Motor",
                                    "background_mobil_motor.jpeg", "motor-org.png")),
                    new KeyFrame(Duration.seconds(11.4), e -> jepret(scene, "pg-motor")),
                    new KeyFrame(Duration.seconds(12.0), e ->
                            panggil(mIsi, mc, "Motor",
                                    "background_mobil_motor.jpeg", "motor-org.png")),
                    new KeyFrame(Duration.seconds(14.6), e -> jepret(scene, "pg-isibensin-motor")),
                    new KeyFrame(Duration.seconds(15.6), e -> Platform.exit())
            );
            jadwal.play();
            new Timeline(new KeyFrame(Duration.seconds(30), e -> Platform.exit())).play();
        } catch (Exception e) {
            System.err.println("[SNAPSHOT] Mode halaman gagal: " + e);
            e.printStackTrace();
            Platform.exit();
        }
    }

    private void panggil(java.lang.reflect.Method m, Object obj, Object... args) {
        try {
            m.invoke(obj, args);
        } catch (Exception e) {
            System.err.println("[SNAPSHOT] invoke gagal: " + e);
        }
    }

    private void jepret(Scene scene, String nama) {
        try {
            // diagnostik: ukuran overlay/adegan + isi adegan
            try {
                javafx.scene.Parent r = scene.getRoot();
                java.lang.reflect.Field fOver = r.getClass().getDeclaredField("overlay");
                fOver.setAccessible(true);
                javafx.scene.layout.Pane overlay = (javafx.scene.layout.Pane) fOver.get(r);
                java.lang.reflect.Field fAdegan = r.getClass().getDeclaredField("adegan");
                fAdegan.setAccessible(true);
                javafx.scene.layout.Pane adegan = (javafx.scene.layout.Pane) fAdegan.get(r);
                System.out.println("[DIAG] " + nama + " overlay=" + overlay.getWidth()
                        + "x" + overlay.getHeight() + " adegan=" + adegan.getWidth()
                        + "x" + adegan.getHeight() + " anak=" + adegan.getChildren().size());
                int batas = 0;
                for (Node n : adegan.getChildren()) {
                    if (batas++ >= 8) break;
                    ImageView iv = (n instanceof ImageView i2) ? i2 : null;
                    System.out.println("   - " + n.getClass().getSimpleName()
                            + (iv != null && iv.getImage() != null
                            ? " img=" + iv.getImage().getWidth() + "x" + iv.getImage().getHeight()
                            : "")
                            + " fitH=" + (iv != null ? iv.getFitHeight() : -1)
                            + " lay=(" + n.getLayoutX() + "," + n.getLayoutY() + ")"
                            + " bounds=" + n.getLayoutBounds().getWidth() + "x"
                            + n.getLayoutBounds().getHeight()
                            + " vis=" + n.isVisible() + " op=" + n.getOpacity());
                }
            } catch (Exception ex) {
                System.out.println("[DIAG] gagal: " + ex);
            }

            WritableImage img = scene.snapshot(null);
            int w = (int) img.getWidth();
            int h = (int) img.getHeight();
            PixelReader pr = img.getPixelReader();
            byte[] bgra = new byte[w * h * 4];
            pr.getPixels(0, 0, w, h, PixelFormat.getByteBgraPreInstance(), bgra, 0, w * 4);
            int[] argb = new int[w * h];
            for (int i = 0; i < w * h; i++) {
                int b = bgra[i * 4] & 0xFF;
                int g = bgra[i * 4 + 1] & 0xFF;
                int r = bgra[i * 4 + 2] & 0xFF;
                int a = bgra[i * 4 + 3] & 0xFF;
                argb[i] = (a << 24) | (r << 16) | (g << 8) | b;
            }
            BufferedImage buffered = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            buffered.setRGB(0, 0, w, h, argb, 0, w);
            File keluar = new File("/tmp/shots/" + nama + ".png");
            javax.imageio.ImageIO.write(buffered, "png", keluar);
            System.out.println("[SNAPSHOT] " + keluar.getAbsolutePath() + " (" + w + "x" + h + ")");
        } catch (Exception e) {
            System.err.println("[SNAPSHOT] Gagal " + nama + ": " + e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) MODE = args[0];
        launch(args);
    }
}
