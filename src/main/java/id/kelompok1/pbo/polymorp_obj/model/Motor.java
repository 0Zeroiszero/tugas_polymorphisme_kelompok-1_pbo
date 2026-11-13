package id.kelompok1.pbo.polymorp_obj.model;

public class Motor extends Kendaraan {
    @Override
    public String bergerak() {
        return "melaju di jalan raya dengan dua roda";
    }

    @Override
    public String isiBensin() {
        return "mengisi Pertamax 92 sebanyak 10 liter di SPBU";
    }
}