package id.kelompok1.pbo.polymorp_obj.model;

public class Kucing extends Hewan {
    public Kucing() { super("kucing.wav"); }

    @Override
    public String getSuaraKhas() { return "Meong"; }
}