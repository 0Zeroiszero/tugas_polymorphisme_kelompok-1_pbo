package id.kelompok1.pbo.polymorp_obj.model;

public class Burung extends Hewan {
    public Burung() { super("burung.wav"); }

    @Override
    public String getSuaraKhas() { return "Cuit cuit"; }
}