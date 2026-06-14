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
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkingStatusSummaryCard
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
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
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
fun DashboardPetugasScreen(
    navController: NavHostController,
    parkingViewModel: ParkingViewModel,
    officerName: String
) {
    id.ac.umkt.kel_10_mk.projectuas.ui.components.SetDarkStatusBar()

    val areas by parkingViewModel.parkingAreas.collectAsStateWithLifecycle()
    val summary = remember(areas) {
        areas.groupBy { it.status }.mapValues { it.value.size }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ParkirBackground,
        bottomBar = {
            ParkirBottomNavBar(
                navController = navController,
                items = id.ac.umkt.kel_10_mk.projectuas.ui.components.petugasNavItems,
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
            item { PetugasHeader(officerName) }
            item { RoleChip() }
            item {
                ParkingStatusSummaryCard(areas = areas)
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
private fun PetugasHeader(officerName: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Selamat pagi, $officerName",
            color = ParkirTextPrimary,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
        )
        val witaTime = rememberWitaTime()
        Text(
            text = witaTime,
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
            text = "Diperbarui ${area.updatedAgoLabel.ifEmpty { "baru saja" }}",
            color = ParkirTextSecondary,
            fontSize = 12.sp,
        )

        OutlinedButton(
            onClick = { navController.navigate("update-kondisi-petugas/${area.id}") },
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
