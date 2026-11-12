package id.kelompok1.pbo.polymorp_obj.ui;

import id.kelompok1.pbo.polymorp_obj.util.SoundManager;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Halaman ALUR CERITA — seluruh kisah diputar di dalam SATU frame.
 *
 * Adegan 1 : Kucing berjalan-jalan di taman, mengeong sekali
 *            (kucing diduplikasi agar tampak ramai, rumput bergoyang).
 * Adegan 2 : Seekor burung terbang lalu adegan berpindah ke burung;
 *            burung cuit-cuit (burung diduplikasi di bawahnya).
 *            Burung mencoba terbang → adegan berpindah ke jalan raya.
 * Adegan 3 : Pengendara motor melaju kencang (gambar seq + suara motor),
 *            disusul mobil yang lewat (gambar seq + suara mobil).
 * Adegan 4 : Motor dan mobil mengisi bensin self-service di SPBU
 *            (pengendara turun dari motor), lalu struk bukti bayar
 *            ditampilkan sebagai gambar hasil generate.
 */
public class CeritaPage extends StackPane {

    private static final String IMG_BASE = "/id/kelompok1/pbo/polymorp_obj/gambar/";
    private static final String CERITA_BASE = IMG_BASE + "cerita/";
    private static final String SOUND_BASE = "/id/kelompok1/pbo/polymorp_obj/sounds/";
    private static final String FONT_MAIN = "Poppins";

    private final Pane overlay;
    private final Pane adegan;            // panggung adegan aktif (tetap satu frame)
    private final Label caption;          // narasi adegan
    private final Button btnUlangi;
    private final Runnable onBack;

    private Timeline master;              // penjadwal utama alur cerita
    private final List<Timeline> animasiAktif = new ArrayList<>();
    private final List<javafx.animation.Animation> transisiAktif = new ArrayList<>();
    private final Map<String, Image> cacheGambar = new HashMap<>();

    public CeritaPage(Runnable onBack) {
        this.onBack = onBack;

        setStyle("-fx-background-color: #101418;");

        // ── Panggung utama ────────────────────────────────────
        overlay = new Pane();
        overlay.setPickOnBounds(false);

        adegan = new Pane();
        adegan.setPickOnBounds(false);
        adegan.setOpacity(0);
        // Pane biasa tidak mengatur ukuran anak — ikat ukuran adegan
        // secara eksplisit ke ukuran panggung (overlay)
        adegan.layoutXProperty().bind(overlay.widthProperty().multiply(0));
        adegan.layoutYProperty().bind(overlay.heightProperty().multiply(0));
        adegan.prefWidthProperty().bind(overlay.widthProperty());
        adegan.prefHeightProperty().bind(overlay.heightProperty());
        adegan.maxWidthProperty().bind(overlay.widthProperty());
        adegan.maxHeightProperty().bind(overlay.heightProperty());
        overlay.getChildren().add(adegan);

        // ── Judul kecil di atas ───────────────────────────────
        Label judul = new Label("Alur Cerita Polimorfisme");
        judul.setFont(Font.font(FONT_MAIN, FontWeight.BOLD, 16));
        judul.setTextFill(Color.WHITE);
        judul.setStyle(
                "-fx-background-color: rgba(15, 20, 24, 0.72);" +
                        "-fx-background-radius: 14;" +
                        "-fx-padding: 8 18 8 18;");
        StackPane.setAlignment(judul, Pos.TOP_CENTER);
        StackPane.setMargin(judul, new Insets(14, 0, 0, 0));

        // ── Narasi adegan (bawah) ─────────────────────────────
        caption = new Label(" ");
        caption.setWrapText(true);
        caption.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        caption.setFont(Font.font(FONT_MAIN, FontWeight.SEMI_BOLD, 15));
        caption.setTextFill(Color.WHITE);
        caption.setMaxWidth(760);
        caption.setStyle(
                "-fx-background-color: rgba(15, 20, 24, 0.78);" +
                        "-fx-background-radius: 14;" +
                        "-fx-padding: 10 22 10 22;");
        StackPane.setAlignment(caption, Pos.BOTTOM_CENTER);
        StackPane.setMargin(caption, new Insets(0, 0, 18, 0));
        caption.setMouseTransparent(true);

        // ── Tombol-tombol ─────────────────────────────────────
        Button btnKembali = tombolKecil("◀  Kembali");
        btnKembali.setOnAction(e -> {
            berhentiSemua();
            onBack.run();
        });
        StackPane.setAlignment(btnKembali, Pos.TOP_LEFT);
        StackPane.setMargin(btnKembali, new Insets(14, 0, 0, 14));

        Button btnLewati = tombolKecil("Lewati ke SPBU  ⏭");
        btnLewati.setOnAction(e -> lompatKeSpbu());
        StackPane.setAlignment(btnLewati, Pos.TOP_RIGHT);
        StackPane.setMargin(btnLewati, new Insets(14, 14, 0, 0));

        btnUlangi = tombolKecil("↺  Putar Ulang");
        btnUlangi.setVisible(false);
        btnUlangi.setOnAction(e -> {
            btnUlangi.setVisible(false);
            putar();
        });
        StackPane.setAlignment(btnUlangi, Pos.TOP_RIGHT);
        StackPane.setMargin(btnUlangi, new Insets(60, 14, 0, 0));

        getChildren().addAll(overlay, judul, caption, btnKembali, btnLewati, btnUlangi);
    }

    // ══════════════════════════════════════════════════════════
    // PENGENDALIAN PUTAR / HENTIKAN
    // ══════════════════════════════════════════════════════════

    /** Mulai memutar alur cerita dari adegan 1 (dipanggil saat halaman tampil). */
    public void putar() {
        berhentiSemua();
        btnUlangi.setVisible(false);
        adegan.getChildren().clear();
        adegan.setOpacity(0);

        // Tunggu panggung punya ukuran, lalu jalankan jadwal utama.
        // Semua perpindahan adegan diatur SATU jadwal utama ini —
        // alur cerita tetap berlangsung dalam satu frame yang sama.
        Runnable mulai = () -> {
            master = new Timeline(
                    new KeyFrame(Duration.seconds(0.0),  e -> adeganTaman()),
                    new KeyFrame(Duration.seconds(13.0), e -> adeganBurung()),
                    new KeyFrame(Duration.seconds(26.5), e -> adeganJalan()),
                    new KeyFrame(Duration.seconds(38.0), e -> adeganSpbu()),
                    new KeyFrame(Duration.seconds(49.5), e -> tahapStruk()),
                    new KeyFrame(Duration.seconds(56.0), e -> selesaiCerita())
            );
            master.play();
            transisiAktif.add(master);
        };
        jalankanSaatSiap(mulai);
    }

    /** Tombol "Lewati ke SPBU": hentikan adegan sekarang, lompat ke adegan 4. */
    private void lompatKeSpbu() {
        berhentiSemua();
        adegan.getChildren().clear();
        adegan.setOpacity(0);
        Runnable mulai = () -> {
            master = new Timeline(
                    new KeyFrame(Duration.seconds(0.0),  e -> adeganSpbu()),
                    new KeyFrame(Duration.seconds(11.5), e -> tahapStruk()),
                    new KeyFrame(Duration.seconds(18.0), e -> selesaiCerita())
            );
            master.play();
            transisiAktif.add(master);
        };
        jalankanSaatSiap(mulai);
    }

    private void selesaiCerita() {
        setCaption("Selesai! Satu method isiBensin() — dua bentuk pengisian berbeda. "
                + "Itulah POLIMORFISME. Tekan \"Putar Ulang\" untuk menonton lagi.");
        btnUlangi.setVisible(true);
        btnUlangi.toFront();
    }

    /** Hentikan seluruh animasi, suara, dan penjadwal. */
    private void berhentiSemua() {
        if (master != null) { master.stop(); master = null; }
        for (Timeline t : animasiAktif) t.stop();
        animasiAktif.clear();
        for (javafx.animation.Animation a : transisiAktif) a.stop();
        transisiAktif.clear();
        SoundManager.stopAll();
    }

    private void trackAnimasi(javafx.animation.Animation a) {
        transisiAktif.add(a);
    }

    private void track(Timeline t) {
        animasiAktif.add(t);
    }

    /** Jalankan blok setelah panggung mendapatkan ukuran (setelah layout). */
    private void jalankanSaatSiap(Runnable blok) {
        if (overlay.getWidth() > 0 && overlay.getHeight() > 0) {
            blok.run();
            return;
        }
        ChangeListener<Number> listener = new ChangeListener<>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends Number> o,
                                Number lama, Number baru) {
                if (overlay.getWidth() > 0 && overlay.getHeight() > 0) {
                    overlay.widthProperty().removeListener(this);
                    overlay.heightProperty().removeListener(this);
                    blok.run();
                }
            }
        };
        overlay.widthProperty().addListener(listener);
        overlay.heightProperty().addListener(listener);
    }

    // ══════════════════════════════════════════════════════════
    // HELPER GAMBAR, ANIMASI & SUARA
    // ══════════════════════════════════════════════════════════

    private Image gambar(String file) {
        Image cached = cacheGambar.get(CERITA_BASE + file);
        if (cached != null) return cached;
        // cari di folder cerita dulu, lalu folder gambar umum (mis. motor.png)
        java.net.URL url = getClass().getResource(CERITA_BASE + file);
        if (url == null) url = getClass().getResource(IMG_BASE + file);
        if (url == null) {
            System.err.println("[CERITA] Gambar tidak ditemukan: " + file);
            return null;
        }
        Image img = new Image(url.toExternalForm());
        cacheGambar.put(CERITA_BASE + file, img);
        return img;
    }

    private Image bg(String file) {
        Image cached = cacheGambar.get(IMG_BASE + file);
        if (cached != null) return cached;
        // cari di folder cerita dulu, lalu folder gambar umum
        java.net.URL url = getClass().getResource(CERITA_BASE + file);
        if (url == null) url = getClass().getResource(IMG_BASE + file);
        if (url == null) {
            System.err.println("[CERITA] Latar tidak ditemukan: " + file);
            return null;
        }
        Image img = new Image(url.toExternalForm());
        cacheGambar.put(IMG_BASE + file, img);
        return img;
    }

    /** Rasio lebar-tertampil (fitHeight × rasio gambar). */
    private Double rasioLebar(ImageView iv) {
        Image img = iv.getImage();
        if (img == null || img.getHeight() <= 0) return null;
        return img.getWidth() / img.getHeight();
    }

    /**
     * Sprite tetap: tinggi & posisi mengikuti persentase panggung.
     * @param yDasar posisi KAKI (bawah) sprite dalam persentase tinggi panggung
     */
    private ImageView spriteTetap(String file, double tinggiPersen,
                                  double xPersen, double yDasar, boolean flip) {
        ImageView iv = new ImageView(gambar(file));
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.fitHeightProperty().bind(adegan.heightProperty().multiply(tinggiPersen));
        Double rasio = rasioLebar(iv);
        if (rasio != null) {
            iv.layoutXProperty().bind(adegan.widthProperty().multiply(xPersen)
                    .subtract(iv.fitHeightProperty().multiply(rasio).divide(2)));
        } else {
            iv.layoutXProperty().bind(adegan.widthProperty().multiply(xPersen));
        }
        iv.layoutYProperty().bind(adegan.heightProperty().multiply(yDasar)
                .subtract(iv.fitHeightProperty()));
        if (flip) iv.setScaleX(-1);
        return iv;
    }

    /** Sprite laju (kendaraan): layoutX bebas diatur manual agar bisa dianimasikan. */
    private ImageView spriteLaju(String file, double tinggiPersen,
                                 double yDasar, boolean flip) {
        ImageView iv = new ImageView(gambar(file));
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.fitHeightProperty().bind(adegan.heightProperty().multiply(tinggiPersen));
        iv.layoutYProperty().bind(adegan.heightProperty().multiply(yDasar)
                .subtract(iv.fitHeightProperty()));
        if (flip) iv.setScaleX(-1);
        return iv;
    }

    /** Latar belakang adegan (cover penuh panggung). */
    private ImageView latar(Image img) {
        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(false);
        iv.fitWidthProperty().bind(adegan.widthProperty());
        iv.fitHeightProperty().bind(adegan.heightProperty());
        return iv;
    }

    /** Tukar isi panggung dengan fade: keluar → bangun baru → masuk. */
    private void gantiAdegan(String teksCaption, Runnable pembuatAdegan) {
        FadeTransition keluar = new FadeTransition(Duration.millis(550), adegan);
        keluar.setFromValue(1);
        keluar.setToValue(0);
        keluar.setOnFinished(e -> {
            adegan.getChildren().clear();
            pembuatAdegan.run();
            setCaption(teksCaption);
            FadeTransition masuk = new FadeTransition(Duration.millis(550), adegan);
            masuk.setFromValue(0);
            masuk.setToValue(1);
            masuk.play();
            trackAnimasi(masuk);
        });
        keluar.play();
        trackAnimasi(keluar);
    }

    /** Fade-in panggung untuk adegan pertama. */
    private void munculkanAdegan(String teksCaption) {
        setCaption(teksCaption);
        FadeTransition masuk = new FadeTransition(Duration.millis(650), adegan);
        masuk.setFromValue(0);
        masuk.setToValue(1);
        masuk.play();
        trackAnimasi(masuk);
    }

    /** Caption narasi di bawah layar. */
    private void setCaption(String teks) {
        caption.setText(teks);
    }

    /** Jadwal kejadian internal sebuah adegan (waktu absolut sejak adegan mulai). */
    private void jadwal(double[] waktu, Runnable[] aksi) {
        Timeline t = new Timeline();
        for (int i = 0; i < waktu.length; i++) {
            final Runnable a = aksi[i];
            t.getKeyFrames().add(new KeyFrame(Duration.seconds(waktu[i]), e -> a.run()));
        }
        t.play();
        track(t);
    }

    /** Animasi langkah (ganti frame sprite) — cycle tak terbatas. */
    private Timeline siklusFrame(ImageView target, Image[] frame, double msPerFrame) {
        Timeline t = new Timeline();
        for (int i = 0; i <= frame.length; i++) {
            final Image f = frame[i % frame.length];
            t.getKeyFrames().add(new KeyFrame(Duration.millis(msPerFrame * i),
                    e -> target.setImage(f)));
        }
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();
        track(t);
        return t;
    }

    /**
     * Gerak melintas horizontal MENGGUNAKAN translateX (aman untuk sprite
     * yang layoutX-nya di-bind maupun tidak). Jarak dalam piksel.
     */
    private TranslateTransition melintas(Node n, double jarakPx, double detik) {
        n.setTranslateX(0);
        TranslateTransition tt = new TranslateTransition(Duration.seconds(detik), n);
        tt.setFromX(0);
        tt.setToX(jarakPx);
        tt.setInterpolator(Interpolator.LINEAR);
        tt.play();
        trackAnimasi(tt);
        return tt;
    }

    /** Bobbing halus naik-turun (terlihat hidup saat berjalan/terbang). */
    private Timeline bobbing(Node n, double jarakPx, double msPerBob) {
        Timeline t = new Timeline(
                new KeyFrame(Duration.ZERO,   new KeyValue(n.translateYProperty(), 0)),
                new KeyFrame(Duration.millis(msPerBob / 2.0),
                        new KeyValue(n.translateYProperty(), -jarakPx,
                                Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(msPerBob),
                        new KeyValue(n.translateYProperty(), 0, Interpolator.EASE_IN))
        );
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();
        track(t);
        return t;
    }

    private void mainkanSekali(String fileSuara) {
        SoundManager.playOnce(SOUND_BASE + fileSuara);
    }

    private void mainkanLoop(String fileSuara) {
        SoundManager.playLoop(SOUND_BASE + fileSuara);
    }

    private void hentikanSuara(String fileSuara) {
        SoundManager.stop(SOUND_BASE + fileSuara);
    }

    /** Tombol kecil seragam untuk kontrol cerita. */
    private Button tombolKecil(String teks) {
        Button b = new Button(teks);
        b.setFont(Font.font(FONT_MAIN, FontWeight.SEMI_BOLD, 13));
        b.setTextFill(Color.web("#1a1a1a"));
        b.setStyle(
                "-fx-background-color: rgba(255,255,255,0.88);" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 7 16 7 16;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.2, 0, 3);");
        b.setCursor(Cursor.HAND);
        return b;
    }

    // ══════════════════════════════════════════════════════════
    // ADEGAN 1 — KUCING BERJALAN-JALAN DI TAMAN (rumput bergoyang)
    // ══════════════════════════════════════════════════════════

    private void adeganTaman() {
        double w = overlay.getWidth();

        adegan.getChildren().add(latar(bg("taman.jpeg")));
        munculkanAdegan(
                "Adegan 1 — Kucing-kucing berjalan-jalan di taman yang ramai. "
                        + "Rumput bergoyang ditiup angin.");

        // ── Rumput bergoyang (gambar hasil generate, dua baris ditimpa) ──
        buatRumputBergoyang();

        // ── Kucing-kucing (diduplikasi agar tampak ramai) ─────
        Image[] jalan = {
                gambar("kucing-walk-1.png"), gambar("kucing-walk-2.png"),
                gambar("kucing-walk-3.png"), gambar("kucing-walk-4.png")
        };

        // Kucing utama: berjalan dari kiri ke tengah, lalu mengeong sekali
        ImageView kucingUtama = spriteTetap("kucing-walk-1.png", 0.20, -0.12, 0.86, false);
        adegan.getChildren().add(kucingUtama);
        Timeline langkahUtama = siklusFrame(kucingUtama, jalan, 170);
        Timeline bobUtama = bobbing(kucingUtama, 6, 340);
        TranslateTransition maju = melintas(kucingUtama, w * 0.56, 4.5);

        // Kucing pendamping — duplikat dengan ukuran/arah/kecepatan berbeda
        double[][] pendamping = {
                // xMulai, yDasar, tinggi, arah(+1 kanan / -1 kiri), detik
                { 1.18, 0.84, 0.13, -1, 7.5 },
                { 1.30, 0.90, 0.16, -1, 9.0 },
                { -0.25, 0.92, 0.15,  1, 8.0 },
                { -0.38, 0.88, 0.11,  1, 6.5 },
                { 1.22, 0.80, 0.09, -1, 7.0 },
        };
        for (double[] p : pendamping) {
            boolean flip = p[3] < 0;
            ImageView kucing = spriteTetap("kucing-walk-2.png", p[2], p[0], p[1], flip);
            adegan.getChildren().add(kucing);
            siklusFrame(kucing, jalan, 150 + (int) (Math.random() * 60));
            bobbing(kucing, 5, 300 + (int) (Math.random() * 120));
            melintas(kucing, (p[3] > 0 ? 1 : -1) * w * 2.0, p[4]);
        }

        // ── Jadwal internal adegan 1 ──────────────────────────
        jadwal(
                new double[]{ 0.2, 5.0, 6.6, 8.0 },
                new Runnable[]{
                        // burung melintas di langit → menandai perpindahan adegan
                        () -> {
                            ImageView burung = spriteTetap("burung-diam.png",
                                    0.07, 1.10, 0.16, false);
                            adegan.getChildren().add(burung);
                            bobbing(burung, 8, 260);
                            melintas(burung, -w * 1.25, 2.6);
                        },
                        // kucing utama berhenti dan MENGEEONG SEKALI
                        () -> {
                            langkahUtama.stop();
                            bobUtama.stop();
                            kucingUtama.setTranslateY(0);
                            mainkanSekali("kucing.wav");
                            tampilkanBalon("meong", kucingUtama, 0.26);
                        },
                        () -> kucingUtama.setImage(gambar("kucing-duduk.png")),
                        // burung terbang mendekat — adegan akan berpindah
                        () -> {
                            ImageView burung2 = spriteTetap("burung-diam.png",
                                    0.10, 1.12, 0.30, true);
                            adegan.getChildren().add(burung2);
                            bobbing(burung2, 10, 240);
                            melintas(burung2, -w * 1.67, 1.6);
                        }
                }
        );
        // perpindahan ke adegan burung ditangani jadwal utama (detik ke-13)
    }

    /**
     * Rumput hasil gambar generate: dua baris ditimpa, setiap rumpun
     * berayun pada poros pangkalnya dengan fase/periode berbeda-beda
     * agar goyangan terlihat alami.
     */
    private void buatRumputBergoyang() {
        // baris belakang: lebih kecil, pelan, sedikit transparan
        double[][] belakang = {
                { 0.06, 0.845 }, { 0.22, 0.855 }, { 0.40, 0.850 }, { 0.58, 0.858 },
                { 0.74, 0.848 }, { 0.90, 0.852 },
        };
        for (int i = 0; i < belakang.length; i++) {
            String file = (i % 2 == 0) ? "rumput-a.png" : "rumput-b.png";
            ImageView rumput = spriteTetap(file, 0.085 + (i % 3) * 0.008,
                    belakang[i][0], belakang[i][1], false);
            rumput.setOpacity(0.85);
            adegan.getChildren().add(rumput);
            goyangRumput(rumput, 7 + (i % 3), 2.9 + 0.25 * (i % 4), 0.30 * i);
        }

        // baris depan: lebih besar, lebih cepat
        double[][] depan = {
                { 0.10, 0.965 }, { 0.28, 0.975 }, { 0.47, 0.962 },
                { 0.64, 0.972 }, { 0.82, 0.966 }, { 0.96, 0.975 },
        };
        for (int i = 0; i < depan.length; i++) {
            String file = (i % 2 == 0) ? "rumput-b.png" : "rumput-a.png";
            ImageView rumput = spriteTetap(file, 0.115 + (i % 3) * 0.010,
                    depan[i][0], depan[i][1], i % 3 == 2);
            adegan.getChildren().add(rumput);
            goyangRumput(rumput, 9 + (i % 4), 2.3 + 0.2 * (i % 5), 0.22 * i + 0.4);
        }
    }

    /** Ayunan rumput: rotasi pada poros pangkal (bawah tengah gambar). */
    private void goyangRumput(ImageView rumput, double sudut, double periode, double tunda) {
        Double rasio = rasioLebar(rumput);
        if (rasio == null) return;
        double lebarTampil = rumput.getFitHeight() * rasio;
        javafx.scene.transform.Rotate rot = new javafx.scene.transform.Rotate(
                0, lebarTampil / 2.0, rumput.getFitHeight());
        rumput.getTransforms().add(rot);

        Timeline ayun = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(rot.angleProperty(), -sudut, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(periode / 2.0),
                        new KeyValue(rot.angleProperty(), sudut, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(periode),
                        new KeyValue(rot.angleProperty(), -sudut, Interpolator.EASE_BOTH))
        );
        ayun.setCycleCount(Timeline.INDEFINITE);
        ayun.setDelay(Duration.seconds(tunda));
        ayun.play();
        track(ayun);
    }

    // ══════════════════════════════════════════════════════════
    // ADEGAN 2 — BURUNG CUIT-CUIT, DI BAWAHNYA RAMAI, COBA TERBANG
    // ══════════════════════════════════════════════════════════

    private void adeganBurung() {
        gantiAdegan(
                "Adegan 2 — Si burung hinggap sambil cuit-cuit; teman-temannya "
                        + "berkumpul di bawahnya hingga ramai.",
                this::bangunAdeganBurung);
    }

    private void bangunAdeganBurung() {
        adegan.getChildren().add(latar(bg("burung-hinggap.jpeg")));

        // Burung utama di atas (ranting), burung duplikat di bawahnya
        ImageView burungUtama = spriteTetap("burung-diam.png", 0.20, 0.33, 0.62, false);
        adegan.getChildren().add(burungUtama);

        double[][] kawanan = {
                { 0.20, 0.93, 0.10 }, { 0.33, 0.95, 0.12 }, { 0.47, 0.92, 0.09 },
                { 0.62, 0.945, 0.11 }, { 0.76, 0.93, 0.09 }, { 0.88, 0.955, 0.12 },
        };
        for (int i = 0; i < kawanan.length; i++) {
            ImageView burungKecil = spriteTetap("burung-di-bawah.png",
                    kawanan[i][2], kawanan[i][0], kawanan[i][1], i % 2 == 1);
            adegan.getChildren().add(burungKecil);
            bobbing(burungKecil, 3, 700 + i * 90);
        }

        // ── Jadwal internal adegan 2 ──────────────────────────
        jadwal(
                new double[]{ 0.8, 3.0, 5.4, 7.2, 8.8, 10.4 },
                new Runnable[]{
                        () -> { // cuit pertama
                            mainkanSekali("burung.wav");
                            tampilkanBalon("cuit", burungUtama, 0.22);
                        },
                        () -> { // cuit kedua — kawanan ikut ramai
                            mainkanSekali("burung.wav");
                            tampilkanBalon("cuit", burungUtama, 0.22);
                        },
                        // burung melihat ke atas — mulai berniat terbang
                        () -> burungUtama.setImage(gambar("burung-antisipasi.png")),
                        // burung mencoba terbang: pose sayap terangkat + lompatan kecil
                        () -> {
                            burungUtama.setImage(gambar("burung-ingin-terbang.png"));
                            TranslateTransition lompat = new TranslateTransition(
                                    Duration.millis(700), burungUtama);
                            lompat.setFromY(0);
                            lompat.setToY(-overlay.getHeight() * 0.03);
                            lompat.setAutoReverse(true);
                            lompat.setCycleCount(2);
                            lompat.play();
                            trackAnimasi(lompat);
                        },
                        // percobaan kedua, lebih tinggi
                        () -> {
                            TranslateTransition lompat2 = new TranslateTransition(
                                    Duration.millis(800), burungUtama);
                            lompat2.setFromY(0);
                            lompat2.setToY(-overlay.getHeight() * 0.05);
                            lompat2.setAutoReverse(true);
                            lompat2.setCycleCount(1);
                            lompat2.play();
                            trackAnimasi(lompat2);
                        },
                        // akhirnya naik terbang — meninggalkan ranting
                        () -> {
                            TranslateTransition naik = new TranslateTransition(
                                    Duration.seconds(1.8), burungUtama);
                            naik.setFromX(0);
                            naik.setFromY(0);
                            naik.setToX(overlay.getWidth() * 0.35);
                            naik.setToY(-overlay.getHeight() * 0.55);
                            naik.setInterpolator(Interpolator.EASE_IN);
                            naik.play();
                            trackAnimasi(naik);
                            FadeTransition pudar = new FadeTransition(
                                    Duration.seconds(1.8), burungUtama);
                            pudar.setFromValue(1);
                            pudar.setToValue(0);
                            pudar.play();
                            trackAnimasi(pudar);
                        }
                }
        );
        // perpindahan ke adegan jalan raya ditangani jadwal utama (detik ke-26.5)
    }

    // ══════════════════════════════════════════════════════════
    // ADEGAN 3 — MOTOR MELAJU KENCANG, DISUSUL MOBIL
    // ══════════════════════════════════════════════════════════

    private void adeganJalan() {
        gantiAdegan(
                "Adegan 3 — Di jalan raya, pengendara motor melaju kencang "
                        + "disusul mobil sport sesudahnya.",
                this::bangunAdeganJalan);
    }

    private void bangunAdeganJalan() {
        double w = overlay.getWidth();

        adegan.getChildren().add(latar(bg("background_mobil_motor.jpeg")));

        Image[] seqMotor = {
                gambar("motor-seq-1.png"), gambar("motor-seq-2.png"),
                gambar("motor-seq-3.png")
        };
        Image[] seqMobil = {
                gambar("mobil-seq-1.png"), gambar("mobil-seq-2.png"),
                gambar("mobil-seq-3.png")
        };

        // ── Jadwal internal adegan 3 ──────────────────────────
        jadwal(
                new double[]{ 0.4, 0.9, 4.6, 5.0, 9.2 },
                new Runnable[]{
                        // motor melaju kencang dari kiri ke kanan (jalur utama)
                        () -> {
                            ImageView motor = spriteLaju("motor-seq-1.png", 0.30, 0.88, false);
                            motor.setLayoutX(-w * 0.30);
                            adegan.getChildren().add(motor);
                            siklusFrame(motor, seqMotor, 140);
                            bobbing(motor, 4, 280);
                            melintas(motor, w * 1.65, 3.6);
                            mainkanLoop("motor.wav");
                        },
                        // motor kedua di jalur jauh — menambah kesan ramai
                        () -> {
                            ImageView motorJauh = spriteLaju("motor-seq-2.png",
                                    0.18, 0.70, true);
                            motorJauh.setLayoutX(-w * 0.25);
                            motorJauh.setOpacity(0.92);
                            adegan.getChildren().add(motorJauh);
                            siklusFrame(motorJauh, seqMotor, 160);
                            melintas(motorJauh, w * 1.55, 4.6);
                        },
                        // motor utama sudah lewat: suara motor berhenti
                        () -> hentikanSuara("motor.wav"),
                        // mobil melintas sesudah motor (dekat, jalur bawah)
                        () -> {
                            ImageView mobil = spriteLaju("mobil-seq-1.png", 0.24, 0.97, false);
                            mobil.setLayoutX(-w * 0.35);
                            adegan.getChildren().add(mobil);
                            siklusFrame(mobil, seqMobil, 150);
                            melintas(mobil, w * 1.75, 3.4);
                            mainkanLoop("porsche.wav");
                        },
                        // mobil sudah lewat: suara berhenti
                        () -> hentikanSuara("porsche.wav")
                }
        );
        // perpindahan ke SPBU ditangani jadwal utama (detik ke-38)
    }

    // ══════════════════════════════════════════════════════════
    // ADEGAN 4 — SPBU: PENGENDARA TURUN, ISI BENSIN SELF-SERVICE
    // ══════════════════════════════════════════════════════════

    private ImageView motorSpbu;
    private ImageView mobilSpbu;
    private ProgressBarCerita barMotor;
    private ProgressBarCerita barMobil;

    private void adeganSpbu() {
        gantiAdegan(
                "Adegan 4 — Kedua kendaraan masuk SPBU. Pengendara turun dari motor "
                        + "dan mengisi bensin secara self-service.",
                this::bangunAdeganSpbu);
    }

    private void bangunAdeganSpbu() {
        double w = overlay.getWidth();

        adegan.getChildren().add(latar(bg("spbu.jpeg")));

        // Kendaraan masuk dari kiri, berhenti di pom (perlambatan halus).
        // layoutX diletakkan berdasarkan PUSAT kendaraan agar berhenti tepat.
        double tinggiMotor = overlay.getHeight() * 0.26;
        double tinggiMobil = overlay.getHeight() * 0.22;
        Image imMotor = gambar("motor-diam.png");
        Image imMobil = gambar("mobil-diam.png");
        double setengahMotor = imMotor == null ? 0
                : tinggiMotor * imMotor.getWidth() / imMotor.getHeight() / 2;
        double setengahMobil = imMobil == null ? 0
                : tinggiMobil * imMobil.getWidth() / imMobil.getHeight() / 2;

        motorSpbu = spriteLaju("motor-diam.png", 0.26, 0.94, false);
        motorSpbu.setLayoutX(-w * 0.30 - setengahMotor);
        mobilSpbu = spriteLaju("mobil-diam.png", 0.22, 0.96, false);
        mobilSpbu.setLayoutX(-w * 0.45 - setengahMobil);
        adegan.getChildren().addAll(motorSpbu, mobilSpbu);

        double berhentiMotor = w * 0.24;
        double berhentiMobil = w * 0.72;
        TranslateTransition masukMotor = new TranslateTransition(Duration.seconds(2.6), motorSpbu);
        masukMotor.setFromX(0);
        masukMotor.setToX(berhentiMotor - (-w * 0.30));
        masukMotor.setInterpolator(Interpolator.EASE_OUT);
        masukMotor.play();
        trackAnimasi(masukMotor);

        TranslateTransition masukMobil = new TranslateTransition(Duration.seconds(2.6), mobilSpbu);
        masukMobil.setFromX(0);
        masukMobil.setToX(berhentiMobil - (-w * 0.45));
        masukMobil.setInterpolator(Interpolator.EASE_OUT);
        masukMobil.setDelay(Duration.seconds(0.4));
        masukMobil.play();
        trackAnimasi(masukMobil);

        // ── Jadwal internal adegan 4 ──────────────────────────
        jadwal(
                new double[]{ 3.4, 4.2, 4.8 },
                new Runnable[]{
                        // pengendara TURUN dari motor, lalu mengambil nozzle
                        () -> {
                            motorSpbu.setImage(gambar("motor.png"));
                            ImageView pengisi = spriteLaju("pengendara-isi-motor.png",
                                    0.23, 0.94, false);
                            pengisi.layoutXProperty().bind(
                                    motorSpbu.layoutXProperty()
                                            .add(motorSpbu.translateXProperty())
                                            .add(overlay.widthProperty().multiply(0.09)));
                            pengisi.setOpacity(0);
                            adegan.getChildren().add(pengisi);
                            fadeMuncul(pengisi);
                            // kartu adegan self-service (gambar generate) singkat
                            tampilkanKartu("kartu-isi-motor.png", true);
                        },
                        // mobil juga diisi — proses sama, self-service
                        () -> {
                            ImageView pengisiMobil = spriteLaju("pengendara-isi-mobil.png",
                                    0.15, 0.96, false);
                            pengisiMobil.layoutXProperty().bind(
                                    mobilSpbu.layoutXProperty()
                                            .add(mobilSpbu.translateXProperty())
                                            .subtract(overlay.widthProperty().multiply(0.09)));
                            pengisiMobil.setOpacity(0);
                            adegan.getChildren().add(pengisiMobil);
                            fadeMuncul(pengisiMobil);
                            tampilkanKartu("kartu-isi-mobil.png", false);
                        },
                        // KEDUA pengisian berjalan BERSAMAAN (proses sama)
                        () -> {
                            barMotor = tambahProgressBar(motorSpbu,
                                    "Motor • Pertalite • Self-Service", 0.115);
                            barMobil = tambahProgressBar(mobilSpbu,
                                    "Mobil • Pertamax 92 • Self-Service", 0.115);
                            barMotor.isi(5.0);
                            barMobil.isi(5.0);
                        }
                }
        );
        // struk ditampilkan jadwal utama (detik ke-49.5), setelah pengisian selesai
    }

    /** Kartu adegan hasil gambar generate: muncul di pojok atas, lalu hilang. */
    private void tampilkanKartu(String file, boolean pojokKanan) {
        ImageView kartu = new ImageView(gambar(file));
        kartu.setPreserveRatio(true);
        kartu.setSmooth(true);
        kartu.setMouseTransparent(true);
        kartu.fitHeightProperty().bind(adegan.heightProperty().multiply(0.30));
        Double rasio = rasioLebar(kartu);
        if (pojokKanan) {
            kartu.layoutXProperty().bind(adegan.widthProperty().multiply(0.985)
                    .subtract(kartu.fitHeightProperty()
                            .multiply(rasio == null ? 0.7 : rasio)));
        } else {
            kartu.layoutXProperty().bind(adegan.widthProperty().multiply(0.015));
        }
        kartu.layoutYProperty().bind(adegan.heightProperty().multiply(0.10));
        kartu.setStyle(
                "-fx-background-color: rgba(255,255,255,0.9);" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 4;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 16, 0.2, 0, 5);");
        kartu.setOpacity(0);
        adegan.getChildren().add(kartu);

        FadeTransition masuk = new FadeTransition(Duration.millis(400), kartu);
        masuk.setFromValue(0);
        masuk.setToValue(1);
        masuk.play();
        trackAnimasi(masuk);

        Timeline tahan = new Timeline(new KeyFrame(Duration.seconds(3.2), e -> {
            FadeTransition keluar = new FadeTransition(Duration.millis(450), kartu);
            keluar.setFromValue(1);
            keluar.setToValue(0);
            keluar.setOnFinished(ev -> adegan.getChildren().remove(kartu));
            keluar.play();
            trackAnimasi(keluar);
        }));
        tahan.play();
        track(tahan);
    }

    private void fadeMuncul(Node n) {
        FadeTransition muncul = new FadeTransition(Duration.millis(500), n);
        muncul.setFromValue(0);
        muncul.setToValue(1);
        muncul.play();
        trackAnimasi(muncul);
    }

    // ══════════════════════════════════════════════════════════
    // STRUK BUKTI BAYAR (gambar hasil generate — bukan render JavaFX)
    // ══════════════════════════════════════════════════════════

    private void tahapStruk() {
        hentikanSuara("motor.wav");
        hentikanSuara("porsche.wav");

        // Hapus progress bar (pengisian sudah selesai)
        adegan.getChildren().removeIf(n -> n instanceof ProgressBarCerita);

        setCaption("Pengisian selesai! Inilah struk pembayaran — bukti bayar self-service "
                + "dengan harga SPBU yang sama.");

        // Lapisan redup agar struk menonjol
        Region redup = new Region();
        redup.setStyle("-fx-background-color: rgba(8, 10, 14, 0.55);");
        redup.setMouseTransparent(true);
        redup.layoutXProperty().bind(adegan.widthProperty().multiply(0));
        redup.layoutYProperty().bind(adegan.heightProperty().multiply(0));
        redup.prefWidthProperty().bind(adegan.widthProperty());
        redup.prefHeightProperty().bind(adegan.heightProperty());

        // Struk motor (kiri) dan struk mobil (kanan) — gambar hasil generate
        ImageView strukMotor = strukView("struk-motor.png", 0.62, 0.26);
        ImageView strukMobil = strukView("struk-mobil.png", 0.62, 0.74);

        Label judulStruk = new Label("BUKTI PEMBAYARAN — SELF-SERVICE (harga sama)");
        judulStruk.setFont(Font.font(FONT_MAIN, FontWeight.BOLD, 17));
        judulStruk.setTextFill(Color.WHITE);
        judulStruk.setStyle(
                "-fx-background-color: rgba(15, 20, 24, 0.82);" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 8 20 8 20;");
        judulStruk.layoutXProperty().bind(adegan.widthProperty().divide(2)
                .subtract(judulStruk.widthProperty().divide(2)));
        judulStruk.layoutYProperty().bind(adegan.heightProperty().multiply(0.05));

        Pane panelStruk = new Pane(redup, strukMotor, strukMobil, judulStruk);
        panelStruk.setPickOnBounds(false);
        panelStruk.setOpacity(0);
        adegan.getChildren().add(panelStruk);

        FadeTransition muncul = new FadeTransition(Duration.millis(600), panelStruk);
        muncul.setFromValue(0);
        muncul.setToValue(1);
        muncul.play();
        trackAnimasi(muncul);
    }

    /** Struk sebagai ImageView (gambar), tinggi & pusat-x dalam persentase. */
    private ImageView strukView(String file, double tinggiPersen, double xPersen) {
        ImageView iv = new ImageView(gambar(file));
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.fitHeightProperty().bind(adegan.heightProperty().multiply(tinggiPersen));
        iv.layoutYProperty().bind(adegan.heightProperty().multiply(0.16));
        Double rasio = rasioLebar(iv);
        if (rasio != null) {
            iv.layoutXProperty().bind(adegan.widthProperty().multiply(xPersen)
                    .subtract(iv.fitHeightProperty().multiply(rasio).divide(2)));
        } else {
            iv.layoutXProperty().bind(adegan.widthProperty().multiply(xPersen));
        }
        iv.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 24, 0.25, 0, 8);");
        return iv;
    }

    // ══════════════════════════════════════════════════════════
    // BALON UCAPAN (gambar generate + teks) & PROGRESS BAR
    // ══════════════════════════════════════════════════════════

    /** Balon ucapan gambar hasil generate ("Meong!" / "Cuit!"), muncul di atas acuan. */
    private void tampilkanBalon(String jenis, ImageView acuan, double tinggiPersen) {
        ImageView balon = new ImageView(gambar("balon-" + jenis + ".png"));
        balon.setPreserveRatio(true);
        balon.setSmooth(true);
        balon.setMouseTransparent(true);
        balon.fitHeightProperty().bind(adegan.heightProperty().multiply(tinggiPersen));
        Double rasio = rasioLebar(balon);

        ImageView acuanRef = acuan;
        if (rasio != null) {
            balon.layoutXProperty().bind(
                    acuanRef.layoutXProperty()
                            .add(acuanRef.translateXProperty())
                            .add(acuanRef.fitHeightProperty()
                                    .multiply(rasioLebar(acuanRef) == null ? 0 : rasioLebar(acuanRef))
                                    .divide(2))
                            .subtract(balon.fitHeightProperty().multiply(rasio).divide(2)));
        } else {
            balon.layoutXProperty().bind(
                    acuanRef.layoutXProperty().add(acuanRef.translateXProperty()));
        }
        balon.layoutYProperty().bind(
                acuanRef.layoutYProperty().add(acuanRef.translateYProperty())
                        .subtract(balon.fitHeightProperty()).subtract(6));

        balon.setOpacity(0);
        adegan.getChildren().add(balon);
        FadeTransition masuk = new FadeTransition(Duration.millis(250), balon);
        masuk.setFromValue(0);
        masuk.setToValue(1);
        masuk.play();
        trackAnimasi(masuk);

        // tahan lalu hilang
        Timeline tahan = new Timeline(new KeyFrame(Duration.millis(1900), e -> {
            FadeTransition keluar = new FadeTransition(Duration.millis(300), balon);
            keluar.setFromValue(1);
            keluar.setToValue(0);
            keluar.setOnFinished(ev -> adegan.getChildren().remove(balon));
            keluar.play();
            trackAnimasi(keluar);
        }));
        tahan.play();
        track(tahan);
    }

    /** Progress bar pengisian bensin (hijau) di atas kendaraan. */
    private ProgressBarCerita tambahProgressBar(ImageView kendaraan,
                                               String label, double tinggiPersen) {
        ProgressBarCerita bar = new ProgressBarCerita(label);
        bar.layoutXProperty().bind(
                kendaraan.layoutXProperty()
                        .add(kendaraan.translateXProperty())
                        .add(kendaraan.fitHeightProperty()
                                .multiply(rasioLebar(kendaraan) == null ? 0 : rasioLebar(kendaraan))
                                .divide(2))
                        .subtract(bar.widthProperty().divide(2)));
        bar.layoutYProperty().bind(
                kendaraan.layoutYProperty().add(kendaraan.translateYProperty())
                        .subtract(bar.heightProperty())
                        .subtract(adegan.heightProperty().multiply(0.015)));
        adegan.getChildren().add(bar);
        return bar;
    }

    /** Progress bar sederhana bergaya panel putih + isian hijau. */
    private static class ProgressBarCerita extends VBox {
        private final Region isian = new Region();
        private final Label persen = new Label("0%");
        private final double LEBAR = 240;

        ProgressBarCerita(String label) {
            setSpacing(4);
            setAlignment(Pos.CENTER);
            setMaxWidth(Region.USE_PREF_SIZE);
            setStyle(
                    "-fx-background-color: rgba(255,255,255,0.92);" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-color: rgba(0,0,0,0.12);" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-width: 1;" +
                            "-fx-padding: 8 12 8 12;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0.2, 0, 4);");

            Label judul = new Label(label);
            judul.setFont(Font.font(FONT_MAIN, FontWeight.SEMI_BOLD, 12));
            judul.setTextFill(Color.web("#2b2b2b"));

            Region latarBar = new Region();
            latarBar.setPrefSize(LEBAR, 12);
            latarBar.setMinSize(LEBAR, 12);
            latarBar.setMaxSize(LEBAR, 12);
            latarBar.setStyle("-fx-background-color: rgba(0,0,0,0.12);" +
                    "-fx-background-radius: 6;");

            isian.setPrefHeight(12);
            isian.setMinHeight(12);
            isian.setMaxHeight(12);
            isian.setPrefWidth(0);
            isian.setStyle("-fx-background-color: linear-gradient(to right, #66bb6a, #2e7d32);" +
                    "-fx-background-radius: 6;");
            isian.setLayoutY(0);

            Pane tumpuk = new Pane(latarBar, isian);
            tumpuk.setPrefSize(LEBAR, 12);
            tumpuk.setMinSize(LEBAR, 12);
            tumpuk.setMaxSize(LEBAR, 12);

            persen.setFont(Font.font(FONT_MAIN, FontWeight.BOLD, 13));
            persen.setTextFill(Color.web("#2e7d32"));

            getChildren().addAll(judul, tumpuk, persen);
        }

        /** Isi progres dari 0% ke 100% selama `detik`. */
        void isi(double detik) {
            Timeline t = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(isian.prefWidthProperty(), 0)),
                    new KeyFrame(Duration.seconds(detik),
                            new KeyValue(isian.prefWidthProperty(), LEBAR))
            );
            t.currentTimeProperty().addListener((o, lama, baru) -> {
                double p = baru.toMillis() / (detik * 1000.0);
                persen.setText(String.format("%.0f%%", Math.min(100, p * 100)));
            });
            t.play();
        }
    }
}
