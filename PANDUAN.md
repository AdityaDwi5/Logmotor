# Paket agen ServisKu untuk Antigravity

## Isi paket

```
AGENTS.md                      konteks proyek, aturan keras, mode kerja
DESIGN.md                      spesifikasi tampilan anti-slop (warna, font, spasi, teks)
PROMPT-DESAIN-ANTI-SLOP.md     prompt portabel untuk AI lain
.agent/
├── rules/
│   ├── 01-architecture.md
│   ├── 02-kotlin-style.md
│   ├── 03-compose-and-design.md
│   ├── 04-testing-and-quality.md
│   └── 05-git-and-safety.md
├── workflows/
│   ├── new-feature.md         /new-feature
│   ├── add-screen.md          /add-screen
│   ├── db-migration.md        /db-migration
│   ├── fix-bug.md             /fix-bug
│   ├── design-review.md       /design-review
│   └── release-check.md       /release-check
└── skills/
    ├── compose-screen/SKILL.md
    ├── room-entity/SKILL.md
    ├── reminder-worker/SKILL.md
    └── anti-slop-design/SKILL.md
```

## Cara pasang

1. Salin seluruh isi folder ini ke root proyek Android, yaitu folder yang sama dengan `settings.gradle.kts`. Folder `.agent/` tersembunyi, jadi pastikan ikut tersalin.
2. Buka proyek di Antigravity. `AGENTS.md` dibaca otomatis. Antigravity juga membaca `GEMINI.md`, dan jika file itu ada, ia diutamakan atas `AGENTS.md`.
3. Ganti `id.serviskus.app` di `AGENTS.md` dengan applicationId aslimu.
4. Commit semuanya agar konfigurasi ikut terversi.

## Catatan soal versi Antigravity

- Dokumentasi yang saya temukan tidak seragam soal nama folder: ada yang menulis `.agent/`, ada yang `.agents/`. Paket ini memakai `.agent/`. Jika rules, workflows, atau skills tidak terbaca di versimu, ganti nama folder menjadi `.agents/`.
- Batas 12.000 karakter per file berlaku untuk rules dan workflows. Semua file di paket ini jauh di bawah batas itu.
- Jika panel pengaturan rules menyediakan mode aktivasi, pilih "Always On" untuk rule 01 sampai 05.
- Saya belum bisa menjalankan paket ini di Antigravity secara langsung, jadi anggap pemasangan pertama sebagai uji coba.

## Cara memakai

- **Workflow** dipanggil dengan garis miring, misalnya `/new-feature tambah pengingat pajak STNK`.
- **Skill** dimuat agen sendiri saat tugasnya cocok. Minta pekerjaan layar Compose dan `compose-screen` ikut terpakai.
- **Mode peran**: tulis "Bertindak sebagai Reviewer" untuk tinjauan tanpa perubahan kode, atau "Bertindak sebagai Arsitek" untuk rencana saja.
- **Desain**: `/design-review` memeriksa layar terhadap `DESIGN.md`. `PROMPT-DESAIN-ANTI-SLOP.md` dipakai di luar Antigravity, misalnya di chat AI atau alat desain.

## Yang perlu kamu sesuaikan

- Versi library tidak ditulis di sini. Agen diminta tidak menebak, jadi isi `gradle/libs.versions.toml` dengan versi stabil terbaru saat proyek dibuat.
- Daftar komponen dan interval bawaan (`PartType`) belum ditentukan. Putuskan angka awal per jenis motor sebelum menulis fitur pengingat.
- Warna dan font di `DESIGN.md` adalah usulan awal. Kalau diubah, hitung ulang kontrasnya sebelum dipakai.
