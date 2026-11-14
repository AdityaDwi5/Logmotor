---
description: Tambah fitur baru dari rencana sampai test lolos
---

# /new-feature

1. Tanyakan tujuan fitur dan kriteria selesainya jika belum jelas (maksimal 3 pertanyaan). Pastikan fitur tidak termasuk "Di luar lingkup versi 1" di `AGENTS.md`.
2. Baca `AGENTS.md`, `DESIGN.md`, dan kode terkait yang sudah ada.
3. Tulis rencana singkat: file yang dibuat atau diubah, perubahan skema, dan test yang akan ditulis. Jika menyentuh lebih dari 3 file atau database, berhenti dan tunggu persetujuan.
4. Kerjakan domain lebih dulu: model, use case, dan unit test-nya.
5. Kerjakan data: entity, DAO, repository, mapper. Jika skema berubah, jalankan alur `/db-migration`. Gunakan skill `room-entity`.
6. Kerjakan UI: `UiState`, ViewModel, `Route`, `Screen`, navigasi, preview. Gunakan skill `compose-screen`.
7. Pindahkan semua teks UI ke `strings.xml` (Bahasa Indonesia).
8. Jalankan `./gradlew testDebugUnitTest lintDebug`. Perbaiki sampai hijau tanpa melemahkan test.
9. Jalankan `/design-review` untuk setiap layar baru atau yang berubah.
10. Laporkan dalam maksimal 8 baris: apa yang berubah, hasil test, dan apa yang sengaja belum dikerjakan.
