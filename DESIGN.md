# DESIGN.md: ServisKu

## Arah

Buku servis bengkel yang muat di saku. Tenang, hangat, dan padat dengan informasi yang bisa dipercaya. Bukan dashboard, bukan aplikasi gaya hidup.

Empat prinsip, urut prioritas:

1. **Angka adalah isi utama.** Odometer, sisa kilometer, dan biaya paling besar dan paling jelas. Ditulis dengan font monospace agar rapi dan mudah dibandingkan.
2. **Status terbaca dalam satu detik.** Terlambat, segera, atau aman. Selalu ikon dan kata, bukan warna saja.
3. **Setiap motor punya warnanya sendiri.** Pengguna memilih satu dari delapan warna, lalu warna itu menjadi aksen di seluruh layar motor tersebut. Ini satu-satunya "dekorasi", dan ia berasal dari isi aplikasi.
4. **Tidak ada hiasan tanpa fungsi.** Kalau dihapus dan tidak ada informasi yang hilang, hapus.

## Warna

Semua pasangan teks dan komponen interaktif di bawah sudah dihitung rasio kontrasnya (WCAG): teks minimal 4,5:1, garis kolom input dan aksen minimal 3:1. Token `outline` hanya untuk garis pemisah dekoratif dan tidak dihitung.

| Token          | Terang  | Gelap   | Dipakai untuk                     |
| -------------- | ------- | ------- | --------------------------------- |
| bg             | #F5F2EB | #151412 | latar layar                       |
| surface        | #FDFCF8 | #1E1C19 | kartu motor, sheet, dialog        |
| surfaceVariant | #EAE5DA | #2A2723 | chip, kolom input, baris terpilih |
| ink            | #1C1B18 | #ECE8DF | teks utama                        |
| inkMuted       | #5E5A50 | #A8A294 | teks sekunder, label              |
| outline        | #CFC8B8 | #3A3630 | garis pemisah halus               |
| outlineStrong  | #8A8374 | #7D766A | batas kolom input dan kontrol     |
| overdue        | #B3261E | #FFB4AB | terlambat                         |
| soon           | #8A5A00 | #E8B84A | segera                            |
| ok             | #2F6B3A | #8FD19A | aman                              |

### Aksen per motor

| Nama               | Terang  | Gelap   |
| ------------------ | ------- | ------- |
| biru-oli (default) | #1D4E6B | #8FC1E0 |
| teal               | #14675E | #7FCBC0 |
| ungu               | #5B3F8C | #C4B0EC |
| plum               | #8C3B62 | #E5A5C4 |
| coklat             | #7A5230 | #D9B48C |
| baja               | #4A5560 | #AEB9C4 |
| biru-terang        | #2A66B0 | #9DC1F0 |
| arang              | #2B2B2B | #D0D0D0 |

Merah, kuning, dan hijau sengaja tidak ada di daftar aksen supaya tidak tertukar dengan status. `onPrimary` adalah putih pada tema terang dan `#151412` pada tema gelap.

### Pemetaan ke Material 3

- `background` = bg, `surface` = surface, `surfaceVariant` = surfaceVariant
- `onBackground` dan `onSurface` = ink, `onSurfaceVariant` = inkMuted
- `outline` = outlineStrong, `outlineVariant` = outline
- `primary` = aksen motor yang sedang aktif, `onPrimary` seperti di atas
- Material 3 tidak punya warna status, jadi sediakan lewat CompositionLocal:

```kotlin
@Immutable
data class StatusColors(val overdue: Color, val soon: Color, val ok: Color)

val LocalStatusColors = staticCompositionLocalOf<StatusColors> {
    error("StatusColors not provided; wrap content in ServisKuTheme")
}
```

Dynamic color (Material You) mati secara default supaya identitas warna tetap. Boleh ditawarkan sebagai opsi di Pengaturan.

## Tipografi

IBM Plex Sans untuk teks dan IBM Plex Mono untuk angka penting (odometer, plat, biaya, sisa km). Keduanya berlisensi OFL. Dibundel di `res/font/` supaya aplikasi tidak butuh internet untuk menampilkan font.

| Peran        | Font      | Ukuran/tinggi baris        | Bobot    |
| ------------ | --------- | -------------------------- | -------- |
| odometerHero | Plex Mono | 40/48                      | Medium   |
| numberLarge  | Plex Mono | 24/32                      | Medium   |
| titleLarge   | Plex Sans | 22/28                      | SemiBold |
| titleMedium  | Plex Sans | 16/24                      | SemiBold |
| body         | Plex Sans | 16/24                      | Regular  |
| bodySmall    | Plex Sans | 14/20                      | Regular  |
| label        | Plex Sans | 12/16, letterSpacing 0,4sp | Medium   |

Teks minimal 12sp, teks bacaan minimal 14sp. Selalu `sp`. Angka dalam daftar yang memakai Plex Sans diberi `fontFeatureSettings = "tnum"` agar sejajar.

## Spasi dan bentuk

- Grid 4dp. Skala: 4, 8, 12, 16, 24, 32, 48. Margin layar 16dp.
- Kedekatan menunjukkan hubungan: 8dp antar-item dalam satu grup, 24dp antar-grup.
- Sudut: 8dp untuk chip dan input, 12dp untuk kartu motor, pill hanya untuk chip status.
- Tanpa bayangan pada kartu. Pemisahan lewat `surface` di atas `bg` dan garis `outline` 1dp. Elevasi hanya untuk dialog, bottom sheet, dan tombol aksi utama.
- Target sentuh minimal 48dp.

## Layar kunci

**Beranda**

1. Header motor aktif: nama, plat (mono), pemilih motor. Di bawahnya odometer besar dengan tombol "Perbarui km".
2. "Perawatan mendatang": daftar baris yang diurutkan dari paling mendesak. Satu baris berisi ikon komponen, nama, "320 km lagi atau 12 hari", dan chip status (ikon + kata).
3. Riwayat servis terakhir sebagai linimasa vertikal sederhana.

Tanpa ucapan selamat datang, banner, atau ilustrasi.

**Form servis**
Satu kolom. Kolom km dan biaya memakai keypad angka dengan pemisah ribuan otomatis. Item servis berupa chip yang bisa dipilih lebih dari satu. Tombol "Simpan servis" menempel di bawah. Isian tidak hilang saat layar diputar.

**Statistik**
Batang biaya per bulan, lalu daftar biaya per komponen urut dari terbesar dalam bentuk batang horizontal. Label langsung di batang, tanpa legenda untuk satu seri, tanpa donat.

## Teks dan mikrocopy

- Bahasa Indonesia, kalimat biasa, huruf kapital hanya di awal. Tanpa emoji dan tanpa tanda seru.
- Tombol memakai kata kerja + objek: "Simpan servis", "Tambah motor". Bukan "Kirim" atau "Lanjutkan".
- Pesan galat menyebut apa yang gagal dan cara memperbaikinya: "Gagal menyimpan. Ruang penyimpanan penuh, hapus beberapa file lalu coba lagi."
- Keadaan kosong: satu kalimat dan satu tombol. "Belum ada servis tercatat." dengan tombol "Catat servis pertama".
- Format: `Rp 1.250.000`, `12.450 km`, `14 Nov 2026`, `3 hari lagi`, `terlambat 2 hari`. Pemformatan hanya lewat `core/format`.
- Data contoh dan preview harus realistis: nama bengkel, komponen, dan harga yang masuk akal, tidak bulat sempurna semua.

## Gerak

150 sampai 250 ms dengan easing standar Material, hanya untuk perubahan status dan kesinambungan navigasi. Tidak ada animasi dekoratif. Jika skala animasi sistem (`ANIMATOR_DURATION_SCALE`) bernilai 0, matikan animasi yang tidak esensial.

## Aksesibilitas

- Layar tetap terbaca pada font 200% tanpa teks terpotong.
- Urutan TalkBack logis. Baris perawatan dibacakan utuh: "Oli mesin, terlambat 2 hari".
- `contentDescription` untuk ikon yang membawa arti, `null` untuk ikon dekoratif.

## Ikon

Material Symbols Outlined, satu bobot, ukuran dasar 24dp. Tidak ada emoji sebagai ikon. Tidak ada ilustrasi di MVP.

## Review

Gunakan `/design-review` (skill `anti-slop-design`) sebelum menyatakan UI selesai. Penyimpangan yang disengaja dari dokumen ini dicatat di bagian di bawah beserta alasannya.

## Keputusan

- **Auto Backup (`allowBackup`):** `android:allowBackup="true"` bersama `backup_rules.xml` dan `data_extraction_rules.xml` bawaan template sementara dibiarkan pada tahap kerangka dan menjadi hal yang masih terbuka. Perlu ditinjau sebelum rilis karena aturan privasi di `AGENTS.md` menetapkan data pengguna tidak boleh keluar dari perangkat.
