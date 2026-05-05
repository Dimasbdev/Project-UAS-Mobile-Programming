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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val BackgroundColor = Color(0xFF0A0E1A)
private val CardColor = Color(0xFF1A1F2E)
private val AccentColor = Color(0xFF00D4AA)
private val WarningColor = Color(0xFFF59E0B)
private val DangerColor = Color(0xFFEF4444)
private val PrimaryTextColor = Color(0xFFF9FAFB)
private val SecondaryTextColor = Color(0xFF9CA3AF)
private val DividerColor = Color(0xFF2D3748)
private val BottomNavColor = Color(0xFF0D1321)
private val InactiveNavColor = Color(0xFF6B7280)

private val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk_regular, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.spacegrotesk_semibold, FontWeight.SemiBold),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

private enum class ParkingStatus {
    SEPI,
    SEDANG,
    PENUH,
}

private data class ParkingArea(
    val name: String,
    val location: String,
    val status: ParkingStatus,
    val updatedMinutes: Int,
)

@Composable
fun DashboardMahasiswaScreen() {
    val view = LocalView.current
    val context = LocalContext.current

    SideEffect {
        (context as? Activity)?.window?.run {
            statusBarColor = BackgroundColor.toArgb()
            WindowCompat.getInsetsController(this, view).isAppearanceLightStatusBars = false
        }
    }

    val areas = remember {
        listOf(
            ParkingArea("Parkiran A", "Gedung A", ParkingStatus.SEPI, 5),
            ParkingArea("Parkiran B", "Gedung B", ParkingStatus.SEDANG, 8),
            ParkingArea("Parkiran C", "Gedung C", ParkingStatus.PENUH, 1),
            ParkingArea("Parkiran D", "Gedung D", ParkingStatus.SEPI, 12),
        )
    }

    val summary = remember(areas) {
        areas.groupBy { it.status }.mapValues { it.value.size }
    }

    var selectedNav by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundColor,
        bottomBar = {
            BottomNavBar(selectedIndex = selectedNav, onSelectedChange = { selectedNav = it })
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .statusBarsPadding()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                TopAppBar()
            }

            item {
                GreetingSection()
            }

            item {
                StatusSummaryCard(
                    sepiCount = summary[ParkingStatus.SEPI] ?: 0,
                    sedangCount = summary[ParkingStatus.SEDANG] ?: 0,
                    penuhCount = summary[ParkingStatus.PENUH] ?: 0,
                )
            }

            items(areas) { area ->
                ParkingAreaCard(area = area)
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun TopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "PARKIRUMKT",
            color = AccentColor,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            letterSpacing = 1.4.sp,
        )
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(CardColor, CircleShape)
                .border(BorderStroke(1.dp, DividerColor), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifikasi",
                tint = AccentColor,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun GreetingSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Selamat pagi, John!",
            color = PrimaryTextColor,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
        )
        Text(
            text = "08:24 WITA - RABU, 23 APRIL 2025",
            color = AccentColor,
            fontSize = 12.sp,
            letterSpacing = 1.1.sp,
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
            .background(CardColor, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, DividerColor), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Status Parkir Kampus",
            color = PrimaryTextColor,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatusChip(label = "$sepiCount Area Sepi", color = AccentColor)
            StatusChip(label = "$sedangCount Area Sedang", color = WarningColor)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatusChip(label = "$penuhCount Area Penuh", color = DangerColor)
        }

        Text(
            text = "Terakhir diperbarui 3 menit lalu",
            color = SecondaryTextColor,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun StatusChip(label: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFF262B3A))
            .border(BorderStroke(1.dp, DividerColor), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
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
            color = PrimaryTextColor,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ParkingAreaCard(area: ParkingArea) {
    val statusColor = when (area.status) {
        ParkingStatus.SEPI -> AccentColor
        ParkingStatus.SEDANG -> WarningColor
        ParkingStatus.PENUH -> DangerColor
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardColor, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, DividerColor), RoundedCornerShape(16.dp))
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
                    color = PrimaryTextColor,
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
                Text(
                    text = area.location,
                    color = SecondaryTextColor,
                    fontSize = 13.sp,
                )
            }
            StatusBadge(status = area.status, color = statusColor)
        }

        ParkingStatusBar(status = area.status, color = statusColor)

        Text(
            text = "Diperbarui ${area.updatedMinutes} menit lalu",
            color = SecondaryTextColor,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun StatusBadge(status: ParkingStatus, color: Color) {
    val label = when (status) {
        ParkingStatus.SEPI -> "SEPI"
        ParkingStatus.SEDANG -> "SEDANG"
        ParkingStatus.PENUH -> "PENUH"
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.15f))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.7f)), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape),
        )
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
        )
    }
}

@Composable
private fun ParkingStatusBar(status: ParkingStatus, color: Color) {
    val filledSegments = when (status) {
        ParkingStatus.SEPI -> 1
        ParkingStatus.SEDANG -> 2
        ParkingStatus.PENUH -> 3
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(3) { index ->
            val segmentColor = if (index < filledSegments) color else DividerColor
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(999.dp))
                    .background(segmentColor),
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BottomNavColor)
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomNavItem(
            label = "Home",
            icon = Icons.Default.Home,
            isSelected = selectedIndex == 0,
            onClick = { onSelectedChange(0) },
        )
        BottomNavItem(
            label = "Map",
            icon = Icons.Default.Map,
            isSelected = selectedIndex == 1,
            onClick = { onSelectedChange(1) },
        )
        BottomNavItem(
            label = "History",
            icon = Icons.Default.History,
            isSelected = selectedIndex == 2,
            onClick = { onSelectedChange(2) },
        )
        BottomNavItem(
            label = "Profile",
            icon = Icons.Default.AccountCircle,
            isSelected = selectedIndex == 3,
            onClick = { onSelectedChange(3) },
        )
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (isSelected) AccentColor else InactiveNavColor
    Column(
        modifier = Modifier
            .widthIn(min = 48.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label.uppercase(),
            color = tint,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp,
        )
    }
}
