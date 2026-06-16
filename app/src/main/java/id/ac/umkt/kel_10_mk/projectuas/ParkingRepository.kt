package id.ac.umkt.kel_10_mk.projectuas

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import id.ac.umkt.kel_10_mk.projectuas.models.ParkingArea
import id.ac.umkt.kel_10_mk.projectuas.models.ActivityLog
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ParkingRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Cache SimpleDateFormat — tidak perlu dibuat ulang tiap listener call
    private val timeFormatter = SimpleDateFormat("HH:mm 'WITA'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("Asia/Makassar")
    }

    // Helper: mapping ID → nama area
    private fun areaName(id: String) = when (id) {
        "parkiran_a" -> "Parkiran A"
        "parkiran_b" -> "Parkiran B"
        "parkiran_c" -> "Parkiran C"
        "parkiran_d" -> "Parkiran D"
        else -> id.replaceFirstChar { it.uppercase() }
    }

    // Helper: mapping ID → lokasi gedung
    private fun areaLocation(id: String) = when (id) {
        "parkiran_a" -> "Gedung A, B, C"
        "parkiran_b" -> "Gedung G"
        "parkiran_c" -> "Gedung D"
        "parkiran_d" -> "Gedung F"
        else -> "Gedung Utama"
    }

    // Helper: parse satu dokumen Firestore → ParkingArea
    private fun documentToParkingArea(
        docId: String,
        name: String,
        location: String,
        statusStr: String,
        updatedAt: Timestamp?,
        updatedBy: String,
        notes: String,
    ): ParkingArea {
        val status = try {
            ParkingStatus.valueOf(statusStr)
        } catch (e: Exception) {
            ParkingStatus.SEPI
        }
        val minutesAgo = updatedAt?.let {
            val diffMs = System.currentTimeMillis() - it.toDate().time
            (diffMs / (1000 * 60)).toInt().coerceAtLeast(0)
        }
        val label = if (updatedAt == null) {
            "Belum pernah diperbarui"
        } else {
            formatRelativeTime(minutesAgo ?: 0)
        }
        return ParkingArea(
            name = areaName(docId),
            location = areaLocation(docId),
            status = status,
            updatedMinutes = minutesAgo ?: 0,
            updatedAgoLabel = label,
            id = docId,
            updatedAtMs = updatedAt?.toDate()?.time,
            updatedBy = updatedBy,
            notes = notes,
        )
    }

    private fun documentToActivityLog(doc: com.google.firebase.firestore.DocumentSnapshot): ActivityLog? {
        val areaId = doc.getString("areaId") ?: return null
        val areaName = doc.getString("areaName") ?: ""
        val statusStr = doc.getString("status") ?: "SEPI"
        val status = try {
            ParkingStatus.valueOf(statusStr)
        } catch (e: Exception) {
            ParkingStatus.SEPI
        }
        val timestamp = doc.getTimestamp("timestamp")
        val officerName = doc.getString("officerName") ?: ""

        val timeLabel = timestamp?.toDate()?.let { timeFormatter.format(it) } ?: ""
        val minutesAgo = timestamp?.let {
            val diffMs = System.currentTimeMillis() - it.toDate().time
            (diffMs / (1000 * 60)).toInt().coerceAtLeast(0)
        } ?: 0

        return ActivityLog(
            id = doc.id,
            areaId = areaId,
            area = areaName,
            status = status,
            timeLabel = timeLabel,
            agoLabel = formatRelativeTime(minutesAgo),
            timestampMs = timestamp?.toDate()?.time,
            officer = officerName,
        )
    }

    // Ambil data area parkir secara real-time menggunakan Flow
    fun getParkingAreas(): Flow<List<ParkingArea>> = callbackFlow {
        val listener = firestore.collection("parkir_areas")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val firestoreAreas = snapshot?.documents?.mapNotNull { doc ->
                    documentToParkingArea(
                        docId = doc.id,
                        name = doc.getString("name") ?: "",
                        location = doc.getString("location") ?: "",
                        statusStr = doc.getString("status") ?: "SEPI",
                        updatedAt = doc.getTimestamp("updatedAt"),
                        updatedBy = doc.getString("updatedBy") ?: "",
                        notes = doc.getString("notes") ?: "",
                    )
                } ?: emptyList()

                val firestoreMap = firestoreAreas.associateBy { it.id }

                val defaultTemplates = listOf(
                    documentToParkingArea("parkiran_a", "Parkiran A", "Gedung A, B, C", "SEPI", null, "", ""),
                    documentToParkingArea("parkiran_b", "Parkiran B", "Gedung G", "SEPI", null, "", ""),
                    documentToParkingArea("parkiran_c", "Parkiran C", "Gedung D", "SEPI", null, "", ""),
                    documentToParkingArea("parkiran_d", "Parkiran D", "Gedung F", "SEPI", null, "", "")
                )

                val merged = defaultTemplates.map { defaultArea ->
                    firestoreMap[defaultArea.id] ?: defaultArea
                }

                trySend(merged)
            }
        awaitClose { listener.remove() }
    }

    // Ambil detail satu area parkir (one-shot query)
    suspend fun getParkingArea(id: String): ParkingArea? {
        return try {
            val doc = firestore.collection("parkir_areas").document(id).get().await()
            if (!doc.exists()) {
                // Return default template if not created yet in database
                return documentToParkingArea(
                    docId = id,
                    name = areaName(id),
                    location = areaLocation(id),
                    statusStr = "SEPI",
                    updatedAt = null,
                    updatedBy = "",
                    notes = ""
                )
            }
            documentToParkingArea(
                docId = doc.id,
                name = doc.getString("name") ?: "",
                location = doc.getString("location") ?: "",
                statusStr = doc.getString("status") ?: "SEPI",
                updatedAt = doc.getTimestamp("updatedAt"),
                updatedBy = doc.getString("updatedBy") ?: "",
                notes = doc.getString("notes") ?: "",
            )
        } catch (e: Exception) {
            null
        }
    }

    // Update status parkir dan catatan oleh petugas
    suspend fun updateParkingArea(
        id: String,
        status: ParkingStatus,
        notes: String,
        officerName: String,
    ): Result<Unit> {
        val name = areaName(id)
        val location = areaLocation(id)
        return try {
            val now = Timestamp.now()
            val updates = mapOf(
                "name" to name,
                "location" to location,
                "status" to status.name,
                "notes" to notes,
                "updatedAt" to now,
                "updatedBy" to officerName,
            )
            firestore.collection("parkir_areas").document(id).update(updates).await()

            val logData = mapOf(
                "areaId" to id,
                "areaName" to name,
                "status" to status.name,
                "timestamp" to now,
                "officerName" to officerName,
            )
            firestore.collection("parkir_logs").add(logData).await()

            Result.success(Unit)
        } catch (e: Exception) {
            // Jika dokumen belum ada di Firestore, buat dokumen baru (inisialisasi otomatis)
            try {
                val now = Timestamp.now()
                val newData = mapOf(
                    "name" to name,
                    "location" to location,
                    "status" to status.name,
                    "notes" to notes,
                    "updatedAt" to now,
                    "updatedBy" to officerName,
                )
                firestore.collection("parkir_areas").document(id).set(newData).await()

                val logData = mapOf(
                    "areaId" to id,
                    "areaName" to name,
                    "status" to status.name,
                    "timestamp" to now,
                    "officerName" to officerName,
                )
                firestore.collection("parkir_logs").add(logData).await()

                Result.success(Unit)
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }

    // Ambil log aktivitas parkir
    fun getActivityLogs(limit: Int = 50): Flow<List<ActivityLog>> = callbackFlow {
        val listener = firestore.collection("parkir_logs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val logs = snapshot?.documents?.mapNotNull { doc ->
                    documentToActivityLog(doc)
                } ?: emptyList()

                trySend(logs)
            }
        awaitClose { listener.remove() }
    }

    // Ambil semua log aktivitas setelah timestamp tertentu untuk kebutuhan analitik/chart
    fun getLogsAfter(cutoff: Timestamp): Flow<List<ActivityLog>> = callbackFlow {
        val listener = firestore.collection("parkir_logs")
            .whereGreaterThanOrEqualTo("timestamp", cutoff)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val logs = snapshot?.documents?.mapNotNull { doc ->
                    documentToActivityLog(doc)
                } ?: emptyList()

                val sortedLogs = logs.sortedByDescending { it.timestampMs ?: 0L }
                trySend(sortedLogs)
            }
        awaitClose { listener.remove() }
    }

    private fun formatRelativeTime(minutesAgo: Int): String {
        return when {
            minutesAgo < 1 -> "baru saja"
            minutesAgo < 60 -> "$minutesAgo menit lalu"
            minutesAgo < 1440 -> "${minutesAgo / 60} jam lalu"
            minutesAgo < 43200 -> "${minutesAgo / 1440} hari lalu"
            else -> "${minutesAgo / 43200} bulan lalu"
        }
    }

    suspend fun generateDummyData() {
        val areas = listOf(
            "parkiran_a" to "Parkiran A",
            "parkiran_b" to "Parkiran B",
            "parkiran_c" to "Parkiran C",
            "parkiran_d" to "Parkiran D",
        )
        val statuses = listOf(ParkingStatus.SEPI, ParkingStatus.SEDANG, ParkingStatus.PENUH)
        val oneDayMs = 24 * 60 * 60 * 1000L
        val now = System.currentTimeMillis()
        val logsCollection = firestore.collection("parkir_logs")

        // Gunakan WriteBatch untuk efisiensi — maksimal 500 operasi per batch
        val batch = firestore.batch()
        for (i in 0..100) {
            val randomTime = now - (Math.random() * 7 * oneDayMs).toLong()
            val area = areas.random()
            val status = statuses.random()
            val logData = mapOf(
                "areaId" to area.first,
                "areaName" to area.second,
                "status" to status.name,
                "timestamp" to Timestamp(java.util.Date(randomTime)),
                "officerName" to "Simulasi Bot",
            )
            batch.set(logsCollection.document(), logData)
        }
        batch.commit().await()
    }
}
