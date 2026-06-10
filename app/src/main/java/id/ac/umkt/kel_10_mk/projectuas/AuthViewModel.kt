package id.ac.umkt.kel_10_mk.projectuas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.umkt.kel_10_mk.projectuas.models.User
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: User) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Idle)
        private set

    var isCheckingSession by mutableStateOf(true)
        private set

    val currentUser: User?
        get() = (uiState as? AuthUiState.Success)?.user

    private val _navigationEvent = Channel<String>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                val profile = repository.fetchCurrentUserProfile()
                if (profile != null) {
                    uiState = AuthUiState.Success(profile)
                    if (profile.role == "petugas") {
                        _navigationEvent.send(RouteDashboardPetugas)
                    } else {
                        _navigationEvent.send(RouteDashboardMahasiswa)
                    }
                }
            } catch (e: Exception) {
                // Ignore or handle
            } finally {
                isCheckingSession = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState = AuthUiState.Loading
            repository.login(email, password)
                .onSuccess { user ->
                    uiState = AuthUiState.Success(user)
                    if (user.role == "petugas") {
                        _navigationEvent.send(RouteDashboardPetugas)
                    } else {
                        _navigationEvent.send(RouteDashboardMahasiswa)
                    }
                }
                .onFailure { error ->
                    uiState = AuthUiState.Error(mapErrorToIndonesian(error))
                }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            uiState = AuthUiState.Loading
            repository.registerMahasiswa(name, email, password)
                .onSuccess {
                    uiState = AuthUiState.Idle
                    _toastMessage.emit("Registrasi berhasil! Silakan masuk.")
                    _navigationEvent.send(RouteLogin)
                }
                .onFailure { error ->
                    uiState = AuthUiState.Error(mapErrorToIndonesian(error))
                }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            uiState = AuthUiState.Loading
            repository.resetPassword(email)
                .onSuccess {
                    uiState = AuthUiState.Idle
                    _toastMessage.emit("Link reset password telah dikirim ke email Anda.")
                    _navigationEvent.send(RouteLogin)
                }
                .onFailure { error ->
                    uiState = AuthUiState.Error(mapErrorToIndonesian(error))
                }
        }
    }

    fun logout() {
        repository.logout()
        uiState = AuthUiState.Idle
        viewModelScope.launch {
            _navigationEvent.send(RouteLogin)
        }
    }

    private fun mapErrorToIndonesian(error: Throwable): String {
        val msg = error.message ?: ""
        return when {
            msg.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ||
            msg.contains("incorrect", ignoreCase = true) ||
            msg.contains("credential", ignoreCase = true) ||
            msg.contains("wrong-password", ignoreCase = true) ||
            msg.contains("user-not-found", ignoreCase = true) -> {
                "Email atau password salah."
            }
            msg.contains("email-already-in-use", ignoreCase = true) ||
            msg.contains("already exists", ignoreCase = true) -> {
                "Email sudah terdaftar."
            }
            msg.contains("network", ignoreCase = true) ||
            msg.contains("connection", ignoreCase = true) -> {
                "Koneksi internet bermasalah. Silakan coba lagi."
            }
            else -> error.localizedMessage ?: "Terjadi kesalahan sistem."
        }
    }
}
