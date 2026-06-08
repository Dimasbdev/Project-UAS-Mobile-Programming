package id.ac.umkt.kel_10_mk.projectuas

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umkt.kel_10_mk.projectuas.models.User
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getCurrentUser() = auth.currentUser

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("UID tidak ditemukan")
            
            val userDoc = firestore.collection("users").document(uid).get().await()
            val user = userDoc.toObject(User::class.java)
                ?: run {
                    auth.signOut()
                    throw Exception("Profil user tidak ditemukan di database")
                }
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerMahasiswa(name: String, email: String, password: String): Result<Unit> {
        return try {
            if (!email.trim().endsWith(".ac.id")) {
                throw Exception("Gunakan email kampus (.ac.id)")
            }

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Gagal membuat user")

            val newUser = User(
                name = name,
                email = email,
                role = "mahasiswa",
                createdAt = Timestamp.now()
            )
            firestore.collection("users").document(uid).set(newUser).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchCurrentUserProfile(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val userDoc = firestore.collection("users").document(uid).get().await()
            userDoc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        auth.signOut()
    }
}
