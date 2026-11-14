# Compose dan tampilan

Baca `DESIGN.md` sebelum membuat atau mengubah UI apa pun. Dokumen itu menentukan warna, font, spasi, dan teks. Rule ini hanya mengatur kode.

## Composable

- Layar dipecah dua: `XxxRoute` (mengambil ViewModel dan mengumpulkan state) dan `XxxScreen` (stateless, hanya menerima state dan lambda). Hanya `XxxScreen` yang punya preview dan UI test.
- `modifier: Modifier = Modifier` adalah parameter opsional pertama dan diteruskan ke elemen akar.
- Hoist state. Composable tidak membuat ViewModel sendiri kecuali di `Route`.
- Kumpulkan Flow dengan `collectAsStateWithLifecycle()`.
- `LazyColumn` dan `LazyRow`: selalu beri `key` dan `contentType`.
- Tidak ada pekerjaan berat, I/O, atau pemformatan mahal di dalam composition. Gunakan `remember`, `derivedStateOf`, atau hitung di ViewModel.
- Parameter composable memakai tipe yang stabil. Bungkus list dari domain di `UiState` immutable.

## Tema

- Semua warna dari `MaterialTheme.colorScheme` atau `LocalStatusColors`. Dilarang menulis `Color(0xFF...)` di luar `core/designsystem/`.
- Semua ukuran teks dari `MaterialTheme.typography`. Dilarang `fontSize = ...` di layar.
- Spasi dari objek `Spacing` (4, 8, 12, 16, 24, 32, 48 dp). Dilarang angka dp lepas di luar skala itu.
- Dynamic color mati secara default.

## Aksesibilitas

- Target sentuh minimal 48dp.
- `contentDescription` bermakna untuk ikon yang membawa arti, `null` untuk ikon dekoratif.
- Status tidak boleh hanya berupa warna. Selalu ikon dan teks.
- Periksa tampilan pada font 200% dan mode gelap.

## Preview

Setiap layar dan komponen bersama punya preview lewat anotasi `@ServisKuPreviews` (terang, gelap, font 200%). Data preview harus realistis, bukan "Item 1".

## Teks

Semua string UI ada di `strings.xml`. Format uang, tanggal, dan kilometer lewat `core/format`.

## Anti-slop

Sebelum dan sesudah membangun UI, gunakan skill `anti-slop-design`. UI baru belum selesai sebelum `/design-review` lulus.
