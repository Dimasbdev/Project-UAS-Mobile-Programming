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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import id.ac.umkt.kel_10_mk.projectuas.models.ActivityLog
import id.ac.umkt.kel_10_mk.projectuas.ui.components.BottomNavItemData
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirBottomNavBar
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirTopBar
import id.ac.umkt.kel_10_mk.projectuas.ui.components.statusLabel
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
fun HistoryMahasiswaScreen(navController: NavHostController) {
    val view = LocalView.current
    val context = LocalContext.current

    SideEffect {
        (context as? Activity)?.window?.run {
            statusBarColor = ParkirBackground.toArgb()
            WindowCompat.getInsetsController(this, view).isAppearanceLightStatusBars = false
        }
    }

    var selectedFilter by remember { mutableStateOf(0) }
    val logs = remember {
        listOf(
            ActivityLog("Parkiran B", ParkingStatus.SEDANG, "08:24 WITA", "3 mnt lalu", "petugas A"),
            ActivityLog("Parkiran D", ParkingStatus.PENUH, "08:15 WITA", "12 mnt lalu", "petugas B"),
            ActivityLog("Parkiran C", ParkingStatus.SEPI, "07:50 WITA", "37 mnt lalu", "petugas C"),
            ActivityLog("Parkiran A", ParkingStatus.SEPI, "07:30 WITA", "1 j lalu", "petugas D"),
            ActivityLog("Parkiran D", ParkingStatus.SEDANG, "07:15 WITA", "1.2 j lalu", "petugas E"),
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ParkirBackground,
        bottomBar = {
            ParkirBottomNavBar(
                navController = navController,
                items = listOf(
                    BottomNavItemData("Home", Icons.Default.Home, RouteDashboardMahasiswa),
                    BottomNavItemData("Map", Icons.Default.Map, RouteMapMahasiswa),
                    BottomNavItemData("History", Icons.Default.History, RouteHistoryMahasiswa),
                    BottomNavItemData("Profile", Icons.Default.AccountCircle, RouteProfileMahasiswa),
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
            item {
                ParkirTopBar()
            }

            item {
                TitleSection()
            }

            item {
                FilterTabs(
                    selectedIndex = selectedFilter,
                    onSelectedChange = { selectedFilter = it },
                )
            }

            item {
                AnalyticsCard()
            }

            item {
                Text(
                    text = "Log Aktivitas Terbaru",
                    color = ParkirTextPrimary,
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )
            }

            items(logs) { log ->
                ActivityLogCard(log = log)
            }

            item {
                ViewAllLogsButton()
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun TitleSection() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Riwayat & Analitik",
            color = ParkirTextPrimary,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
        )
        Text(
            text = "Ringkasan kepadatan parkir harian kampus",
            color = ParkirTextSecondary,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun FilterTabs(
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(999.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(999.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        FilterTab(
            label = "Hari Ini",
            isSelected = selectedIndex == 0,
            onClick = { onSelectedChange(0) },
            modifier = Modifier.weight(1f),
        )
        FilterTab(
            label = "Minggu Ini",
            isSelected = selectedIndex == 1,
            onClick = { onSelectedChange(1) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun FilterTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSelected) ParkirAccent else Color.Transparent
    val textColor = if (isSelected) Color(0xFF0F1A18) else ParkirTextSecondary

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun AnalyticsCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Kepadatan Parkir per Jam",
                color = ParkirTextPrimary,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "Opsi",
                tint = ParkirAccent,
            )
        }

        ChartPlaceholder()
    }
}

@Composable
private fun ChartPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirMapSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ParkirDivider.copy(alpha = 0.7f)),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            listOf("07", "08", "09", "10", "11", "12", "13").forEach { label ->
                Text(
                    text = label,
                    color = ParkirTextSecondary,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun ActivityLogCard(log: ActivityLog) {
    val statusColor = when (log.status) {
        ParkingStatus.SEPI -> ParkirAccent
        ParkingStatus.SEDANG -> ParkirWarning
        ParkingStatus.PENUH -> ParkirDanger
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(statusColor, CircleShape),
                )
                Column {
                    Text(
                        text = "${log.area} - ${statusLabel(log.status)}",
                        color = ParkirTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val timeLabel = if (log.officer.isNullOrBlank()) {
                        log.timeLabel
                    } else {
                        "${log.timeLabel} - oleh ${log.officer}"
                    }
                    Text(
                        text = timeLabel,
                        color = ParkirTextSecondary,
                        fontSize = 12.sp,
                    )
                }
            }
            Text(
                text = log.agoLabel,
                color = ParkirTextSecondary,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun ViewAllLogsButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(14.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(14.dp))
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Lihat Semua Log",
            color = ParkirAccent,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )
    }
}
