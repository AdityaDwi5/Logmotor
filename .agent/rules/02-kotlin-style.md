# Gaya Kotlin

Ikuti panduan gaya Kotlin resmi dan konfigurasi `.editorconfig` proyek.

## Dasar

- Nama: `PascalCase` untuk tipe, `camelCase` untuk fungsi dan properti, `UPPER_SNAKE_CASE` untuk konstanta. Fungsi composable yang mengembalikan Unit memakai `PascalCase`.
- Utamakan `val`, `data class` immutable, dan `sealed interface` untuk state dan hasil.
- Dilarang `!!`. Gunakan `?:`, `requireNotNull` dengan pesan jelas, atau ubah desainnya.
- Tidak ada angka ajaib. Beri nama konstanta, misalnya `DEFAULT_OIL_INTERVAL_KM`.
- Fungsi pendek dengan satu tugas. Kalau butuh komentar untuk menjelaskan apa yang dilakukan, ganti nama atau pecah fungsinya.
- Komentar menjelaskan alasan, bukan isi kode. KDoc hanya untuk API publik yang tidak jelas dari namanya.
- Dilarang meninggalkan `TODO` tanpa penjelasan dan pemilik.
- Hapus import dan kode mati. Jangan meninggalkan kode yang dikomentari.

## Coroutines

- Concurrency terstruktur: `viewModelScope`, `lifecycleScope`, atau scope yang jelas pemiliknya. Dilarang `GlobalScope`.
- Dispatcher di-inject, tidak ditulis keras.
- Fungsi `suspend` aman dipanggil dari main thread. Pindahkan kerja berat ke dispatcher di dalam fungsinya.

## Format dan teks

- Uang, tanggal, dan kilometer hanya diformat lewat `core/format`. Dilarang `String.format` atau `NumberFormat` lepas di layar.
- Angka dalam kalimat memakai placeholder (`%1$s`) di `strings.xml`, bukan penggabungan string.

## Privasi

Jangan menulis plat nomor, biaya, atau isi catatan pengguna ke Logcat atau pesan galat.
