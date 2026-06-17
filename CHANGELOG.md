# Changelog

## 2026-06-17
- Update parking coordinates for campus buildings to new GPS coordinates.
- Rename campus buildings to: Gedung A, B, C (Parkiran A), Gedung G (Parkiran B), Gedung D (Parkiran C), Gedung F (Parkiran D).
- Implement real-time distance calculation from student's GPS location to campus parking areas on the student map screen.
- Sort parking area list under student map by distance (closest first).
- Add "TERDEKAT & SEPI" recommendation badge for the closest available (SEPI/SEDANG) parking area.

## 2026-05-18
- Refine dashboard petugas layout to match design reference (status summary and update actions).
- Tidy profile petugas layout with centered header text and clearer stat cards.
- Remove petugas dashboard notification action.

## 2026-05-17
- Wire mahasiswa location permission flow with system request and settings shortcut.
- Add permission status indicator on location permission screen.
- Add profile actions for notifications, location permission, and logout confirmation.
- Implement dashboard petugas UI layout with status summary and update cards.
- Add temporary login role buttons for mahasiswa and petugas.
- Add petugas history, profile, map, and update kondisi UI screens.

## 2026-05-05
- Add mahasiswa dashboard UI layout with status summary, area cards, and bottom navigation.
- Register mahasiswa dashboard route in navigation.
- Ignore .vscode/mcp.json in git.
- Add mahasiswa history and analytics UI screen.
- Add mahasiswa map placeholder screen with legend and markers.
