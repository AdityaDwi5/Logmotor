---
description: Perbaiki bug dengan test yang mereproduksinya lebih dulu
---

# /fix-bug

1. Ulangi gejala dengan kata-kata sendiri dan pastikan sudah benar. Tanyakan langkah reproduksi jika belum ada.
2. Cari penyebabnya di kode. Jangan menebak: baca alur datanya dan sebutkan baris yang bermasalah.
3. Tulis test yang gagal karena bug ini. Jalankan dan pastikan ia gagal dengan alasan yang benar.
4. Buat perbaikan sekecil mungkin. Jangan merapikan kode lain di commit yang sama.
5. Jalankan test baru, lalu `./gradlew testDebugUnitTest lintDebug`.
6. Laporkan penyebab utama, perbaikan, test yang ditambahkan, dan kemungkinan bug serupa di tempat lain (tanpa memperbaikinya).
