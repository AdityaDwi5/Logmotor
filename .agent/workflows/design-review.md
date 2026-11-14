---
description: Audit tampilan terhadap DESIGN.md memakai skill anti-slop-design
---

# /design-review

1. Muat skill `anti-slop-design` dan baca `DESIGN.md`.
2. Tentukan layar yang diaudit. Defaultnya layar yang berubah di diff terbaru.
3. Jika emulator atau perangkat tersedia, ambil tangkapan layar tema terang, tema gelap, dan font 200% (`adb exec-out screencap -p`). Jika tidak ada, audit dari kode dan preview, dan katakan bahwa tangkapan layar belum diperiksa.
4. Jalankan semua pemeriksaan di skill: sinyal slop, token, teks, aksesibilitas, dan lima uji cepat.
5. Tampilkan hasil sebagai tabel dengan kolom: pemeriksaan, lulus atau gagal, bukti (file atau elemen), perbaikan.
6. Perbaiki yang gagal, lalu periksa ulang item itu. Maksimal 3 putaran; sisanya dilaporkan apa adanya.
7. Penyimpangan yang disengaja dari `DESIGN.md` dicatat di bagian "Keputusan" beserta alasannya, setelah pengguna setuju.
