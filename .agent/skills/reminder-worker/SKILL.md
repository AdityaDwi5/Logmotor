---
name: reminder-worker
description: Dipakai saat membuat atau mengubah pengingat servis, pengingat pajak STNK, notifikasi, WorkManager, atau izin POST_NOTIFICATIONS di ServisKu.
---

# Pengingat dengan WorkManager

Pengingat servis cukup diperiksa sekali sehari. Tidak perlu alarm tepat waktu, jadi **jangan** meminta `SCHEDULE_EXACT_ALARM` atau menjalankan foreground service.

## Worker

```kotlin
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val findDueReminders: FindDueRemindersUseCase,
    private val notifier: ReminderNotifier,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        findDueReminders().forEach { notifier.notify(it) }
        return Result.success()
    }
}
```

Logika menentukan apa yang jatuh tempo ada di `FindDueRemindersUseCase` (domain, murni, dites dengan `Clock` palsu). Worker hanya menjembatani ke notifikasi.

## Penjadwalan

```kotlin
fun scheduleDailyCheck(workManager: WorkManager) {
    val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS).build()
    workManager.enqueueUniquePeriodicWork(
        "service-reminders",
        ExistingPeriodicWorkPolicy.KEEP,
        request,
    )
}
```

WorkManager mempertahankan jadwal setelah perangkat dinyalakan ulang. Waktu eksekusi bersifat perkiraan karena sistem mengatur penghematan baterai.

## Setup Hilt untuk Worker

- Tambahkan `androidx.hilt:hilt-work` dan kompiler Hilt-nya lewat KSP.
- Kelas `Application` mengimplementasikan `Configuration.Provider` dan memakai `HiltWorkerFactory`:

```kotlin
@HiltAndroidApp
class ServisKuApp : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}
```

- Nonaktifkan inisialisasi bawaan WorkManager di manifest, sesuai dokumentasi WorkManager untuk konfigurasi kustom.

## Notifikasi

- Buat channel `service_reminders` saat `Application.onCreate()` (minSdk 26, jadi channel selalu tersedia).
- Sebelum menampilkan, periksa `NotificationManagerCompat.from(context).areNotificationsEnabled()`. Jika mati, lewati tanpa galat.
- Minta izin `POST_NOTIFICATIONS` (Android 13 ke atas) pada saat pengguna membuat aturan pengingat pertamanya, dengan penjelasan satu kalimat. Jangan meminta saat aplikasi pertama dibuka.
- `PendingIntent` memakai `FLAG_IMMUTABLE` dan membuka layar motor terkait lewat deep link type-safe.
- Teks mengikuti mikrocopy `DESIGN.md`: singkat, konkret, tanpa emoji. Contoh: "Oli mesin Vario sudah lewat 2 hari dari jadwal."

## Mencegah notifikasi berulang

Satu aturan tidak boleh memunculkan notifikasi yang sama setiap hari. Simpan `lastNotifiedOn` (`LocalDate?`) dan status terakhir yang dinotifikasikan pada `ReminderRule` (tambahkan lewat `/db-migration`). Kirim ulang hanya jika status memburuk (segera menjadi terlambat) atau setelah 7 hari tanpa tindakan.

## Pajak STNK

Tanggalnya absolut. Notifikasi pada H-30, H-7, dan hari jatuh tempo. Tahap yang sudah dikirim dicatat agar tidak terulang.

## Test

- Unit test `FindDueRemindersUseCase` dengan `Clock` palsu: tepat di batas, satu km di bawah, interval kosong, hanya interval waktu yang terlewat.
- Test worker dengan `TestListenableWorkerBuilder` dan use case palsu. Pastikan notifier dipanggil sekali per aturan yang jatuh tempo, dan tidak dipanggil dua kali pada hari yang sama.
