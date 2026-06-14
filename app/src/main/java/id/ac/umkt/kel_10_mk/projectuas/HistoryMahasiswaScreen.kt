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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ChartDataPoint
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirBarChart
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirBottomNavBar
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirTopBar
import id.ac.umkt.kel_10_mk.projectuas.ui.components.statusLabel
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
fun HistoryMahasiswaScreen(navController: NavHostController, viewModel: ParkingViewModel) {
    id.ac.umkt.kel_10_mk.projectuas.ui.components.SetDarkStatusBar()

    var selectedFilter by remember { mutableIntStateOf(0) }
    val logs by viewModel.activityLogs.collectAsStateWithLifecycle()
    val analyticsLogs by viewModel.analyticsLogs.collectAsStateWithLifecycle()
    val logsLimit by viewModel.logsLimit.collectAsStateWithLifecycle()

    // derivedStateOf memastikan rekomputasi hanya terjadi ketika logs atau filter benar-benar berubah
    val filteredLogs by remember(logs, selectedFilter) {
        derivedStateOf { filterLogs(logs, selectedFilter) }
    }
    val groupedLogs by remember(filteredLogs) {
        derivedStateOf { 
            val limited = if (selectedFilter == 1) filteredLogs.take(100) else filteredLogs
            groupLogsByDate(limited) 
        }
    }
    val chartData by remember(analyticsLogs, selectedFilter) {
        derivedStateOf {
            val chartFilteredLogs = filterLogs(analyticsLogs, selectedFilter)
            buildChartData(chartFilteredLogs, isToday = selectedFilter == 0)
        }
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

            item { HistoryTitleSection() }

            item {
                HistoryFilterTabs(
                    selectedIndex = selectedFilter,
                    onSelectedChange = { selectedFilter = it },
                )
            }

            item {
                HistoryAnalyticsCard(
                    chartData = chartData,
                    isToday = selectedFilter == 0,
                    isEmpty = chartData.isEmpty(),
                )
            }

            item {
                Text(
                    text = if (filteredLogs.isEmpty()) "Belum ada log aktivitas" else "Log Aktivitas",
                    color = if (filteredLogs.isEmpty()) ParkirTextSecondary else ParkirTextPrimary,
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                )
            }

            groupedLogs.forEach { (dateHeader, logsForDate) ->
                item {
                    Text(
                        text = dateHeader,
                        color = ParkirTextSecondary,
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                items(logsForDate, key = { it.id }) { log ->
                    HistoryActivityLogCard(log = log)
                }
            }

            if (logs.size >= logsLimit && filteredLogs.isNotEmpty()) {
                item {
                    androidx.compose.runtime.LaunchedEffect(logsLimit) {
                        // Tidak ada action karena loadMoreLogs() dipanggil dari tombol
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.OutlinedButton(
                            onClick = { viewModel.loadMoreLogs() },
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                contentColor = ParkirAccent
                            ),
                            border = BorderStroke(1.dp, ParkirDivider)
                        ) {
                            Text("Muat Lebih Banyak")
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

// ─── Shared private composables ─────────────────────────────────────────────

@Composable
internal fun HistoryTitleSection() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Riwayat & Analitik",
            color = ParkirTextPrimary,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
        )
        Text(
            text = "Ringkasan kepadatan parkir kampus",
            color = ParkirTextSecondary,
            fontSize = 13.sp,
        )
    }
}

@Composable
internal fun HistoryFilterTabs(selectedIndex: Int, onSelectedChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(999.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(999.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        listOf("Hari Ini", "7 Hari Terakhir").forEachIndexed { index, label ->
            val isSelected = selectedIndex == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isSelected) ParkirAccent else Color.Transparent)
                    .clickable { onSelectedChange(index) }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color(0xFF0A0E1A) else ParkirTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
internal fun HistoryAnalyticsCard(
    chartData: List<ChartDataPoint>,
    isToday: Boolean,
    isEmpty: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = if (isToday) "Kepadatan per Jam · Hari Ini" else "Rata-rata per Hari · 7 Hari Terakhir",
            color = ParkirTextPrimary,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ParkirMapSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isEmpty || chartData.isEmpty()) {
                Text(
                    text = "Belum ada data untuk rentang ini",
                    color = ParkirTextSecondary,
                    fontSize = 13.sp,
                )
            } else {
                ParkirBarChart(
                    data = chartData,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
internal fun HistoryActivityLogCard(log: ActivityLog) {
    val statusColor = when (log.status) {
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
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(statusColor, CircleShape),
            )
            Column {
                Text(
                    text = "${log.area} · ${statusLabel(log.status)}",
                    color = ParkirTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (log.officer.isNullOrBlank()) log.timeLabel
                           else "${log.timeLabel} · ${log.officer}",
                    color = ParkirTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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
