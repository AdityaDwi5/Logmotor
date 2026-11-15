# ServisKu

Aplikasi Android untuk mencatat servis dan perawatan motor, dengan pengingat berdasarkan kilometer dan waktu. Offline-first: semua data tersimpan di perangkat. Kotlin + Jetpack Compose.

File ini dibaca agen di awal setiap sesi. Rujukan lain:

- `DESIGN.md`: spesifikasi tampilan. Wajib dibaca sebelum menyentuh UI.
- `.agent/rules/`: aturan rinci arsitektur, gaya kode, UI, test, dan git.
- `.agent/workflows/`: prosedur yang dipanggil dengan `/nama-workflow`.
- `.agent/skills/`: keahlian khusus yang dimuat otomatis saat relevan.

## Komunikasi

- Balas pengguna dalam Bahasa Indonesia, ringkas dan langsung.
- Kode, nama identifier, komentar kode, dan pesan commit memakai bahasa Inggris.
- Teks yang tampil di aplikasi memakai Bahasa Indonesia dan disimpan di `res/values/strings.xml`.
- Jangan mengarang nama API, versi library, atau perilaku Android. Kalau ragu, baca dokumentasi resmi atau kode yang sudah ada, lalu katakan bagian mana yang belum pasti.
- Laporkan hasil apa adanya, termasuk build atau test yang gagal.

## Stack

- Kotlin, Jetpack Compose, Material 3
- Arsitektur MVVM + Clean Architecture (`ui`, `domain`, `data`)
- Hilt, Room (dengan KSP), DataStore, Coroutines + Flow
- Navigation Compose dengan rute type-safe (kotlinx.serialization)
- WorkManager (pengingat), CameraX atau Photo Picker (foto nota), Vico (grafik), Glance (widget, tahap lanjut)
- Gradle Kotlin DSL dan version catalog `gradle/libs.versions.toml`
- minSdk 26. compileSdk dan targetSdk mengikuti syarat terbaru Google Play.
- Test: JUnit, MockK, Turbine, Room in-memory, Compose UI Test

## Struktur paket

Satu modul `:app`. Ganti `id.serviskus.app` dengan applicationId yang dipakai.

```
app/src/main/java/id/serviskus/app/
├── core/       util, formatter, dispatcher, designsystem/ (tema dan komponen bersama)
├── data/       local/ (Room, DataStore), repository/, mapper/, work/
├── domain/     model/, repository/ (interface), usecase/
├── feature/    satu paket per fitur: vehicle, service, reminder, stats, settings
│   └── <fitur>/  XxxRoute, XxxScreen, XxxViewModel, XxxUiState, navigasi
└── di/         modul Hilt
```

## Perintah

```
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew connectedDebugAndroidTest   # butuh emulator atau perangkat
./gradlew ktlintCheck detekt          # hanya jika sudah dipasang
```

## Aturan domain

- Uang: `Long` rupiah utuh. Jangan `Float` atau `Double`.
- Odometer: `Int` kilometer. Tidak boleh lebih kecil dari catatan sebelumnya menurut tanggal kecuali pengguna mengonfirmasi koreksi.
- Tanggal servis: `LocalDate`, disimpan sebagai epoch day. Perbandingan "hari ini" memakai zona waktu perangkat.
- Jatuh tempo: sebuah komponen jatuh tempo jika `odometer >= lastDoneOdometer + intervalKm` ATAU `today >= lastDoneDate + intervalMonths`. Salah satu interval boleh kosong. Logika ini ada di `domain/usecase` dan wajib punya unit test.
- Estimasi km per hari dihitung dari dua catatan odometer terakhir dan hanya dipakai untuk perkiraan, bukan untuk menentukan jatuh tempo.

## Aturan keras

1. Aplikasi harus berfungsi penuh tanpa internet. MVP tidak memakai jaringan.
2. Jangan menambah izin di `AndroidManifest.xml` tanpa persetujuan pengguna. MVP hanya memakai `POST_NOTIFICATIONS` dan `CAMERA` (opsional).
3. Jangan mengubah atau menghapus migrasi Room yang sudah dirilis. Perubahan skema berarti versi baru, migrasi baru, dan test migrasi.
4. Jangan commit rahasia: keystore, `local.properties`, kunci API.
5. Jangan menambah dependency tanpa alasan. Sebutkan nama, versi, dan kebutuhannya.
6. Jangan menghapus atau melemahkan test supaya build hijau. Perbaiki penyebabnya.
7. Data pengguna tidak boleh keluar dari perangkat (tanpa analytics atau crash reporting) kecuali diminta eksplisit.

## Cara bekerja

1. Baca kode terkait dulu. Ikuti pola yang sudah ada sebelum membuat pola baru.
2. Perubahan yang menyentuh lebih dari 3 file atau database: tulis rencana singkat dan tunggu persetujuan.
3. Kerjakan perubahan kecil yang bisa diverifikasi. Satu commit, satu maksud.
4. Tulis atau ubah test bersamaan dengan kode.
5. Jalankan `testDebugUnitTest` dan `lintDebug` sebelum menyatakan selesai.
6. UI baru harus lolos `/design-review`.

## Mode kerja

| Peran | Untuk apa | Cara memanggil |
|---|---|---|
| Arsitek | Rencana, struktur, keputusan skema. Keluarannya rencana, bukan kode produksi. | "Bertindak sebagai Arsitek: ..." |
| Implementer | Menulis kode dan test sesuai rencana yang disetujui. Ini mode default. | (tanpa perintah) |
| Reviewer | Membaca diff, mencari bug dan pelanggaran aturan, tanpa mengubah kode. | "Bertindak sebagai Reviewer" |
| Desainer UI | Mengaudit tampilan memakai skill `anti-slop-design`. | `/design-review` |

## Definisi selesai

- [ ] Build debug berhasil
- [ ] Unit test lolos, test baru ditulis untuk logika baru
- [ ] `lintDebug` tanpa peringatan baru
- [ ] Tidak ada string UI di dalam kode, semuanya di `strings.xml`
- [ ] UI baru punya preview (terang, gelap, font 200%) dan lolos `/design-review`
- [ ] Migrasi Room dan test migrasi ada bila skema berubah
- [ ] Ringkasan: apa yang berubah, hasil test, dan apa yang belum dikerjakan

## Di luar lingkup versi 1

Catatan isi bensin, backup dan sinkronisasi cloud, widget, template jadwal per model motor, berbagi riwayat, multi-mata uang. Jangan dikerjakan kecuali diminta.
