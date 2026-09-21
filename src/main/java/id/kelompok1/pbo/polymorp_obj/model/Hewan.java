package id.kelompok1.pbo.polymorp_obj.model;

import id.kelompok1.pbo.polymorp_obj.util.SoundManager;

public abstract class Hewan {

    private static final String SOUND_BASE = "/id/kelompok1/pbo/polymorp_obj/sounds/";
    private final String fileSuara;

    protected Hewan(String fileSuara) {
        this.fileSuara = fileSuara;
    }

    /** Memainkan WAV khas hewan ini, lalu mengembalikan deskripsi suaranya. */
    public String bersuara() {
        SoundManager.playOnce(SOUND_BASE + fileSuara);
        return getSuaraKhas();
    }

    /** Deskripsi suara khas (tanpa memainkan WAV). */
    public abstract String getSuaraKhas();

    public String getFileSuara() { return fileSuara; }
}