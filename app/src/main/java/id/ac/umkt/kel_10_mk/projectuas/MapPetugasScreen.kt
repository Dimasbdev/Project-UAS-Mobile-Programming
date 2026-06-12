package id.ac.umkt.kel_10_mk.projectuas

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
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
import id.ac.umkt.kel_10_mk.projectuas.models.ParkingArea
import id.ac.umkt.kel_10_mk.projectuas.ui.components.BottomNavItemData
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirBottomNavBar
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirTopBar
import id.ac.umkt.kel_10_mk.projectuas.ui.components.StatusBadge
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.MapStyle
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirAccent
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirBackground
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDanger
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDivider
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirMapSurface
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirSurface
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextPrimary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextSecondary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirWarning
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.SpaceGroteskFamily

@Composable
fun MapPetugasScreen(navController: NavHostController, parkingViewModel: ParkingViewModel) {
    val view = androidx.compose.ui.platform.LocalView.current
    val context = androidx.compose.ui.platform.LocalContext.current

    SideEffect {
        (context as? Activity)?.window?.run {
            statusBarColor = ParkirBackground.toArgb()
            WindowCompat.getInsetsController(this, view).isAppearanceLightStatusBars = false
        }
    }

    val parkingAreas = parkingViewModel.parkingAreas

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ParkirBackground,
        bottomBar = {
            ParkirBottomNavBar(
                navController = navController,
                items = listOf(
                    BottomNavItemData("Home", Icons.Default.Home, RouteDashboardPetugas),
                    BottomNavItemData("Map", Icons.Default.Map, RouteMapPetugas),
                    BottomNavItemData("History", Icons.Default.History, RouteHistoryPetugas),
                    BottomNavItemData("Profile", Icons.Default.AccountCircle, RouteProfilePetugas),
                ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ParkirBackground)
                .statusBarsPadding()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ParkirTopBar(showAction = false)

            Text(
                text = "Peta Parkir Kampus",
                color = ParkirTextPrimary,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
            )

            Text(
                text = "Pantau dan update area parkir",
                color = ParkirTextSecondary,
                fontSize = 13.sp,
            )

            GoogleMapContainer(parkingAreas = parkingAreas)

            LegendRow()

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                parkingAreas.forEach { area ->
                    MarkerRow(area = area)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GoogleMapContainer(parkingAreas: List<ParkingArea>) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val hasPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
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

    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = hasPermission
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
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
                val markerHue = when (area.status) {
                    ParkingStatus.SEPI -> BitmapDescriptorFactory.HUE_CYAN
                    ParkingStatus.SEDANG -> BitmapDescriptorFactory.HUE_ORANGE
                    ParkingStatus.PENUH -> BitmapDescriptorFactory.HUE_RED
                }
                Marker(
                    state = MarkerState(position = position),
                    title = area.name,
                    snippet = "${area.location} - Status: ${area.status.name}",
                    icon = BitmapDescriptorFactory.defaultMarker(markerHue)
                )
            }
        }
    }
}

private fun getLatLngForArea(areaId: String): LatLng {
    return when (areaId) {
        "parkiran_a" -> LatLng(-0.482065, 117.150493)
        "parkiran_b" -> LatLng(-0.482500, 117.150850)
        "parkiran_c" -> LatLng(-0.482900, 117.151150)
        "parkiran_d" -> LatLng(-0.481800, 117.151300)
        else -> LatLng(-0.4822, 117.1508)
    }
}

@Composable
private fun LegendRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LegendItem(label = "SEPI", color = ParkirAccent)
        LegendItem(label = "SEDANG", color = ParkirWarning)
        LegendItem(label = "PENUH", color = ParkirDanger)
    }
}

@Composable
private fun LegendItem(label: String, color: androidx.compose.ui.graphics.Color) {
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
            letterSpacing = 0.8.sp,
        )
    }
}

@Composable
private fun MarkerRow(area: ParkingArea) {
    val statusColor = when (area.status) {
        ParkingStatus.SEPI -> ParkirAccent
        ParkingStatus.SEDANG -> ParkirWarning
        ParkingStatus.PENUH -> ParkirDanger
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(14.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(statusColor.copy(alpha = 0.15f), CircleShape)
                    .border(BorderStroke(1.dp, statusColor.copy(alpha = 0.7f)), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(statusColor, CircleShape),
                )
            }
            Column {
                Text(
                    text = area.name,
                    color = ParkirTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = area.location,
                    color = ParkirTextSecondary,
                    fontSize = 12.sp,
                )
            }
        }
        StatusBadge(status = area.status, color = statusColor)
    }
}
