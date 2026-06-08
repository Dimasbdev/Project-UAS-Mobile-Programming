package id.ac.umkt.kel_10_mk.projectuas

import android.app.Activity
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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import id.ac.umkt.kel_10_mk.projectuas.models.ParkingArea
import id.ac.umkt.kel_10_mk.projectuas.ui.components.BottomNavItemData
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirBottomNavBar
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirTopBar
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkingStatusBar
import id.ac.umkt.kel_10_mk.projectuas.ui.components.StatusBadge
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirAccent
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirBackground
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDanger
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDivider
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirSurface
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextPrimary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextSecondary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirWarning
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.SpaceGroteskFamily

@Composable
fun DashboardPetugasScreen(navController: NavHostController) {
    val view = androidx.compose.ui.platform.LocalView.current
    val context = androidx.compose.ui.platform.LocalContext.current

    SideEffect {
        (context as? Activity)?.window?.run {
            statusBarColor = ParkirBackground.toArgb()
            WindowCompat.getInsetsController(this, view).isAppearanceLightStatusBars = false
        }
    }

    val areas = remember {
        listOf(
            ParkingArea("Parkiran A", "Gedung A", ParkingStatus.SEDANG, 4),
            ParkingArea("Parkiran B", "Gedung B", ParkingStatus.PENUH, 1),
            ParkingArea("Parkiran C", "Gedung C", ParkingStatus.SEPI, 9),
            ParkingArea("Parkiran D", "Gedung D", ParkingStatus.SEDANG, 6),
        )
    }

    val summary = remember(areas) {
        areas.groupBy { it.status }.mapValues { it.value.size }
    }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ParkirBackground)
                .statusBarsPadding()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { ParkirTopBar(showAction = false) }
            item { PetugasHeader() }
            item { RoleChip() }
            item {
                StatusSummaryCard(
                    sepiCount = summary[ParkingStatus.SEPI] ?: 0,
                    sedangCount = summary[ParkingStatus.SEDANG] ?: 0,
                    penuhCount = summary[ParkingStatus.PENUH] ?: 0,
                )
            }
            item {
                Text(
                    text = "Kelola Kondisi Parkir",
                    color = ParkirTextPrimary,
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )
            }
            items(areas) { area ->
                PetugasAreaCard(area = area, navController = navController)
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun PetugasHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Selamat pagi, Petugas",
            color = ParkirTextPrimary,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
        )
        Text(
            text = "08:24 WITA - Rabu, 23 April 2026",
            color = ParkirTextSecondary,
            fontSize = 12.sp,
            letterSpacing = 1.1.sp,
        )
    }
}

@Composable
private fun RoleChip() {
    Row(
        modifier = Modifier
            .background(ParkirSurface, RoundedCornerShape(999.dp))
            .border(BorderStroke(1.dp, ParkirAccent.copy(alpha = 0.6f)), RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        androidx.compose.material3.Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = "Petugas",
            tint = ParkirAccent,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = "Petugas Parkir",
            color = ParkirAccent,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun StatusSummaryCard(
    sepiCount: Int,
    sedangCount: Int,
    penuhCount: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(18.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Status Parkir Kampus",
            color = ParkirTextPrimary,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatusPill(label = "$sepiCount Area Sepi", color = ParkirAccent)
            StatusPill(label = "$sedangCount Area Sedang", color = ParkirWarning)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatusPill(label = "$penuhCount Area Penuh", color = ParkirDanger)
        }
        Text(
            text = "Terakhir diperbarui 3 menit lalu",
            color = ParkirTextSecondary,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun StatusPill(label: String, color: Color) {
    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.4f)), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape),
        )
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun PetugasAreaCard(area: ParkingArea, navController: NavHostController) {
    val statusColor = when (area.status) {
        ParkingStatus.SEPI -> ParkirAccent
        ParkingStatus.SEDANG -> ParkirWarning
        ParkingStatus.PENUH -> ParkirDanger
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = area.name,
                    color = ParkirTextPrimary,
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
                Text(
                    text = area.location,
                    color = ParkirTextSecondary,
                    fontSize = 13.sp,
                )
            }
            StatusBadge(status = area.status, color = statusColor)
        }

        ParkingStatusBar(status = area.status, color = statusColor)

        Text(
            text = "Diperbarui ${area.updatedMinutes ?: 0} menit lalu",
            color = ParkirTextSecondary,
            fontSize = 12.sp,
        )

        OutlinedButton(
            onClick = { navController.navigate(RouteUpdateKondisiPetugas) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, ParkirDivider),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = ParkirTextPrimary,
            ),
        ) {
            Text(
                text = "Update Kondisi",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
