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
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        // Hasilkan 7 jam terakhir hingga jam sekarang (misal: jika sekarang jam 18, maka 12, 13, 14, 15, 16, 17, 18)
        val last7Hours = (0..6).map { offset ->
            (currentHour - 6 + offset + 24) % 24
        }
        val grouped = logs.groupBy { log ->
            log.timestamp?.toDate()?.let { cal.time = it }
            cal.get(Calendar.HOUR_OF_DAY)
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
        val fmt = SimpleDateFormat("EEE", Locale("id", "ID"))
        val cal = Calendar.getInstance()
        // Hasilkan 7 hari terakhir hingga hari ini
        val last7Days = (0..6).map { offset ->
            val dayCal = Calendar.getInstance()
            dayCal.add(Calendar.DAY_OF_YEAR, -6 + offset)
            val key = dayCal.get(Calendar.YEAR) * 1000 + dayCal.get(Calendar.DAY_OF_YEAR)
            val label = fmt.format(dayCal.time)
            key to label
        }
        val grouped = logs.groupBy { log ->
            log.timestamp?.toDate()?.let { cal.time = it }
            cal.get(Calendar.YEAR) * 1000 + cal.get(Calendar.DAY_OF_YEAR)
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
    val fmt = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    val calToday = Calendar.getInstance()
    val calYesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    
    // Pastikan log sudah berurutan dari yang terbaru
    val sortedLogs = logs.sortedByDescending { it.timestamp?.toDate()?.time ?: 0L }
    
    // Gunakan LinkedHashMap (default dari groupBy) untuk menjaga urutan hari dari yang terbaru
    return sortedLogs.groupBy { log ->
        val logDate = log.timestamp?.toDate() ?: return@groupBy "Waktu Tidak Diketahui"
        val logCal = Calendar.getInstance().apply { time = logDate }
        
        val isToday = logCal.get(Calendar.YEAR) == calToday.get(Calendar.YEAR) &&
                      logCal.get(Calendar.DAY_OF_YEAR) == calToday.get(Calendar.DAY_OF_YEAR)
                      
        val isYesterday = logCal.get(Calendar.YEAR) == calYesterday.get(Calendar.YEAR) &&
                          logCal.get(Calendar.DAY_OF_YEAR) == calYesterday.get(Calendar.DAY_OF_YEAR)
                          
        when {
            isToday -> "Hari Ini - ${fmt.format(logDate)}"
            isYesterday -> "Kemarin - ${fmt.format(logDate)}"
            else -> fmt.format(logDate)
        }
    }
}
