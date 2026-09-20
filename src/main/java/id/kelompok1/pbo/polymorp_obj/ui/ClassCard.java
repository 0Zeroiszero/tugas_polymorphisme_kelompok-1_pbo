package id.kelompok1.pbo.polymorp_obj.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class ClassCard extends VBox {

    private static final String IMG_BASE = "/id/kelompok1/pbo/polymorp_obj/gambar/";
    private static final String FONT_MAIN = "Poppins";
    private static final String FONT_CODE = "JetBrains Mono";

    private static final double ITEM_SIZE = 120;
    private static final double ITEM_RADIUS = 24;

    private static final Color COLOR_KEYWORD = Color.web("#d73a49");
    private static final Color COLOR_CLASSNAME = Color.web("#6f42c1");

    private static final String CARD_NORMAL =
            "-fx-background-color: rgba(255, 255, 255, 0.55);" +
                    "-fx-background-radius: 28;" +
                    "-fx-border-color: rgba(0, 0, 0, 0.08);" +
                    "-fx-border-radius: 28;" +
                    "-fx-border-width: 1;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.12), 24, 0.15, 0, 8);";

    private static final String CARD_HOVER =
            "-fx-background-color: rgba(255, 255, 255, 0.72);" +
                    "-fx-background-radius: 28;" +
                    "-fx-border-color: rgba(0, 0, 0, 0.18);" +
                    "-fx-border-radius: 28;" +
                    "-fx-border-width: 1.2;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.20), 32, 0.20, 0, 12);";

    private static final String ITEM_NORMAL =
            "-fx-background-color: rgba(0, 0, 0, 0.04);" +
                    "-fx-background-radius: " + ITEM_RADIUS + ";" +
                    "-fx-border-color: rgba(0, 0, 0, 0.08);" +
                    "-fx-border-radius: " + ITEM_RADIUS + ";" +
                    "-fx-border-width: 1;";

    private static final String ITEM_HOVER =
            "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                    "-fx-background-radius: " + ITEM_RADIUS + ";" +
                    "-fx-border-color: rgba(0, 0, 0, 0.35);" +
                    "-fx-border-radius: " + ITEM_RADIUS + ";" +
                    "-fx-border-width: 1.8;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.18), 14, 0.15, 0, 4);";

    public static class Item {
        public final String nama;
        public final String fileGambar;
        public final Runnable aksi;

        public Item(String nama, String fileGambar, Runnable aksi) {
            this.nama = nama;
            this.fileGambar = fileGambar;
            this.aksi = aksi;
        }
    }

    public ClassCard(String namaParent, String[] methods, Item[] items) {
        setSpacing(22);
        setPadding(new Insets(30, 36, 30, 36));
        setAlignment(Pos.CENTER);
        setPrefWidth(560);

        setStyle(CARD_NORMAL);
        setOnMouseEntered(e -> setStyle(CARD_HOVER));
        setOnMouseExited(e -> setStyle(CARD_NORMAL));

        // ── Judul + tombol "!" ────────────────────────────────
        // TIDAK ada mouseTransparent di sini, agar tombol bisa diklik
        TextFlow judul = buatJudul("class", namaParent);
        Button btnInfo = buatTombolInfo(namaParent, methods);

        HBox judulWrap = new HBox(10, judul, btnInfo);
        judulWrap.setAlignment(Pos.CENTER);

        // ── Grid sub-card ─────────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(18);
        grid.setAlignment(Pos.CENTER);

        int cols = items.length == 3 ? 3 : 2;
        for (int i = 0; i < items.length; i++) {
            Node itemNode = buatItem(items[i]);
            grid.add(itemNode, i % cols, i / cols);
        }

        getChildren().addAll(judulWrap, grid);
    }

    /** Tombol bulat "!" pakai Button agar klik pasti bekerja. */
    private Button buatTombolInfo(String namaParent, String[] methods) {
        Button btn = new Button("!");
        String styleNormal =
                "-fx-font-family: '" + FONT_CODE + "';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #6f42c1;" +
                        "-fx-background-color: rgba(111, 66, 193, 0.12);" +
                        "-fx-background-radius: 50%;" +
                        "-fx-border-color: #6f42c1;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-border-width: 1.4;" +
                        "-fx-min-width: 26px;" +
                        "-fx-min-height: 26px;" +
                        "-fx-max-width: 26px;" +
                        "-fx-max-height: 26px;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: hand;";

        String styleHover =
                "-fx-font-family: '" + FONT_CODE + "';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-color: #6f42c1;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-border-color: #6f42c1;" +
                        "-fx-border-radius: 50%;" +
                        "-fx-border-width: 1.4;" +
                        "-fx-min-width: 26px;" +
                        "-fx-min-height: 26px;" +
                        "-fx-max-width: 26px;" +
                        "-fx-max-height: 26px;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: hand;";

        btn.setStyle(styleNormal);
        btn.setCursor(Cursor.HAND);
        btn.setFocusTraversable(false);

        btn.setOnMouseEntered(e -> btn.setStyle(styleHover));
        btn.setOnMouseExited(e -> btn.setStyle(styleNormal));

        // Pakai setOnAction — lebih andal untuk Button
        btn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Method yang bisa dipanggil pada class ")
                    .append(namaParent).append(":\n\n");
            for (String m : methods) {
                sb.append("  •  ").append(m).append("\n");
            }
            sb.append("\nSemua method ini tersedia untuk semua subclass ")
                    .append(namaParent).append(".");

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Method: " + namaParent);
            a.setHeaderText(null);
            a.setContentText(sb.toString());
            a.showAndWait();
        });

        return btn;
    }

    private Node buatItem(Item item) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(ITEM_SIZE + 20);
        box.setCursor(Cursor.HAND);

        StackPane imgWrap = new StackPane();
        imgWrap.setPrefSize(ITEM_SIZE, ITEM_SIZE);
        imgWrap.setMinSize(ITEM_SIZE, ITEM_SIZE);
        imgWrap.setMaxSize(ITEM_SIZE, ITEM_SIZE);
        imgWrap.setStyle(ITEM_NORMAL);
        imgWrap.setMouseTransparent(true);

        try {
            Image img = new Image(getClass().getResourceAsStream(IMG_BASE + item.fileGambar));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(ITEM_SIZE);
            iv.setFitHeight(ITEM_SIZE);
            iv.setPreserveRatio(false);

            Rectangle clip = new Rectangle(ITEM_SIZE, ITEM_SIZE);
            clip.setArcWidth(ITEM_RADIUS * 2);
            clip.setArcHeight(ITEM_RADIUS * 2);
            iv.setClip(clip);

            imgWrap.getChildren().add(iv);
        } catch (Exception ex) {
            Label fallback = new Label(item.nama.substring(0, 1).toUpperCase());
            fallback.setStyle(
                    "-fx-font-family: '" + FONT_CODE + "';" +
                            "-fx-font-size: 42px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #555;"
            );
            imgWrap.getChildren().add(fallback);
        }

        Label lbl = new Label(item.nama);
        lbl.setStyle(
                "-fx-font-family: '" + FONT_MAIN + "';" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: 600;" +
                        "-fx-text-fill: #2b2b2b;"
        );
        lbl.setMouseTransparent(true);

        box.getChildren().addAll(imgWrap, lbl);

        box.setOnMouseEntered(e -> imgWrap.setStyle(ITEM_HOVER));
        box.setOnMouseExited(e -> imgWrap.setStyle(ITEM_NORMAL));

        box.setOnMouseClicked(e -> {
            e.consume();
            item.aksi.run();
        });

        return box;
    }

    private TextFlow buatJudul(String keyword, String nama) {
        Text tKeyword = new Text(keyword);
        tKeyword.setFill(COLOR_KEYWORD);
        tKeyword.setFont(Font.font(FONT_CODE, FontWeight.BOLD, 22));

        Text tSpasi = new Text(" ");
        tSpasi.setFont(Font.font(FONT_CODE, FontWeight.NORMAL, 22));

        Text tNama = new Text(nama);
        tNama.setFill(COLOR_CLASSNAME);
        tNama.setFont(Font.font(FONT_CODE, FontWeight.BOLD, 22));

        TextFlow tf = new TextFlow(tKeyword, tSpasi, tNama);
        tf.setTextAlignment(TextAlignment.CENTER);
        return tf;
    }
}