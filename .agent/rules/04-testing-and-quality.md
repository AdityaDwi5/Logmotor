# Test dan kualitas

## Wajib punya test

| Yang diuji | Jenis | Catatan |
|---|---|---|
| Logika jatuh tempo, validasi odometer, estimasi km per hari | Unit | Termasuk batas: tepat di batas, satu di bawah, interval kosong |
| Formatter uang, tanggal, kilometer | Unit | Contoh: 0, 999, 1.000, 1.250.000 |
| ViewModel | Unit + Turbine | Uji urutan state: loading, konten, galat |
| DAO dan query | Instrumented, Room in-memory | Termasuk cascade hapus dan transaksi servis + item |
| Migrasi | Instrumented, `MigrationTestHelper` | Satu test per migrasi, memakai file di `schemas/` |
| Worker pengingat | `TestListenableWorkerBuilder` | Jangan menunggu waktu sungguhan |
| Layar penting | Compose UI Test | Pakai semantics dan teks, tag hanya jika terpaksa |

## Cara menulis test

- Nama test berupa kalimat yang menjelaskan perilaku, misalnya `` `jatuh tempo saat odometer melewati batas` ``.
- Pola Arrange, Act, Assert, dengan satu perilaku per test.
- Pakai fake repository untuk test domain dan ViewModel. MockK hanya untuk memverifikasi interaksi.
- Waktu lewat `Clock` palsu. Test tidak boleh bergantung pada tanggal sungguhan atau zona waktu mesin.
- Test harus deterministik. Tidak ada `Thread.sleep`.

## Gerbang kualitas

Sebelum menyatakan selesai, jalankan dan laporkan hasilnya:

```
./gradlew testDebugUnitTest lintDebug
```

Jika perubahan menyentuh Room, WorkManager, atau UI, jalankan juga `./gradlew connectedDebugAndroidTest` bila emulator atau perangkat tersedia. Jika tidak tersedia, katakan bahwa test itu belum dijalankan.

## Bug

Perbaikan bug dimulai dari test yang gagal yang mereproduksinya, baru kemudian perbaikannya.
