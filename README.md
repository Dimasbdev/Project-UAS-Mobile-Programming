# PARKIRUMKT

Aplikasi mobile Android berbasis Jetpack Compose untuk memantau ketersediaan area parkir secara real-time di lingkungan Universitas Muhammadiyah Kalimantan Timur (UMKT). Aplikasi ini memfasilitasi mahasiswa untuk melihat kondisi parkir secara instan dan memudahkan petugas untuk memperbarui status parkir.

## 📱 Fitur Utama

Aplikasi ini memiliki 2 peran (*Role*) pengguna dengan fungsionalitas yang disesuaikan:

### 1. Mahasiswa (Read-Only)
- Login aman menggunakan email universitas (`.ac.id`).
- Memantau kondisi tiap area parkir secara *real-time*.
- Melihat lokasi area parkir secara visual melalui peta (Google Maps).
- Memeriksa riwayat pembaruan status parkir.

### 2. Petugas Parkir (Update)
- Memperbarui kondisi area parkir secara *real-time* dengan sekali klik.
- Menerapkan **3 Status Parkir** sederhana (Tanpa Persentase):
  - 🟢 **SEPI** (Ruang parkir tersedia luas)
  - 🟡 **SEDANG** (Cukup ramai)
  - 🔴 **PENUH** (Tidak ada ruang tersedia)
- Semua riwayat dan waktu pembaruan otomatis dicatat dalam zona waktu **WITA (UTC+8)**.

## 🛠️ Teknologi & Arsitektur

- **Bahasa Pemrograman:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Tema Desain:** *Dark Mode Only* (Khusus Tema Gelap)
- **Tipografi:** Space Grotesk
- **Backend & Database:** Firebase Firestore (Real-time Database)
- **Autentikasi:** Firebase Authentication
- **Layanan Peta:** Google Maps SDK for Android
- **SDK & Environment:** Min SDK 24, Target SDK 35, JDK 17

## 📂 Struktur Data Firebase (Firestore)

Aplikasi ini menggunakan Cloud Firestore dengan struktur NoSQL:

- `parkir_areas/` : Koleksi yang menyimpan data tiap area parkir (contoh: `parkiran_a`, `parkiran_b`), berisi status terkini (`SEPI` | `SEDANG` | `PENUH`), waktu update (`updatedAt`), dan identitas petugas yang melakukan update (`updatedBy`).
- `users/` : Koleksi yang menyimpan informasi pengguna terdaftar, termasuk pembagian role otorisasi (`mahasiswa` atau `petugas`).

## 🚀 Cara Menjalankan Project Secara Lokal

1. *Clone* repository ini ke komputer Anda:
   ```bash
   git clone https://github.com/Dimasbdev/Project-UAS-Mobile-Programming.git
   ```
2. Buka project menggunakan **Android Studio** versi terbaru.
3. Hubungkan project dengan Firebase milik Anda sendiri (Tambahkan file `google-services.json` ke dalam direktori `app/`).
4. Lakukan *Sync Project with Gradle Files* untuk mengunduh semua dependensi.
5. Jalankan aplikasi (Run) pada Android Emulator atau perangkat Android asli (Minimal Android 7.0 / API 24).

---
*Project UAS - Mata Kuliah Pemrograman Perangkat Bergerak*
