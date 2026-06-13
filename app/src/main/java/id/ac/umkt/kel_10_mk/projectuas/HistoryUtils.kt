package id.ac.umkt.kel_10_mk.projectuas

import id.ac.umkt.kel_10_mk.projectuas.models.ActivityLog
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ChartDataPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Memfilter log berdasarkan indeks filter:
 * - 0 = Hari Ini (sejak tengah malam)
 * - 1 = 7 Hari Terakhir
 */
fun filterLogs(logs: List<ActivityLog>, filterIndex: Int): List<ActivityLog> {
    val cal = Calendar.getInstance()
    if (filterIndex == 0) {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    } else {
        cal.add(Calendar.DAY_OF_YEAR, -7)
    }
    val cutoff = cal.timeInMillis
    return logs.filter { log ->
        val ts = log.timestamp?.toDate()?.time ?: return@filter false
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

    return if (isToday) {
        val cal = Calendar.getInstance()
        val grouped = logs.groupBy { log ->
            log.timestamp?.toDate()?.let { cal.time = it }
            cal.get(Calendar.HOUR_OF_DAY)
        }
        grouped.entries
            .sortedBy { it.key }
            .takeLast(7)
            .map { (hour, hourLogs) ->
                val avg = hourLogs.map { it.status.ordinal }.average()
                val status = ParkingStatus.entries.getOrElse(avg.toInt().coerceIn(0, 2)) { ParkingStatus.SEPI }
                ChartDataPoint(String.format("%02d", hour), status)
            }
    } else {
        val fmt = SimpleDateFormat("EEE", Locale("id", "ID"))
        val cal = Calendar.getInstance()
        val grouped = logs.groupBy { log ->
            log.timestamp?.toDate()?.let { cal.time = it }
            // Key = tahun * 1000 + hari_dalam_tahun agar tahun berbeda tidak bentrok
            cal.get(Calendar.YEAR) * 1000 + cal.get(Calendar.DAY_OF_YEAR)
        }
        grouped.entries
            .sortedBy { it.key }
            .takeLast(7)
            .map { (_, dayLogs) ->
                val avg = dayLogs.map { it.status.ordinal }.average()
                val status = ParkingStatus.entries.getOrElse(avg.toInt().coerceIn(0, 2)) { ParkingStatus.SEPI }
                val label = dayLogs.first().timestamp?.toDate()?.let { fmt.format(it) } ?: "-"
                ChartDataPoint(label, status)
            }
    }
}
