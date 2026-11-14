# Git dan keselamatan

## Git

- Pesan commit memakai Conventional Commits: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `chore:`. Contoh: `feat(reminder): notify when oil change is due`.
- Cabang: `feat/<topik>`, `fix/<topik>`. Jangan commit langsung ke `main`.
- Satu commit, satu maksud. Jangan mencampur refactor dengan perubahan perilaku.
- Jangan `git push --force`, `git reset --hard`, atau menulis ulang riwayat tanpa diminta.

## Tanyakan dulu sebelum

- menambah izin di `AndroidManifest.xml`;
- menambah atau mengganti dependency, plugin Gradle, atau versi AGP dan Kotlin;
- mengubah skema Room;
- menghapus file, folder, atau data di luar `build/`;
- mengubah `AGENTS.md`, `DESIGN.md`, atau isi `.agent/`;
- menjalankan perintah yang menghapus data aplikasi di emulator atau perangkat (`adb uninstall`, `pm clear`).

## Dilarang

- Menulis atau meng-commit keystore, kata sandi, token, atau `local.properties`.
- Mengubah file di luar folder proyek.
- Menambah pelacakan, analytics, atau panggilan jaringan yang mengirim data pengguna.
- Menonaktifkan lint, test, atau R8 untuk menyembunyikan masalah.
