package id.ac.umkt.kel_10_mk.projectuas.models

import id.ac.umkt.kel_10_mk.projectuas.ParkingStatus

import com.google.firebase.Timestamp

data class ParkingArea(
    val name: String = "",
    val location: String = "",
    val status: ParkingStatus = ParkingStatus.SEPI,
    val updatedMinutes: Int? = null,
    val updatedAgoLabel: String = "",
    val id: String = "",
    val updatedAt: Timestamp? = null,
    val updatedBy: String = "",
    val notes: String = ""
)

data class ParkingMarker(
    val area: String,
    val location: String,
    val status: ParkingStatus,
)

data class ActivityLog(
    val id: String = "",
    val areaId: String = "",
    val area: String = "",
    val status: ParkingStatus = ParkingStatus.SEPI,
    val timeLabel: String = "",
    val agoLabel: String = "",
    val timestamp: Timestamp? = null,
    val officer: String? = null,
)
