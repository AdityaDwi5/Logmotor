---
description: Ubah skema Room dengan aman (versi baru, migrasi, test migrasi)
---

# /db-migration

1. Tulis perubahan skema yang diminta dan dampaknya ke data lama: kolom baru, nilai default, data yang perlu diisi ulang. Tunggu persetujuan.
2. Pastikan file skema versi saat ini ada di `app/schemas/`. Jika tidak ada, berhenti dan laporkan.
3. Ubah entity. Naikkan `version` di `@Database` sebanyak satu.
4. Tambahkan migrasi. Pakai `AutoMigration` bila perubahannya sederhana (tambah kolom atau tabel). Jika tidak, tulis `Migration` manual dengan SQL eksplisit. Jangan menyentuh migrasi lama.
5. Bangun proyek agar file skema versi baru dibuat di `app/schemas/`, lalu pastikan file itu ikut di-commit.
6. Tulis test migrasi dengan `MigrationTestHelper`: buat database di versi lama, isi data contoh, migrasikan, lalu periksa data lama terjaga dan kolom baru berisi nilai yang benar.
7. Perbarui DAO, mapper, dan model domain yang terkena.
8. Jalankan `testDebugUnitTest` dan `connectedDebugAndroidTest` (bila ada emulator).
9. Laporkan: versi lama ke baru, jenis migrasi, dan hasil test.
