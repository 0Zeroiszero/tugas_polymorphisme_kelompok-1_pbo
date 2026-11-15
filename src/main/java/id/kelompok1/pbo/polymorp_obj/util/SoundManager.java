package id.kelompok1.pbo.polymorp_obj.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cache Media/MediaPlayer per path agar file audio tidak di-lock berulang kali.
 * Efek satu-kali bisa tumpang tindih (mis. banyak kucing/burung berbunyi).
 */
public class SoundManager {

    private static final Map<String, Media> MEDIA = new HashMap<>();
    private static final Map<String, MediaPlayer> CACHE = new HashMap<>();
    private static final List<MediaPlayer> EFEK = new ArrayList<>();

    /** Ambil MediaPlayer dari cache, atau buat baru jika belum ada. */
    public static MediaPlayer get(String resourcePath) {
        if (resourcePath == null) return null;
        return CACHE.computeIfAbsent(resourcePath, path -> {
            try {
                Media media = ambilMedia(path);
                if (media == null) return null;
                MediaPlayer mp = new MediaPlayer(media);
                mp.setOnError(() ->
                        System.err.println("[SOUND] Error " + path + ": " + mp.getError()));
                return mp;
            } catch (Exception e) {
                System.err.println("[SOUND] Gagal buat MediaPlayer: " + path);
                e.printStackTrace();
                return null;
            }
        });
    }

    private static Media ambilMedia(String path) {
        return MEDIA.computeIfAbsent(path, p -> {
            try {
                URL url = SoundManager.class.getResource(p);
                if (url == null) {
                    System.err.println("[SOUND] Resource NULL: " + p);
                    return null;
                }
                return new Media(url.toExternalForm());
            } catch (Exception e) {
                System.err.println("[SOUND] Gagal buat Media: " + p);
                e.printStackTrace();
                return null;
            }
        });
    }

    public static void playOnce(String path) {
        MediaPlayer mp = get(path);
        if (mp == null) return;

        // Kalau sudah READY, langsung play. Kalau belum, tunggu sampai READY.
        if (mp.getStatus() == MediaPlayer.Status.READY
                || mp.getStatus() == MediaPlayer.Status.PAUSED
                || mp.getStatus() == MediaPlayer.Status.STOPPED) {
            startPlay(mp, 1);
        } else {
            // Belum siap — antre lewat setOnReady
            mp.setOnReady(() -> startPlay(mp, 1));
        }
    }

    /**
     * Efek satu-kali dengan instance baru, jadi beberapa suara dari file yang
     * sama bisa berbunyi bersamaan (tidak saling memotong).
     */
    public static void playEffect(String path) {
        Media media = ambilMedia(path);
        if (media == null) return;
        try {
            MediaPlayer mp = new MediaPlayer(media);
            mp.setVolume(1.0);
            mp.setCycleCount(1);
            mp.setOnError(() ->
                    System.err.println("[SOUND] Error efek " + path + ": " + mp.getError()));
            mp.setOnEndOfMedia(() -> {
                EFEK.remove(mp);
                mp.dispose();
            });
            EFEK.add(mp);
            mp.play();
        } catch (Exception e) {
            System.err.println("[SOUND] Gagal play efek: " + path);
            e.printStackTrace();
        }
    }

    public static void playLoop(String path) {
        MediaPlayer mp = get(path);
        if (mp == null) return;

        if (mp.getStatus() == MediaPlayer.Status.READY
                || mp.getStatus() == MediaPlayer.Status.PAUSED
                || mp.getStatus() == MediaPlayer.Status.STOPPED) {
            startPlay(mp, MediaPlayer.INDEFINITE);
        } else {
            mp.setOnReady(() -> startPlay(mp, MediaPlayer.INDEFINITE));
        }
    }

    /** Helper: play dengan cycle count tertentu. */
    private static void startPlay(MediaPlayer mp, int cycles) {
        mp.setMute(false);
        mp.setVolume(1.0);
        mp.setCycleCount(cycles);
        mp.stop();
        mp.seek(Duration.ZERO);
        mp.play();
    }

    /** Hentikan suara tertentu. */
    public static void stop(String path) {
        MediaPlayer mp = CACHE.get(path);
        if (mp == null) return;
        mp.stop();
        mp.seek(Duration.ZERO);
        mp.setCycleCount(1);
    }

    /** Hentikan semua suara (dipakai saat keluar aplikasi / ganti adegan). */
    public static void stopAll() {
        for (MediaPlayer mp : CACHE.values()) {
            if (mp != null) {
                mp.stop();
                mp.seek(Duration.ZERO);
                mp.setCycleCount(1);
            }
        }
        for (MediaPlayer mp : new ArrayList<>(EFEK)) {
            mp.stop();
            mp.dispose();
        }
        EFEK.clear();
    }
}
