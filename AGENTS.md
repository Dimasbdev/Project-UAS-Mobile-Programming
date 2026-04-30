# ParkirUMKT Agent Notes

## Ringkas
- Platform: Android (Kotlin, Jetpack Compose Material3), min SDK 24, target SDK 35, JDK 17.
- Dark mode only.
- Font wajib: Space Grotesk (res/font).
- Nama app tampil: PARKIRUMKT (uppercase, monospace feel, warna teal).

## Design System
- Background: #0A0E1A
- Card Surface: #1A1F2E
- Primary Accent (Teal): #00D4AA
- Warning (Amber): #F59E0B
- Danger (Red): #EF4444
- Text Primary: #F9FAFB
- Text Secondary: #9CA3AF
- Input Border: #2D3748
- Bottom Nav Background: #0D1321
- Bottom nav: 4 item (Home, Map, History, Profile), tanpa floating center button.

## Aturan Fungsional
- Status parkir hanya: SEPI, SEDANG, PENUH (tanpa persentase).
- Mahasiswa hanya view, tidak bisa update.
- Petugas bisa update kondisi parkir.
- Waktu selalu WITA (UTC+8), format: HH:mm WITA - EEEE, dd MMMM yyyy.

## Data (Firestore)
- parkir_areas/{parkiran_a|b|c|d}: name, location, status, updatedAt, updatedBy, notes.
- users/{uid}: name, email, role (mahasiswa|petugas), createdAt.
