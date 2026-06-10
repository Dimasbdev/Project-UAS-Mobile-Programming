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
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirBarChart
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ChartDataPoint
import id.ac.umkt.kel_10_mk.projectuas.ui.components.statusLabel
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirAccent
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirBackground
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDanger
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDivider
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirStatCard
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirSurface
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextPrimary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextSecondary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirWarning
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.SpaceGroteskFamily

@Composable
fun HistoryPetugasScreen(navController: NavHostController) {
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
            ActivityLog("Parkiran B", ParkingStatus.SEDANG, "08:24 WITA", "3 mnt lalu"),
            ActivityLog("Parkiran C", ParkingStatus.PENUH, "08:15 WITA", "12 mnt lalu"),
            ActivityLog("Parkiran A", ParkingStatus.SEPI, "08:10 WITA", "17 mnt lalu"),
            ActivityLog("Parkiran D", ParkingStatus.SEPI, "08:05 WITA", "22 mnt lalu"),
            ActivityLog("Parkiran B", ParkingStatus.SEDANG, "07:58 WITA", "29 mnt lalu"),
        )
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

            item { TitleSection() }

            item {
                FilterTabs(
                    selectedIndex = selectedFilter,
                    onSelectedChange = { selectedFilter = it },
                )
            }

            item { AnalyticsCard() }

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

            item { Spacer(modifier = Modifier.height(12.dp)) }
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
    val backgroundColor = if (isSelected) ParkirAccent else androidx.compose.ui.graphics.Color.Transparent
    val textColor = if (isSelected) androidx.compose.ui.graphics.Color(0xFF0F1A18) else ParkirTextSecondary

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun AnalyticsCard() {
    val chartData = listOf(
        ChartDataPoint("07", ParkingStatus.SEPI),
        ChartDataPoint("08", ParkingStatus.SEDANG),
        ChartDataPoint("09", ParkingStatus.PENUH),
        ChartDataPoint("10", ParkingStatus.PENUH),
        ChartDataPoint("11", ParkingStatus.SEDANG),
        ChartDataPoint("12", ParkingStatus.SEPI),
        ChartDataPoint("13", ParkingStatus.SEPI)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(18.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
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
                contentDescription = "Lainnya",
                tint = ParkirAccent,
                modifier = Modifier.size(18.dp),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(ParkirStatCard, RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(16.dp))
                .padding(16.dp),
        ) {
            ParkirBarChart(data = chartData)
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
            .background(ParkirSurface, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
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
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = log.timeLabel,
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
