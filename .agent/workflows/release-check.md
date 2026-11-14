---
description: Pemeriksaan sebelum rilis ke Play Store (internal testing atau produksi)
---

# /release-check

1. Pastikan `versionCode` naik dan `versionName` sesuai. Tampilkan nilai lama dan baru.
2. Jalankan `./gradlew testDebugUnitTest lintRelease`, dan `connectedDebugAndroidTest` bila ada emulator.
3. Bangun `./gradlew bundleRelease` dengan R8 aktif. Laporkan jika build gagal atau ada peringatan baru.
4. Periksa manifest hasil gabungan: hanya izin yang disetujui di `AGENTS.md`. Laporkan izin tambahan yang masuk dari library.
5. Periksa aturan R8 untuk Room, Hilt, WorkManager, dan kotlinx.serialization. Jangan menambah aturan `-keep` yang luas tanpa alasan.
6. Pastikan tidak ada keystore, kata sandi, atau `local.properties` di riwayat git maupun working tree.
7. Susun daftar pengujian manual untuk pengguna: instal bersih, upgrade dari versi sebelumnya dengan data lama (migrasi), notifikasi di Android 13 ke atas, mode gelap, font 200%.
8. Ingatkan pengisian formulir Data safety di Play Console sesuai kenyataan.
9. Keluarkan daftar centang akhir dengan tiga status: lulus, gagal, belum diperiksa.
