# Polymorphism Edu — Kelompok 1

Aplikasi JavaFX untuk demonstrasi **polimorfisme** pada Pemrograman Berorientasi
Objek: dua class abstrak (`Hewan` dan `Kendaraan`) dengan implementasi
`Kucing`, `Burung`, `Mobil`, dan `Motor`.

## Fitur

1. **Menu Class Hewan** — detail `Kucing` dan `Burung` (bersuara & informasi).
2. **Menu Class Kendaraan** — detail `Mobil` dan `Motor`
   (bergerak, isi bensin, informasi).
3. **Menu Isi Bensin** — progress bar pengisian **dalam liter** (Motor 10 L,
   Mobil 40 L) dengan efek suara `water-pour.wav`.
4. **Alur Cerita (fitur baru)** — satu tombol, satu frame, satu kisah utuh:

   | Adegan | Isi |
   |--------|-----|
   | 1. Taman | Kucing-kucing duduk dan berjalan-jalan (animasi + balon "Meong!"), burung-burung berkicau di rumput dan dahan, lalu beberapa burung terbang melintas; suara kucing/burung bersahutan |
   | 2. Burung | Kawanan burung berkicau ramai di dahan dan rerumputan, burung mencoba terbang (3 gambar sequence), lalu seluruh kawanan terbang bersama |
   | 3. Jalan Raya | Ramai kendaraan: motor melintas di **jalur atas** (kiri→kanan) dan mobil sport di **jalur bawah** (kanan→kiri), berlawanan arah; suara `motor.wav` + `porsche.wav` |
   | 4. SPBU | Pengendara berdiri di samping motor, mengisi bensin **self-service** dalam liter bersama mobil (2 progress bar + `water-pour.wav`), struk pembayaran muncul, lalu saat berangkat motor dan mobil melaju dengan `motor-slowing-and-brake-sound.wav` dan `car-slow-down.wav` |

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
└── sounds/          kucing*.wav, burung*.wav, motor.wav, porsche.wav,
                     water-pour.wav, motor-slowing-and-brake-sound.wav,
                     car-slow-down.wav (RMS seragam ±-26 dBFS)
```

## Catatan Aset

- Sprite sequence (kucing, burung, motor, mobil) diolah dari `informasi gambar`
  yang diberikan (slice sheet + penghapusan latar putih).
- Gambar hasil generate: rumput, pengendara mengisi bensin self-service,
  balon ucapan, dan struk pembayaran (ditempel teks agar terbaca jelas).
