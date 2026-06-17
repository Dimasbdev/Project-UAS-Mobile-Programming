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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkingGoogleMap
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkingLegendRow
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import id.ac.umkt.kel_10_mk.projectuas.ui.components.getLatLngForArea
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

private fun calculateDistance(start: LatLng, end: LatLng): Float {
    val results = FloatArray(1)
    android.location.Location.distanceBetween(
        start.latitude, start.longitude,
        end.latitude, end.longitude,
        results
    )
    return results[0]
}

@Composable
fun MapMahasiswaScreen(navController: NavHostController, parkingViewModel: ParkingViewModel) {
    id.ac.umkt.kel_10_mk.projectuas.ui.components.SetDarkStatusBar()

    val context = LocalContext.current
    val parkingAreas by parkingViewModel.parkingAreas.collectAsStateWithLifecycle()
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    val hasPermission = remember {
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    DisposableEffect(hasPermission) {
        if (hasPermission) {
            val locationManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as? android.location.LocationManager
            if (locationManager != null) {
                val lastGps = try { locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER) } catch (e: SecurityException) { null }
                val lastNetwork = try { locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER) } catch (e: SecurityException) { null }
                val bestLast = lastGps ?: lastNetwork
                if (bestLast != null) {
                    userLocation = LatLng(bestLast.latitude, bestLast.longitude)
                }

                val listener = object : android.location.LocationListener {
                    override fun onLocationChanged(location: android.location.Location) {
                        userLocation = LatLng(location.latitude, location.longitude)
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                try {
                    locationManager.requestLocationUpdates(
                        android.location.LocationManager.GPS_PROVIDER,
                        5000L,
                        5f,
                        listener
                    )
                    locationManager.requestLocationUpdates(
                        android.location.LocationManager.NETWORK_PROVIDER,
                        5000L,
                        5f,
                        listener
                    )
                } catch (e: SecurityException) {
                    // Ignore
                }

                onDispose {
                    try {
                        locationManager.removeUpdates(listener)
                    } catch (e: Exception) {
                        // Ignore
                    }
                }
            } else {
                onDispose {}
            }
        } else {
            onDispose {}
        }
    }

    val areasWithDistance = remember(parkingAreas, userLocation) {
        parkingAreas.map { area ->
            val latLng = getLatLngForArea(area.id)
            val distance = userLocation?.let { calculateDistance(it, latLng) }
            area to distance
        }.sortedWith { a, b ->
            val distA = a.second
            val distB = b.second
            when {
                distA == null && distB == null -> 0
                distA == null -> 1
                distB == null -> -1
                else -> distA.compareTo(distB)
            }
        }
    }

    val recommendedAreaId = remember(areasWithDistance) {
        areasWithDistance
            .filter { (area, distance) -> distance != null && (area.status == ParkingStatus.SEPI || area.status == ParkingStatus.SEDANG) }
            .minByOrNull { it.second!! }
            ?.first?.id
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ParkirBackground,
        bottomBar = {
            ParkirBottomNavBar(
                navController = navController,
                items = id.ac.umkt.kel_10_mk.projectuas.ui.components.mahasiswaNavItems,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ParkirBackground)
                .statusBarsPadding()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                ParkirTopBar(
                    onActionClick = { navController.navigate(RouteNotificationsMahasiswa) }
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Peta Parkir Kampus",
                        color = ParkirTextPrimary,
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                    )

                    Text(
                        text = "Pantau area parkir secara real-time",
                        color = ParkirTextSecondary,
                        fontSize = 13.sp,
                    )
                }
            }

            item {
                ParkingGoogleMap(parkingAreas = parkingAreas)
            }

            item {
                ParkingLegendRow()
            }

            items(areasWithDistance) { (area, distance) ->
                MarkerRow(
                    area = area,
                    distance = distance,
                    isRecommended = area.id == recommendedAreaId
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}



@Composable
private fun MarkerRow(
    area: ParkingArea,
    distance: Float?,
    isRecommended: Boolean
) {
    val statusColor = when (area.status) {
        ParkingStatus.SEPI -> ParkirAccent
        ParkingStatus.SEDANG -> ParkirWarning
        ParkingStatus.PENUH -> ParkirDanger
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(14.dp))
            .border(
                BorderStroke(
                    1.dp,
                    if (isRecommended) ParkirAccent.copy(alpha = 0.5f) else ParkirDivider
                ),
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = area.name,
                        color = ParkirTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = SpaceGroteskFamily,
                    )
                    if (isRecommended) {
                        Box(
                            modifier = Modifier
                                .background(ParkirAccent.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .border(BorderStroke(1.dp, ParkirAccent.copy(alpha = 0.5f)), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "TERDEKAT & SEPI",
                                color = ParkirAccent,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                fontFamily = SpaceGroteskFamily,
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = area.location,
                        color = ParkirTextSecondary,
                        fontSize = 12.sp,
                    )
                    if (distance != null) {
                        Text(
                            text = "•",
                            color = ParkirTextSecondary,
                            fontSize = 12.sp,
                        )
                        Text(
                            text = if (distance < 1000) "${distance.toInt()} m" else String.format(java.util.Locale.US, "%.1f km", distance / 1000f),
                            color = ParkirAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
        StatusBadge(status = area.status, color = statusColor)
    }
}
