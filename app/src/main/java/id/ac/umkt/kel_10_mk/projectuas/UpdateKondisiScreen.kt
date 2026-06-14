package id.ac.umkt.kel_10_mk.projectuas

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import id.ac.umkt.kel_10_mk.projectuas.ui.components.StatusBadge
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirAccent
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirBackground
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDanger
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDivider
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirInputBorder
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirSurface
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextPrimary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextSecondary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirWarning
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.SpaceGroteskFamily
import id.ac.umkt.kel_10_mk.projectuas.models.ParkingArea

private data class StatusOption(
    val status: ParkingStatus,
    val title: String,
    val description: String,
    val color: androidx.compose.ui.graphics.Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

@Composable
fun UpdateKondisiScreen(
    navController: NavHostController,
    areaId: String,
    parkingViewModel: ParkingViewModel,
    authViewModel: AuthViewModel
) {
    val view = androidx.compose.ui.platform.LocalView.current
    val context = androidx.compose.ui.platform.LocalContext.current

    androidx.compose.runtime.LaunchedEffect(areaId) {
        parkingViewModel.loadParkingArea(areaId)
    }

    val currentArea by parkingViewModel.currentArea.collectAsStateWithLifecycle()
    val isLoading by parkingViewModel.isLoading.collectAsStateWithLifecycle()

    var selectedStatus by remember { mutableStateOf(ParkingStatus.SEDANG) }
    var note by remember { mutableStateOf("") }

    // Set initial values once data is loaded
    androidx.compose.runtime.LaunchedEffect(currentArea) {
        currentArea?.let {
            selectedStatus = it.status
            note = it.notes
        }
    }

    // Handle toast and navigation events
    androidx.compose.runtime.LaunchedEffect(Unit) {
        parkingViewModel.uiEvent.collect { event ->
            if (event == "UPDATE_SUCCESS") {
                android.widget.Toast.makeText(context, "Kondisi parkir berhasil diperbarui!", android.widget.Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else if (event == "UPDATE_FAILURE") {
                android.widget.Toast.makeText(context, "Gagal memperbarui kondisi parkir.", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    SideEffect {
        (context as? Activity)?.window?.run {
            statusBarColor = ParkirBackground.toArgb()
            WindowCompat.getInsetsController(this, view).isAppearanceLightStatusBars = false
        }
    }

    val options = remember {
        listOf(
            StatusOption(
                status = ParkingStatus.SEPI,
                title = "Sepi",
                description = "Masih banyak tempat tersedia",
                color = ParkirAccent,
                icon = Icons.Default.Check,
            ),
            StatusOption(
                status = ParkingStatus.SEDANG,
                title = "Sedang",
                description = "Mulai ramai",
                color = ParkirWarning,
                icon = Icons.Default.Remove,
            ),
            StatusOption(
                status = ParkingStatus.PENUH,
                title = "Penuh",
                description = "Tidak ada tempat tersisa",
                color = ParkirDanger,
                icon = Icons.Default.Close,
            ),
        )
    }

    val textFieldColors = remember {
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ParkirAccent,
            unfocusedBorderColor = ParkirDivider,
            focusedTextColor = ParkirTextPrimary,
            unfocusedTextColor = ParkirTextPrimary,
            focusedLabelColor = ParkirAccent,
            unfocusedLabelColor = ParkirTextSecondary,
            cursorColor = ParkirAccent,
            focusedContainerColor = ParkirBackground,
            unfocusedContainerColor = ParkirBackground,
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ParkirBackground,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ParkirBackground)
                .statusBarsPadding()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            TopBar(onBack = { navController.popBackStack() })

            val area = currentArea
            if (area != null) {
                AreaCard(area = area, status = selectedStatus)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(color = ParkirAccent)
                }
            }

            Text(
                text = "Perbarui Kondisi Saat Ini",
                color = ParkirTextPrimary,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { option ->
                    StatusOptionCard(
                        option = option,
                        isSelected = option.status == selectedStatus,
                        onClick = { selectedStatus = option.status },
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Catatan Tambahan (opsional)",
                    color = ParkirTextSecondary,
                    fontSize = 12.sp,
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text(text = "Tulis catatan di sini...") },
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors,
                )
            }

            SaveButton(
                onClick = {
                    val officerName = authViewModel.currentUser?.name ?: "Petugas"
                    parkingViewModel.updateParkingArea(areaId, selectedStatus, note, officerName)
                },
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(ParkirSurface, CircleShape)
                .border(BorderStroke(1.dp, ParkirDivider), CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                tint = ParkirTextPrimary,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = "Update Kondisi Parkir",
            color = ParkirTextPrimary,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
        )
    }
}

@Composable
private fun AreaCard(area: ParkingArea, status: ParkingStatus) {
    val statusColor = when (status) {
        ParkingStatus.SEPI -> ParkirAccent
        ParkingStatus.SEDANG -> ParkirWarning
        ParkingStatus.PENUH -> ParkirDanger
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Lokasi",
                tint = ParkirAccent,
                modifier = Modifier.size(20.dp),
            )
            Column {
                Text(
                    text = area.name.ifEmpty { "Parkiran" },
                    color = ParkirTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
                Text(
                    text = area.location.ifEmpty { "Gedung" },
                    color = ParkirTextSecondary,
                    fontSize = 13.sp,
                )
            }
        }
        StatusBadge(status = status, color = statusColor)
    }
}

@Composable
private fun StatusOptionCard(
    option: StatusOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (isSelected) option.color else ParkirDivider
    val surfaceColor = if (isSelected) option.color.copy(alpha = 0.1f) else ParkirSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceColor, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(16.dp))
            .padding(16.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(option.color.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.title,
                    tint = option.color,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column {
                Text(
                    text = option.title,
                    color = ParkirTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
                Text(
                    text = option.description,
                    color = ParkirTextSecondary,
                    fontSize = 13.sp,
                )
            }
        }
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(androidx.compose.ui.graphics.Color.Transparent, CircleShape)
                .border(BorderStroke(2.dp, borderColor), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(option.color, CircleShape),
                )
            }
        }
    }
}

@Composable
private fun SaveButton(onClick: () -> Unit, isLoading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(if (isLoading) ParkirAccent.copy(alpha = 0.5f) else ParkirAccent, RoundedCornerShape(999.dp))
            .clickable(enabled = !isLoading) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(
                color = ParkirBackground,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = "Simpan Perubahan",
                color = ParkirBackground,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
        }
    }
}
