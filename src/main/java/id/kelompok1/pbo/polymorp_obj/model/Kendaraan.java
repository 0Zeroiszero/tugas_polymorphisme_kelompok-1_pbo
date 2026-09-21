package id.kelompok1.pbo.polymorp_obj.model;

import id.kelompok1.pbo.polymorp_obj.util.SoundManager;

public abstract class Kendaraan {

    private static final String SOUND_BASE = "/id/kelompok1/pbo/polymorp_obj/sounds/";

    // ── Private atribut (dibaca oleh isiBensin()) ─────────
    private final String jenisBBM;
    private final int kapasitasLiter;
    private final String fileSuara;

    // ── Callback animasi dari UI ──────────────────────────
    private Runnable onStartMove;
    private Runnable onStopMove;
    private boolean moving = false;

    protected Kendaraan(String jenisBBM, int kapasitasLiter, String fileSuara) {
        this.jenisBBM = jenisBBM;
        this.kapasitasLiter = kapasitasLiter;
        this.fileSuara = fileSuara;
    }

    /** Controller memanggil ini untuk menyambungkan animasi UI. */
    public void setMoveCallbacks(Runnable onStart, Runnable onStop) {
        this.onStartMove = onStart;
        this.onStopMove = onStop;
    }

    /**
     * Toggle bergerak/berhenti.
     * Saat bergerak  → jalankan animasi + play WAV (loop).
     * Saat berhenti  → hentikan animasi + stop WAV.
     * @return status baru (true = sedang bergerak).
     */
    public boolean bergerak() {
        if (moving) {
            if (onStopMove != null) onStopMove.run();
            SoundManager.stop(SOUND_BASE + fileSuara);
            moving = false;
        } else {
            if (onStartMove != null) onStartMove.run();
            SoundManager.playLoop(SOUND_BASE + fileSuara);
            moving = true;
        }
        return moving;
    }

    /** Paksa berhenti (dipakai saat pindah halaman). */
    public void stopMoving() {
        if (moving) {
            if (onStopMove != null) onStopMove.run();
            SoundManager.stop(SOUND_BASE + fileSuara);
            moving = false;
        }
    }

    /** Informasi isi bensin — dibaca dari private atribut. */
    public String isiBensin() {
        return "mengisi " + jenisBBM + " sebanyak " + kapasitasLiter + " liter di SPBU";
    }

    // ── Getter ────────────────────────────────────────────
    public String getJenisBBM() { return jenisBBM; }
    public int getKapasitasLiter() { return kapasitasLiter; }
    public String getFileSuara() { return fileSuara; }
    public boolean isMoving() { return moving; }
}