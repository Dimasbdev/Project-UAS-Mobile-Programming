package id.ac.umkt.kel_10_mk.projectuas

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umkt.kel_10_mk.projectuas.models.ParkingArea
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ParkingRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Ambil data area parkir secara real-time menggunakan Flow
    fun getParkingAreas(): Flow<List<ParkingArea>> = callbackFlow {
        val listener = firestore.collection("parkir_areas")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val areas = snapshot?.documents?.mapNotNull { doc ->
                    val name = doc.getString("name") ?: ""
                    val location = doc.getString("location") ?: ""
                    val statusStr = doc.getString("status") ?: "SEPI"
                    val status = try {
                        ParkingStatus.valueOf(statusStr)
                    } catch (e: Exception) {
                        ParkingStatus.SEPI
                    }
                    val updatedAt = doc.getTimestamp("updatedAt")
                    val updatedBy = doc.getString("updatedBy") ?: ""
                    val notes = doc.getString("notes") ?: ""
                    
                    // Hitung selisih menit secara dinamis
                    val minutesAgo = updatedAt?.let {
                        val diffMs = System.currentTimeMillis() - it.toDate().time
                        (diffMs / (1000 * 60)).toInt().coerceAtLeast(0)
                    } ?: 0

                    ParkingArea(
                        name = name,
                        location = location,
                        status = status,
                        updatedMinutes = minutesAgo,
                        id = doc.id,
                        updatedAt = updatedAt,
                        updatedBy = updatedBy,
                        notes = notes
                    )
                } ?: emptyList()
                
                // Urutkan berdasarkan ID agar urutannya konsisten (a, b, c, d)
                trySend(areas.sortedBy { it.id })
            }
        awaitClose { listener.remove() }
    }

    // Ambil detail satu area parkir (one-shot query)
    suspend fun getParkingArea(id: String): ParkingArea? {
        return try {
            val doc = firestore.collection("parkir_areas").document(id).get().await()
            if (!doc.exists()) return null
            
            val name = doc.getString("name") ?: ""
            val location = doc.getString("location") ?: ""
            val statusStr = doc.getString("status") ?: "SEPI"
            val status = try {
                ParkingStatus.valueOf(statusStr)
            } catch (e: Exception) {
                ParkingStatus.SEPI
            }
            val updatedAt = doc.getTimestamp("updatedAt")
            val updatedBy = doc.getString("updatedBy") ?: ""
            val notes = doc.getString("notes") ?: ""
            
            val minutesAgo = updatedAt?.let {
                val diffMs = System.currentTimeMillis() - it.toDate().time
                (diffMs / (1000 * 60)).toInt().coerceAtLeast(0)
            } ?: 0

            ParkingArea(
                name = name,
                location = location,
                status = status,
                updatedMinutes = minutesAgo,
                id = doc.id,
                updatedAt = updatedAt,
                updatedBy = updatedBy,
                notes = notes
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
        officerName: String
    ): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to status.name,
                "notes" to notes,
                "updatedAt" to Timestamp.now(),
                "updatedBy" to officerName
            )
            firestore.collection("parkir_areas").document(id).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            // Jika dokumen belum ada di Firestore, buat dokumen baru (inisialisasi otomatis)
            try {
                val name = when (id) {
                    "parkiran_a" -> "Parkiran A"
                    "parkiran_b" -> "Parkiran B"
                    "parkiran_c" -> "Parkiran C"
                    "parkiran_d" -> "Parkiran D"
                    else -> id.replaceFirstChar { it.uppercase() }
                }
                val location = when (id) {
                    "parkiran_a" -> "Gedung A"
                    "parkiran_b" -> "Gedung B"
                    "parkiran_c" -> "Gedung C"
                    "parkiran_d" -> "Gedung D"
                    else -> "Gedung Utama"
                }
                val newData = mapOf(
                    "name" to name,
                    "location" to location,
                    "status" to status.name,
                    "notes" to notes,
                    "updatedAt" to Timestamp.now(),
                    "updatedBy" to officerName
                )
                firestore.collection("parkir_areas").document(id).set(newData).await()
                Result.success(Unit)
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }
}
