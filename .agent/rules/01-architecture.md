# Arsitektur

Berlaku untuk semua kode di `app/`.

## Arah dependensi

`feature (ui)` → `domain` ← `data`. Paket `domain` adalah Kotlin murni: tanpa import `android.*`, `androidx.*`, atau Room. UI tidak boleh mengimpor apa pun dari `data`.

## ViewModel

- Satu `UiState` immutable per layar, diekspos sebagai `StateFlow<UiState>`.
- Aksi pengguna masuk lewat fungsi publik di ViewModel (misalnya `onSaveClick()`), supaya logika tidak bocor ke UI.
- Efek sekali pakai (navigasi, snackbar) lewat `Channel<Effect>(Channel.BUFFERED)` yang dikonsumsi sebagai Flow. Jangan simpan efek di `UiState`.
- Ubah Flow menjadi state dengan `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)`.
- ViewModel tidak memegang `Context`, `View`, atau referensi composable.

## Domain

- Buat use case hanya jika berisi logika. Jangan membungkus satu panggilan repository tanpa logika.
- Satu use case, satu `operator fun invoke`.
- Aturan jatuh tempo, validasi odometer, dan estimasi km per hari tinggal di domain dan wajib punya unit test.
- Ambil waktu lewat `Clock` yang di-inject, bukan `LocalDate.now()` langsung, agar bisa dites.

## Data

- Repository: `Flow` untuk data yang diamati, `suspend` untuk tulis.
- Entity Room tidak keluar dari paket `data`. Petakan ke model domain di mapper.
- Uang `Long` rupiah, tanggal `LocalDate` (epoch day lewat TypeConverter), kilometer `Int`.
- Room: `exportSchema = true` dan folder `schemas/` ikut di-commit. Foreign key harus punya indeks. Menghapus motor menghapus servisnya (`CASCADE`). Menyimpan servis beserta item harus dalam satu transaksi.
- Setiap perubahan skema lewat `/db-migration`.
- Jangan pernah memakai `fallbackToDestructiveMigration()`. Data di perangkat adalah satu-satunya salinan milik pengguna.

## Hilt

- Injeksi lewat konstruktor. Interface diikat dengan `@Binds`.
- Dispatcher di-inject lewat qualifier (misalnya `@IoDispatcher`), bukan `Dispatchers.IO` langsung.

## Galat

- Kegagalan yang wajar terjadi (validasi, data tidak ditemukan) memakai tipe hasil di domain, bukan exception.
- Jangan menelan exception diam-diam. Selalu lempar ulang `CancellationException`.
