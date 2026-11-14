---
description: Tambah layar Compose baru lengkap dengan ViewModel, navigasi, preview, dan test
---

# /add-screen

1. Tanyakan nama layar, data yang ditampilkan, dan aksi pengguna jika belum jelas.
2. Baca `DESIGN.md` dan satu layar yang sudah ada sebagai contoh pola.
3. Muat skill `compose-screen`. Buat `UiState`, ViewModel, `Route`, `Screen`, dan rute type-safe.
4. Sambungkan ke NavHost.
5. Tangani empat kondisi: memuat, kosong, galat, berisi. Teks kosong dan galat mengikuti aturan mikrocopy di `DESIGN.md`.
6. Tambahkan preview `@ServisKuPreviews`, satu test ViewModel (Turbine), dan satu Compose UI test untuk alur utama.
7. Jalankan `./gradlew testDebugUnitTest lintDebug`, lalu `/design-review`.
