package id.ac.umkt.kel_10_mk.projectuas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirBottomNavBar
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirTopBar
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirBackground
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextPrimary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextSecondary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.SpaceGroteskFamily
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirAccent
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDivider

@Composable
fun HistoryPetugasScreen(navController: NavHostController, viewModel: ParkingViewModel) {
    id.ac.umkt.kel_10_mk.projectuas.ui.components.SetDarkStatusBar()
    val context = LocalContext.current

    var selectedFilter by remember { mutableIntStateOf(0) }
    val logs by viewModel.activityLogs.collectAsStateWithLifecycle()
    val analyticsLogs by viewModel.analyticsLogs.collectAsStateWithLifecycle()
    val logsLimit by viewModel.logsLimit.collectAsStateWithLifecycle()

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

    val exportLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val writer = outputStream.bufferedWriter()
                    writer.write("Waktu,Area,Status,Petugas\n")
                    val logsToExport = filterLogs(analyticsLogs, selectedFilter)
                    logsToExport.forEach { log ->
                        writer.write("${log.timeLabel},${log.area},${log.status.name},${log.officer ?: ""}\n")
                    }
                    writer.flush()
                }
                android.widget.Toast.makeText(context, "Berhasil export data", android.widget.Toast.LENGTH_SHORT).show()
            } catch (_: Exception) {
                android.widget.Toast.makeText(context, "Gagal export data", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
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
            item { 
                ParkirTopBar(
                    showAction = true,
                    actionIcon = Icons.Default.Share,
                    onActionClick = {
                        exportLauncher.launch("export_histori_parkir.csv")
                    }
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
                    androidx.compose.foundation.layout.Box(
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
