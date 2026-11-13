# Polymorphism Edu — Kelompok 1

Aplikasi JavaFX untuk demonstrasi **polimorfisme** pada Pemrograman Berorientasi
Objek: dua class abstrak (`Hewan` dan `Kendaraan`) dengan implementasi
`Kucing`, `Burung`, `Mobil`, dan `Motor`.

## Fitur

1. **Menu Class Hewan** — detail `Kucing` dan `Burung` (bersuara & informasi).
2. **Menu Class Kendaraan** — detail `Mobil` dan `Motor`
   (bergerak, isi bensin, informasi).
3. **Menu Isi Bensin** — progress bar pengisian tiap kendaraan.
4. **Alur Cerita (fitur baru)** — satu tombol, satu frame, satu kisah utuh:

   | Adegan | Isi |
   |--------|-----|
   | 1. Taman | Kucing berjalan-jalan (animasi 4 frame + 5 kucing duplikat), rumput bergoyang (gambar generate diayunkan pada poros pangkalnya), kucing mengeong sekali (suara + balon "Meong!") |
   | 2. Burung | Burung cuit-cuit (suara + balon), 6 burung duplikat di bawahnya, burung mencoba terbang (3 gambar sequence) lalu terbang pergi |
   | 3. Jalan Raya | Motor melaju kencang (3 gambar sequence + suara `motor.wav`), disusul mobil (3 gambar sequence + suara `porsche.wav`) |
   | 4. SPBU | Pengendara turun dari motor, mengisi bensin **self-service** bersama mobil (2 progress bar), lalu **struk bukti bayar** ditampilkan sebagai gambar hasil generate |

   Kontrol: `Kembali`, `Lewati ke SPBU`, dan `Putar Ulang`.

## Cara Menjalankan

```bash
./mvnw clean javafx:run
```

Atau build jar lakukan:

```bash
./mvnw clean package
java -jar target/polymorp_obj-1.0-SNAPSHOT.jar
```

> `pom.xml` memakai Java **21** (LTS). Jalankan dengan JDK 21 atau yang lebih baru.

## Struktur Kode

```
src/main/java/id/kelompok1/pbo/polymorp_obj/
├── model/           Hewan, Kucing, Burung, Kendaraan, Mobil, Motor
├── ui/              MainController, ClassCard, DetailPage, IsiBensinPage,
│                    CeritaPage (alur cerita satu frame)
├── util/            SoundManager, FontLoader
├── PolymorphismApp  Entry point JavaFX
└── Launcher         Bootstrap non-Application

src/main/resources/id/kelompok1/pbo/polymorp_obj/
├── gambar/          Aset latar & sprite
├── gambar/cerita/   Aset alur cerita (sprite sequence, rumput, balon, struk)
└── sounds/          kucing.wav, burung.wav, motor.wav, porsche.wav
```

## Catatan Aset

- Sprite sequence (kucing, burung, motor, mobil) diolah dari `informasi gambar`
  yang diberikan (slice sheet + penghapusan latar putih).
- Gambar hasil generate: rumput, pengendara mengisi bensin self-service,
  balon ucapan, dan struk pembayaran (ditempel teks agar terbaca jelas).
