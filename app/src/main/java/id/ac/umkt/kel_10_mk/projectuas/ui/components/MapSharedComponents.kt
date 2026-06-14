package id.ac.umkt.kel_10_mk.projectuas.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import id.ac.umkt.kel_10_mk.projectuas.MapStyle
import id.ac.umkt.kel_10_mk.projectuas.ParkingStatus
import id.ac.umkt.kel_10_mk.projectuas.models.ParkingArea
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.*

val parkingAreaLatLng = mapOf(
    "parkiran_a" to LatLng(-0.482065, 117.150493),
    "parkiran_b" to LatLng(-0.482500, 117.150850),
    "parkiran_c" to LatLng(-0.482900, 117.151150),
    "parkiran_d" to LatLng(-0.481800, 117.151300)
)

fun getLatLngForArea(areaId: String): LatLng {
    return parkingAreaLatLng[areaId] ?: LatLng(-0.4822, 117.1508)
}

@Composable
fun rememberMarkerIcon(status: ParkingStatus): BitmapDescriptor {
    return remember(status) {
        val hue = when (status) {
            ParkingStatus.SEPI -> BitmapDescriptorFactory.HUE_CYAN
            ParkingStatus.SEDANG -> BitmapDescriptorFactory.HUE_ORANGE
            ParkingStatus.PENUH -> BitmapDescriptorFactory.HUE_RED
        }
        BitmapDescriptorFactory.defaultMarker(hue)
    }
}

@Composable
fun ParkingGoogleMap(parkingAreas: List<ParkingArea>) {
    val context = LocalContext.current
    val hasPermission = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    val umktCenter = LatLng(-0.4822, 117.1508)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(umktCenter, 16.5f)
    }

    val mapProperties = remember(hasPermission) {
        MapProperties(
            mapStyleOptions = MapStyleOptions(MapStyle.json),
            isMyLocationEnabled = hasPermission
        )
    }

    val mapUiSettings = remember(hasPermission) {
        MapUiSettings(
            zoomControlsEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = hasPermission
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(16.dp))
            .background(ParkirMapSurface)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings
        ) {
            parkingAreas.forEach { area ->
                val position = getLatLngForArea(area.id)
                val markerIcon = rememberMarkerIcon(area.status)
                val markerState = remember(area.id) { MarkerState(position = position) }
                Marker(
                    state = markerState,
                    title = area.name,
                    snippet = "${area.location} - Status: ${area.status.name}",
                    icon = markerIcon
                )
            }
        }
    }
}

@Composable
fun ParkingLegendRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ParkingLegendItem(label = "SEPI", color = ParkirAccent)
        ParkingLegendItem(label = "SEDANG", color = ParkirWarning)
        ParkingLegendItem(label = "PENUH", color = ParkirDanger)
    }
}

@Composable
private fun ParkingLegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape),
        )
        Text(
            text = label,
            color = ParkirTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
        )
    }
}
