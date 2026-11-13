package id.kelompok1.pbo.polymorp_obj.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache MediaPlayer per path agar file audio tidak di-lock berulang kali.
 * MediaPlayer hanya dibuat sekali untuk setiap file.
 */
public class SoundManager {

    private static final Map<String, MediaPlayer> CACHE = new HashMap<>();

    /** Ambil MediaPlayer dari cache, atau buat baru jika belum ada. */
    public static MediaPlayer get(String resourcePath) {
        if (resourcePath == null) return null;
        return CACHE.computeIfAbsent(resourcePath, path -> {
            try {
                URL url = SoundManager.class.getResource(path);
                if (url == null) {
                    System.err.println("[SOUND] Resource NULL: " + path);
                    return null;
                }
                Media media = new Media(url.toExternalForm());
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

    /** Hentikan semua suara (dipakai saat keluar aplikasi). */
    public static void stopAll() {
        for (MediaPlayer mp : CACHE.values()) {
            if (mp != null) {
                mp.stop();
                mp.seek(Duration.ZERO);
                mp.setCycleCount(1);
            }
        }
    }
}