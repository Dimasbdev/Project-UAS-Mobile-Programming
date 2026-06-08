package id.ac.umkt.kel_10_mk.projectuas.models

import com.google.firebase.Timestamp

data class User(
    val name: String = "",
    val email: String = "",
    val role: String = "mahasiswa", // "mahasiswa" atau "petugas"
    val createdAt: Timestamp? = null
)
