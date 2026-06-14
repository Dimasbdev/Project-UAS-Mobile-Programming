package id.ac.umkt.kel_10_mk.projectuas.models

import androidx.compose.runtime.Immutable
import id.ac.umkt.kel_10_mk.projectuas.ParkingStatus

@Immutable
data class ParkingArea(
    val name: String = "",
    val location: String = "",
    val status: ParkingStatus = ParkingStatus.SEPI,
    val updatedMinutes: Int? = null,
    val updatedAgoLabel: String = "",
    val id: String = "",
    val updatedAtMs: Long? = null,
    val updatedBy: String = "",
    val notes: String = ""
)

data class ParkingMarker(
    val area: String,
    val location: String,
    val status: ParkingStatus,
)

@Immutable
data class ActivityLog(
    val id: String = "",
    val areaId: String = "",
    val area: String = "",
    val status: ParkingStatus = ParkingStatus.SEPI,
    val timeLabel: String = "",
    val agoLabel: String = "",
    val timestampMs: Long? = null,
    val officer: String? = null,
)
