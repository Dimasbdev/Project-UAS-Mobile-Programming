package id.ac.umkt.kel_10_mk.projectuas.models

import id.ac.umkt.kel_10_mk.projectuas.ParkingStatus

data class ParkingArea(
    val name: String,
    val location: String,
    val status: ParkingStatus,
    val updatedMinutes: Int? = null,
)

data class ParkingMarker(
    val area: String,
    val location: String,
    val status: ParkingStatus,
)

data class ActivityLog(
    val area: String,
    val status: ParkingStatus,
    val timeLabel: String,
    val agoLabel: String,
    val officer: String? = null,
)
