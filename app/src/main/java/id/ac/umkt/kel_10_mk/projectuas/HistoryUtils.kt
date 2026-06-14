package id.ac.umkt.kel_10_mk.projectuas

import id.ac.umkt.kel_10_mk.projectuas.models.ActivityLog
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ChartDataPoint
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private val ZONE_ID = ZoneId.of("Asia/Makassar")

/**
 * Memfilter log berdasarkan indeks filter:
 * - 0 = Hari Ini (sejak tengah malam)
 * - 1 = 7 Hari Terakhir
 */
fun filterLogs(logs: List<ActivityLog>, filterIndex: Int): List<ActivityLog> {
    val now = ZonedDateTime.now(ZONE_ID)
    val cutoff = if (filterIndex == 0) {
        now.truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli()
    } else {
        now.minusDays(7).toInstant().toEpochMilli()
    }
    return logs.filter { log ->
        val ts = log.timestampMs ?: return@filter false
        ts >= cutoff
    }
}

/**
 * Membuat data chart dari list log yang sudah difilter:
 * - isToday=true  → aggregasi per jam (label "08", "09", dst)
 * - isToday=false → aggregasi per hari (label "Sen", "Sel", dst)
 * Mengembalikan list kosong jika tidak ada data.
 */
fun buildChartData(logs: List<ActivityLog>, isToday: Boolean): List<ChartDataPoint> {
    if (logs.isEmpty()) return emptyList()

    val now = ZonedDateTime.now(ZONE_ID)

    return if (isToday) {
        val currentHour = now.hour
        val last7Hours = (0..6).map { offset ->
            (currentHour - 6 + offset + 24) % 24
        }
        val grouped = logs.groupBy { log ->
            val ms = log.timestampMs ?: return@groupBy -1
            Instant.ofEpochMilli(ms).atZone(ZONE_ID).hour
        }
        last7Hours.map { hour ->
            val hourLogs = grouped[hour]
            val status = if (hourLogs.isNullOrEmpty()) {
                ParkingStatus.SEPI
            } else {
                val avg = hourLogs.map { it.status.ordinal }.average()
                ParkingStatus.entries.getOrElse(avg.toInt().coerceIn(0, 2)) { ParkingStatus.SEPI }
            }
            ChartDataPoint(String.format("%02d", hour), status)
        }
    } else {
        val fmt = DateTimeFormatter.ofPattern("EEE", Locale("id", "ID"))
        val last7Days = (0..6).map { offset ->
            val dayDate = now.minusDays((6 - offset).toLong())
            val key = dayDate.year * 1000 + dayDate.dayOfYear
            val label = dayDate.format(fmt)
            key to label
        }
        val grouped = logs.groupBy { log ->
            val ms = log.timestampMs ?: return@groupBy -1
            val zdt = Instant.ofEpochMilli(ms).atZone(ZONE_ID)
            zdt.year * 1000 + zdt.dayOfYear
        }
        last7Days.map { (key, label) ->
            val dayLogs = grouped[key]
            val status = if (dayLogs.isNullOrEmpty()) {
                ParkingStatus.SEPI
            } else {
                val avg = dayLogs.map { it.status.ordinal }.average()
                ParkingStatus.entries.getOrElse(avg.toInt().coerceIn(0, 2)) { ParkingStatus.SEPI }
            }
            ChartDataPoint(label, status)
        }
    }
}

/**
 * Mengelompokkan log aktivitas berdasarkan tanggal untuk keperluan header pada UI.
 * Format: "Hari Ini - 14 Juni 2026", "Kemarin - 13 Juni 2026", dll.
 */
fun groupLogsByDate(logs: List<ActivityLog>): Map<String, List<ActivityLog>> {
    val fmt = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    val now = ZonedDateTime.now(ZONE_ID)
    val todayKey = now.year * 1000 + now.dayOfYear
    val yesterday = now.minusDays(1)
    val yesterdayKey = yesterday.year * 1000 + yesterday.dayOfYear
    
    val sortedLogs = logs.sortedByDescending { it.timestampMs ?: 0L }
    
    return sortedLogs.groupBy { log ->
        val ms = log.timestampMs ?: return@groupBy "Waktu Tidak Diketahui"
        val zdt = Instant.ofEpochMilli(ms).atZone(ZONE_ID)
        val key = zdt.year * 1000 + zdt.dayOfYear
        
        when (key) {
            todayKey -> "Hari Ini - ${zdt.format(fmt)}"
            yesterdayKey -> "Kemarin - ${zdt.format(fmt)}"
            else -> zdt.format(fmt)
        }
    }
}
