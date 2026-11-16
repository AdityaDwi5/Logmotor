# ServisKu

Aplikasi Android **offline-first** untuk mencatat servis dan perawatan sepeda motor, dengan pengingat berdasarkan kilometer dan waktu. Seluruh data tersimpan secara lokal di perangkat tanpa memerlukan koneksi internet.

## Status pengembangan saat ini

- **Kerangka & Sistem Desain:** modul tunggal `:app` (`id.serviskus.app`) dengan Kotlin, Jetpack Compose, Material 3 (dynamic color mati secara default), palet terang & gelap terverifikasi rasio kontras (`ContrastTest`), 8 warna aksen motor, tipografi lokal IBM Plex Sans dan IBM Plex Mono, skala spasi 4dp, `LocalStatusColors`, serta anotasi `@ServisKuPreviews`.
- **Lapisan Domain & Data Motor/Servis:** database Room v1 (`vehicle`, `part_type`, `service_record`, `service_item`) dengan ekspor skema di `app/schemas/`, `LocalDate` disimpan sebagai epoch day, uang sebagai `Long` rupiah, validasi odometer berdasarkan urutan tanggal (`AddServiceRecordUseCase`), validasi biaya (`InvalidCost`), serta transaksi atomik simpan servis dan pembaruan `current_odometer`.
- **Layar Beranda:** kartu motor aktif (`odometerHero`, plat nomor monospace, pemilih motor berbasis DataStore), daftar **Perawatan mendatang** diurutkan dari paling mendesak (pada build `debug` memakai `FakeUpcomingMaintenanceProvider`, sedangkan pada build `release` memakai `EmptyUpcomingMaintenanceProvider` sementara sebelum `ReminderRule` dibangun), linimasa riwayat servis terakhir, dan penanganan kondisi memuat, kosong, galat, serta perawatan kosong.
- **Belum dikerjakan:** layar tambah/kelola motor, form perbarui kilometer & catat servis, serta entitas, use case, dan worker `ReminderRule`.

## Tech stack

- **Bahasa & Build:** Kotlin `2.2.10`, Android Gradle Plugin `9.1.1`, KSP `2.3.6`, Gradle Kotlin DSL (`gradle/libs.versions.toml`)
- **SDK:** `minSdk 26`, `compileSdk 36.1`, `targetSdk 36`, Java 17 compatibility
- **UI & Navigasi:** Jetpack Compose (BOM `2026.02.01`), Material 3, Navigation Compose `2.9.8` (type-safe dengan `kotlinx-serialization-json` `1.9.0`)
- **Arsitektur & Persistensi:** MVVM + Clean Architecture (`domain` Kotlin murni, `data`, `feature`), Hilt `2.60.1` (`androidxHilt` `1.3.0`), Room `2.8.5`, DataStore Preferences `1.3.0`, WorkManager `2.12.0`
- **Pengujian:** JUnit `4.13.2`, `kotlinx-coroutines-test` `1.9.0`, Turbine `1.2.1`, AndroidX JUnit `1.3.0`, Espresso `3.7.0`, Compose UI Test, Room in-memory

## Struktur utama

```text
app/src/
├── main/java/id/serviskus/app/
│   ├── core/        # designsystem/ (tema, warna, tipografi, spasi), dispatcher/, format/
│   ├── data/        # local/ (entity, DAO, TypeConverter, database), mapper/, repository/
│   ├── domain/      # model/, repository/ (interface), usecase/ (Kotlin murni)
│   ├── feature/     # home/ (UiState, ViewModel, Route/Screen, navigasi)
│   ├── navigation/  # ServisKuNavHost
│   └── di/          # modul Hilt
├── debug/           # FakeUpcomingMaintenanceProvider (data contoh debug sementara)
├── release/         # EmptyUpcomingMaintenanceProvider (daftar kosong untuk rilis)
├── test/            # unit test (kontras, token, formatter, mapper, domain, ViewModel)
└── androidTest/     # instrumented test (Room DAO, transaksi, Compose UI Beranda)
```

## Perintah build dan pengujian

```bash
./gradlew assembleDebug              # Build APK debug
./gradlew testDebugUnitTest          # Jalankan JVM unit test
./gradlew lintDebug                  # Pemeriksaan Android Lint
./gradlew connectedDebugAndroidTest  # Jalankan instrumented test (butuh perangkat/emulator)
./gradlew assembleRelease            # Build APK rilis (unsigned)
```

## Rujukan & lisensi

- Konteks dan aturan pengembangan proyek: [AGENTS.md](AGENTS.md)
- Spesifikasi visual dan tipografi: [DESIGN.md](DESIGN.md)
- Font IBM Plex Sans dan IBM Plex Mono dibundel di `app/src/main/res/font/` di bawah **SIL Open Font License 1.1** ([LICENSES/IBM-Plex-OFL.txt](LICENSES/IBM-Plex-OFL.txt)).
- Ikon antarmuka menggunakan **Material Symbols Outlined**.
