package id.kelompok1.pbo.polymorp_obj.util;

import javafx.scene.text.Font;
import java.util.Objects;

public class FontLoader {

    private static final String FONT_BASE = "/id/kelompok1/pbo/polymorp_obj/fonts/";

    public static void loadAll() {
        load("Poppins-Regular.ttf");
        load("Poppins-SemiBold.ttf");
        load("JetBrainsMono-Bold.ttf");
    }

    private static void load(String fileName) {
        try {
            Font.loadFont(
                    Objects.requireNonNull(
                            FontLoader.class.getResourceAsStream(FONT_BASE + fileName)
                    ),
                    14
            );
        } catch (Exception e) {
            System.err.println("Gagal memuat font: " + fileName);
        }
    }
}