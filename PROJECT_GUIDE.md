# ParkirUMKT — Project Guide for AI Agent

## Overview
ParkirUMKT is an Android mobile application built with Kotlin and Jetpack Compose.
The app helps UMKT university students monitor campus parking area conditions in real-time.
Parking conditions are updated by designated parking officers (petugas), while students can only view the information.

---

## Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material3)
- **Font:** Space Grotesk (loaded from res/font/)
- **Backend:** Firebase Firestore (real-time database)
- **Authentication:** Firebase Authentication
- **Maps:** Google Maps SDK for Android
- **IDE:** IntelliJ IDEA / Android Studio
- **Min SDK:** 24
- **Target SDK:** 35
- **JDK:** 17

---

## Design System
| Token | Value |
|---|---|
| Background | #0A0E1A |
| Card Surface | #1A1F2E |
| Primary Accent (Teal) | #00D4AA |
| Warning (Amber) | #F59E0B |
| Danger (Red) | #EF4444 |
| Text Primary | #F9FAFB |
| Text Secondary | #9CA3AF |
| Input Border | #2D3748 |
| Bottom Nav Background | #0D1321 |

---

## User Roles
There are 2 roles in this app:

### 1. Mahasiswa (Student)
- Login using university email ending with `.ac.id`
- Can only VIEW parking conditions (read only)
- No ability to update parking status
- Bottom nav: Home, Map, History, Profile

### 2. Petugas (Parking Officer)
- Account created manually in Firebase Console (not through register screen)
- Can UPDATE parking conditions for all areas
- Bottom nav: Home, Map, History, Profile (same as mahasiswa)
- Difference is in screen CONTENT, not navigation structure


---

## App Screens

### Auth Flow
| Screen | File | Description |
|---|---|---|
| Login | `LoginScreen.kt` | Single login screen for both roles |
| Register | `RegisterScreen.kt` | Only for mahasiswa, validates .ac.id email |
| Lupa Password | `ForgotPasswordScreen.kt` | Email reset via Firebase |

### Mahasiswa Screens
| Screen | File | Description |
|---|---|---|
| Dashboard | `DashboardMahasiswaScreen.kt` | View-only parking status |
| Map | `MapMahasiswaScreen.kt` | Google Maps with colored pins |
| History | `HistoryMahasiswaScreen.kt` | Charts and activity log |
| Profile | `ProfileMahasiswaScreen.kt` | Account info and settings |

### Petugas Screens
| Screen | File | Description |
|---|---|---|
| Dashboard | `DashboardPetugasScreen.kt` | Parking status + Update button per area |
| Update Kondisi | `UpdateKondisiScreen.kt` | Form to update parking status |
| Map | `MapPetugasScreen.kt` | Google Maps with colored pins |
| History | `HistoryPetugasScreen.kt` | Charts and activity log with officer label |
| Profile | `ProfilePetugasScreen.kt` | Officer account info |

---

## Parking Data Structure
Parking status uses 3 levels only — NO percentage numbers:
- **SEPI** (green #00D4AA) — Few vehicles, plenty of space
- **SEDANG** (amber #F59E0B) — Moderately busy
- **PENUH** (red #EF4444) — Full, no space available

Visual indicator: 3-segment bar (NOT percentage bar)
- SEPI → 1 of 3 segments filled (green)
- SEDANG → 2 of 3 segments filled (amber)
- PENUH → 3 of 3 segments filled (red)

---

## Firebase Firestore Structure
```
parkir_areas/
  ├── parkiran_a/
  │     ├── name: "Parkiran A"
  │     ├── location: "Gedung A, B, C"
  │     ├── status: "SEPI" | "SEDANG" | "PENUH"
  │     ├── updatedAt: timestamp (WITA)
  │     ├── updatedBy: "Petugas Parkir"
  │     └── notes: "optional notes"
  ├── parkiran_b/
  ├── parkiran_c/
  └── parkiran_d/

users/
  ├── {uid}/
  │     ├── name: string
  │     ├── email: string
  │     ├── role: "mahasiswa" | "petugas"
  │     └── createdAt: timestamp
```

---

## Parking Areas
| ID | Name | Location |
|---|---|----------|
| parkiran_a | Parkiran A | Gedung A, B, C |
| parkiran_b | Parkiran B | Gedung G |
| parkiran_c | Parkiran C | Gedung D |
| parkiran_d | Parkiran D | Gedung F |

---

## Bottom Navigation
Both roles use the same 4-item bottom nav:
1. 🏠 Home
2. 🗺️ Map
3. 🕐 History
4. 👤 Profile

**NO floating center button.**
Active item color: #00D4AA
Inactive item color: #6B7280

---

## Font Setup
Space Grotesk is loaded locally from `res/font/`:
```
res/font/
  ├── space_grotesk_regular.ttf
  ├── space_grotesk_medium.ttf
  ├── space_grotesk_bold.ttf
  └── space_grotesk_extrabold.ttf
```

FontFamily declaration in `Type.kt`:
```kotlin
val SpaceGroteskFamily = FontFamily(
    Font(R.font.space_grotesk_regular, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
    Font(R.font.space_grotesk_extrabold, FontWeight.ExtraBold)
)
```

---

## Timezone
All timestamps displayed in **WITA (UTC+8)**.
Format: `HH:mm WITA · EEEE, dd MMMM yyyy`
Example: `08:24 WITA · Rabu, 23 April 2026`

---

## Current Progress
- [x] Login Screen UI
- [ ] Register Screen UI
- [ ] Forgot Password Screen UI
- [ ] Dashboard Mahasiswa UI
- [ ] Dashboard Petugas UI
- [ ] Update Kondisi UI
- [ ] Map Screen UI
- [ ] History Screen UI
- [ ] Profile Screen UI
- [ ] Firebase Authentication integration
- [ ] Firestore real-time data integration
- [ ] Google Maps integration
- [ ] Role-based navigation logic
- [ ] End-to-end testing

---

## Important Rules for AI Agent
1. **NEVER use percentage numbers** for parking capacity (no 70%, 65%)
2. **NEVER add floating center button** to bottom navigation
3. **Always use Space Grotesk** font family
4. **Always show time in WITA** timezone
5. **Parking status ONLY:** SEPI, SEDANG, or PENUH
6. **App name display:** "PARKIRUMKT" — all uppercase, monospace, teal color
7. **Students cannot update** parking conditions — read only
8. **Only petugas** can update parking conditions
9. **Bottom nav is always 4 items** — no exceptions
10. **Dark mode only** — no light mode support