package id.kelompok1.pbo.polymorp_obj.model;

public class Mobil extends Kendaraan {
    @Override
    public String bergerak() {
        return "melaju di jalan raya dengan empat roda";
    }

    @Override
    public String isiBensin() {
        return "mengisi Pertamax 92 sebanyak 40 liter di SPBU";
    }
}