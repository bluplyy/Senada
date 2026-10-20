# Senada — Pemutar Musik Lokal Android

**Senada** adalah aplikasi pemutar musik lokal Android berbasis **Jetpack Compose** dan **AndroidX Media3**, yang dirancang dengan estetika *liquid glass*, tipografi rapi, palet warna bertema bumi (*earthy & calm*), dan 100% offline tanpa analitik, akun, streaming, maupun iklan.

Implementasi ini dibangun secara presisi mengacu pada dokumen desain:
- **Design Tokens**: `SzO06` (*00 · Sistem desain / Senada*)
- **Perilaku & Handoff**: `rU68D` (*06 / Perilaku, aksesibilitas & handoff*)
- **15 Frame Spesifikasi**: Frame `01` hingga `14` serta frame `18` (pemutar jeda/gelap).

[![Download APK](https://img.shields.io/badge/Download-APK%20(v1.0.0)-596749?style=for-the-badge&logo=android&logoColor=white)](https://github.com/bluplyy/Senada/releases/latest)

> **📲 [Klik di sini untuk Download Senada.apk langsung di HP](https://github.com/bluplyy/Senada/releases/latest)**

---

## 📱 Fitur Utama & Kesesuaian Desain

1. **100% Lokal & Offline:**
   - Membaca koleksi lagu langsung dari `MediaStore.Audio.Media` perangkat.
   - Pemantauan otomatis perubahan file melalui `ContentObserver` dengan *debounce*.
   - Tidak memerlukan koneksi internet maupun akun pengguna.

2. **Arsitektur Pemutaran Single Source of Truth:**
   - Background playback menggunakan `MediaSessionService` dan `ExoPlayer` (`PlaybackService.kt`).
   - UI terhubung melalui `MediaController` yang memancarkan `StateFlow<PlaybackState>`.
   - Mengelola *Audio Focus* dan *Becoming Noisy* (otomatis jeda saat headphone dicabut).
   - Notifikasi pemutaran sistem Android dengan kontrol play/pause, prev, next, dan seek.

3. **Desain & Liquid Glass Surfaces:**
   - **Gradasi Latar:** 180° vertikal linear gradient (`#F3ECE6` 0% → `#ECE0DA` 55% → `#D8DECE` 100%).
   - **Liquid Glass:** Fill semi-transparan `Color(0x8CFFF9F0)`, border putih 70% `Color(0xB3FFFFFF)`, dan bayangan halus.
   - **Floating Dock:** Menampung *Mini Player* (ketinggian 72 dp) dan *Bottom Navigation* (ketinggian 80 dp).
   - **IME-Aware:** Dock otomatis tersembunyi dengan animasi transisi saat keyboard virtual aktif (Frame 14).

4. **Adaptasi Palet & Kontras Dinamis (Pemutar Penuh):**
   - Mengekstrak warna dominan album art menggunakan AndroidX `Palette` (`ArtworkLoader.kt`).
   - Transisi warna latar 240 ms saat lagu berganti.
   - Kontras teks judul, artis, dan ikon otomatis menyesuaikan luminance gambar (teks putih pada album gelap, teks ink pada album terang).
   - Seekbar interaktif yang tidak melompat sebelum drag dilepas.

5. **Aksesibilitas & Standar Android:**
   - Target sentuh minimal 48 × 48 dp untuk semua elemen interaktif.
   - Label TalkBack terstruktur dalam bahasa Indonesia untuk pembaca layar.
   - Dukungan penuh *Edge-to-Edge* (`WindowInsets.statusBars`, `WindowInsets.navigationBars`, `WindowInsets.ime`).

---

## 🎨 Pemetaan 15 Frame Desain

| No | Frame ID | Nama Layar | Deskripsi Implementasi |
|---|---|---|---|
| 01 | `ql1aD` | Home | Header "senada", subtitle jumlah lagu, daftar "Baru ditambahkan" terurut `DATE_ADDED` menurun, Mini Player, Bottom Dock (tab Home aktif). |
| 02 | `jpVTq` | Semua Lagu | Header "Lagu", urutan judul A–Z dengan durasi di setiap baris lagu, Mini Player, Bottom Dock (tab Lagu aktif). |
| 03 | `uZvog` | Folder | Daftar direktori riil perangkat (Music, Download, dll.) beserta jumlah lagu dan catatan pembaruan otomatis. |
| 04 | `HqEJt` | Isi Folder | Navigasi kembali `< Folder`, judul folder, path penyimpanan, dan daftar lagu di dalam folder tersebut. |
| 05 | `s74Ah` | Cari Awal | Kolom pencarian dengan placeholder, pesan panduan "Temukan musikmu", Mini Player, Bottom Dock (tab Cari aktif). |
| 06 | `hCq8H` | Cari Hasil | Hasil pencarian instan berdasarkan judul atau nama penyanyi, indikator jumlah hasil ditemukan. |
| 07 | `conNP` | Cari Kosong | Tampilan ilustrasi dan pesan bahwa kueri pencarian tidak ditemukan di koleksi lokal. |
| 08 | `m3Oboj` | Pemutar Penuh | Layar pemutar penuh dengan artwork besar, seekbar responsif, kontrol putar 72 dp, acak/ulang, dan status deskriptif. |
| 09 | `xFxAC` | Izin Akses | Penjelasan kebutuhan akses media audio dan tombol "Izinkan akses" (`READ_MEDIA_AUDIO` / `READ_EXTERNAL_STORAGE`). |
| 10 | `JSg0K` | Izin Ditolak | Pesan saat izin ditolak dengan tombol coba lagi dan opsi "Buka pengaturan" sistem aplikasi. |
| 11 | `J87xH` | Pemindaian | Indikator progres membaca file musik lokal saat pemindaian pertama kali berjalan. |
| 12 | `tbVK2` | Koleksi Kosong | Tampilan saat perangkat belum memiliki file musik, navigasi tetap tersedia di bagian bawah. |
| 13 | `G5wqc` | File Hilang Snackbar | Pesan melayang `#30372D` jika file telah dihapus/tidak ditemukan di penyimpanan, otomatis memutar lagu berikutnya. |
| 14 | `u37L5` | Keyboard Aktif | Kolom cari terfokus dengan keyboard software terbuka, Floating Dock otomatis disembunyikan. |
| 18 | `UbMi6` | Pemutar Jeda / Gelap | Pemutar penuh dalam kondisi jeda dengan artwork bernuansa gelap dan tombol *Repeat One* aktif. |

---

## 🏗️ Struktur Proyek

```
app/src/main/java/com/senada/music/
├── MainActivity.kt                  # Entry point edge-to-edge
├── data/
│   ├── model/
│   │   └── Song.kt                  # Model Song, Folder, PlaybackState, RepeatMode
│   ├── fixtures/
│   │   └── MockMusicData.kt         # Data deterministik untuk preview & testing
│   ├── repository/
│   │   └── MusicRepository.kt       # MediaStore query & ContentObserver
│   └── artwork/
│       └── ArtworkLoader.kt         # LruCache bitmap & Palette dominant color extractor
├── playback/
│   ├── PlaybackService.kt           # MediaSessionService, ExoPlayer, Notification
│   └── MusicPlayerController.kt     # MediaController wrapper & StateFlow
└── ui/
    ├── MainViewModel.kt             # ViewModel orchestrator
    ├── SenadaApp.kt                 # Root Composable & state routing
    ├── theme/
    │   ├── Color.kt                 # Token warna Senada (Cream, Olive, Pink, Glass, dll.)
    │   ├── Type.kt                  # Konfigurasi tipografi & fallback SF Pro Display
    │   └── Theme.kt                 # MaterialTheme Senada
    ├── components/
    │   ├── CommonComponents.kt      # SenadaBackground, GlassSurface, SongRow, FolderRow, SenadaSeekBar
    │   ├── PlaybackControlComponents.kt # Tombol Play/Pause 72dp, Shuffle, Repeat, Prev, Next
    │   └── FloatingDock.kt          # MiniPlayer, BottomNavigationDock, FloatingDock
    ├── screens/
    │   ├── HomeScreen.kt            # Frame 01
    │   ├── SongsScreen.kt           # Frame 02
    │   ├── FoldersScreen.kt         # Frame 03
    │   ├── FolderDetailScreen.kt    # Frame 04
    │   ├── SearchScreen.kt          # Frame 05, 06, 07, 14
    │   ├── FullPlayerScreen.kt      # Frame 08, 18
    │   └── StateScreens.kt          # Frame 09, 10, 11, 12
    └── previews/
        └── SenadaPreviews.kt        # Compose Previews untuk semua 15 frame
```

---

## 🚀 Cara Build dan Menjalankan

### Kebutuhan Sistem
- **Java:** JDK 17 (Microsoft OpenJDK 17 atau setara)
- **Android SDK:** Platform 34 / 36, Build-Tools 34 / 36
- **Gradle:** 9.1.0 (menggunakan Gradle Wrapper bawaan)

### 1. Menjalankan Unit Test
Semua pengujian unit (sorting DATE_ADDED, pengelompokan folder, pencarian, siklus repeat, pemformatan durasi, status playback) dapat dijalankan dengan:

```bash
./gradlew test
```

*Status:* **13 test passed (100% success rate)**.

### 2. Mengompilasi Debug APK
Untuk membangun file APK Android:

```bash
./gradlew assembleDebug
```

File APK siap pakai akan tersedia di:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 3. Menginstal ke Perangkat Fisik atau Emulator
Jika perangkat Android atau emulator terhubung via ADB:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## ⚖️ Catatan Lisensi Tipografi
Berdasarkan handoff dokumen desain `SzO06`, font utama mengacu pada *SF Pro Display*. File tipografi diisolasi di [`ui/theme/Type.kt`](app/src/main/java/com/senada/music/ui/theme/Type.kt) dengan *fallback* aman ke font sistem Android sans-serif untuk menghormati ketentuan lisensi proprietary Apple.
