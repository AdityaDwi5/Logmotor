---
name: anti-slop-design
description: Dipakai sebelum dan sesudah membangun atau mengubah UI apa pun di ServisKu, dan oleh /design-review. Mencegah tampilan generik ala AI (gradien ungu, kartu bertumpuk, emoji sebagai ikon, copy klise) dan memeriksa UI terhadap DESIGN.md.
---

# Desain anti-slop

"Slop" adalah hasil dari pilihan bawaan yang tidak pernah benar-benar diputuskan: warna default, kartu di mana-mana, sapaan basa-basi, ilustrasi stok. Tampilannya rapi tetapi bisa menjadi aplikasi apa saja. Tugas skill ini adalah memastikan setiap keputusan visual punya alasan yang bersumber dari isi ServisKu.

## Sebelum membangun

1. Baca `DESIGN.md`.
2. Tulis satu kalimat: apa yang harus diketahui atau dilakukan pengguna di layar ini dalam 5 detik pertama?
3. Tentukan satu elemen utama (biasanya sebuah angka atau status). Elemen itu paling besar dan paling kontras.
4. Baru setelah itu tata letak. Susun dari kebutuhan informasi, bukan dari komponen yang tersedia.

## Sinyal slop dan penggantinya

| Sinyal slop | Ganti dengan |
|---|---|
| Gradien ungu ke biru, efek menyala, kaca buram (glassmorphism) | Permukaan datar dari token `surface` dan garis `outline` 1dp |
| Warna bawaan Material 3 (ungu) atau dynamic color tanpa disengaja | Token warna dan aksen motor di `DESIGN.md` |
| Emoji atau ikon campur gaya | Material Symbols Outlined, satu bobot, 24dp |
| "Kartu bertumpuk": semuanya di kartu berbayang | Baris dengan pemisah. Kartu hanya untuk benda yang berdiri sendiri (motor) |
| Sapaan "Halo, Aditya!" atau "Selamat datang kembali" | Langsung ke isi: motor aktif dan status perawatannya |
| Angka besar tanpa keputusan (vanity metrics) | Hanya angka yang mengarah ke tindakan: sisa km, biaya bulan ini dibanding rata-rata |
| Donat, grafik 3D, warna pelangi, legenda rumit | Batang urut besar ke kecil, label langsung, satu warna |
| Teks contoh "Lorem ipsum", "Item 1", "John Doe", angka bulat sempurna | Data realistis: "Bengkel Maju Jaya", "Oli mesin", "Rp 58.000", "12.450 km" |
| Keadaan kosong dengan ilustrasi stok atau emoji sedih | Satu kalimat dan satu tombol |
| Animasi memantul, shimmer di semua tempat, paralaks | 150 sampai 250 ms, hanya untuk perubahan status |
| Semua teks rata tengah, semua ukuran font sama | Rata kiri, tiga tingkat hierarki yang jelas, angka memakai font mono |
| Tombol "Kirim", "Lanjutkan", "OK" | Kata kerja + objek: "Simpan servis" |
| Status hanya lewat warna | Ikon + kata + warna |
| Padding 16dp seragam di semua tempat | Skala spasi dengan pengelompokan: dekat berarti berhubungan |
| Dialog konfirmasi untuk semuanya, toast untuk setiap sukses | Snackbar dengan "Urungkan" untuk tindakan yang bisa dibatalkan |
| "Oops! Terjadi kesalahan" | Apa yang gagal dan cara memperbaikinya |
| Kata bombastis: "mulus", "canggih", "revolusioner" | Kata kerja dan benda yang nyata |

## Lima uji cepat

1. **Uji ganti-nama.** Ganti nama aplikasi dan warna aksen. Jika layar terlihat sama persis seperti aplikasi lain, ia belum punya sudut pandang.
2. **Uji 5 detik.** Tutup layar setelah 5 detik. Apakah orang bisa menyebut status motornya?
3. **Uji sipit.** Pandang sambil menyipitkan mata. Apakah elemen utama masih menonjol dan urutannya jelas?
4. **Uji hapus.** Hapus tiap elemen dekoratif satu per satu. Jika tidak ada informasi yang hilang, biarkan terhapus.
5. **Uji angka.** Apakah angka yang paling penting adalah yang paling besar, rata kanan atau sejajar, dan memakai font mono?

## Pemeriksaan teknis

- Warna hanya dari token. Tidak ada `Color(0xFF...)` di layar.
- Teks hanya dari `MaterialTheme.typography`. Tidak ada `fontSize` lepas.
- Spasi hanya dari skala 4/8/12/16/24/32/48.
- Rasio kontras mengikuti tabel di `DESIGN.md`. Warna baru harus dihitung dulu, tidak boleh ditebak.
- Layar tetap utuh pada font 200% dan mode gelap.
- Urutan TalkBack logis dan baris status dibacakan utuh.

## Format hasil review

Tampilkan tabel: pemeriksaan | lulus atau gagal | bukti (file atau elemen) | perbaikan. Akhiri dengan satu kalimat: layar ini boleh dianggap selesai atau belum, dan alasannya.

## Jika perlu menyimpang

Penyimpangan dari `DESIGN.md` boleh jika punya alasan yang kuat. Catat di bagian "Keputusan" pada `DESIGN.md`, dan minta persetujuan pengguna dulu.
