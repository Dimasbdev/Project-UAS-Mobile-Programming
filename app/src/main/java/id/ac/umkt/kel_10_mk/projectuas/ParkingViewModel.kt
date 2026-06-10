package id.ac.umkt.kel_10_mk.projectuas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.umkt.kel_10_mk.projectuas.models.ParkingArea
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ParkingViewModel(
    private val repository: ParkingRepository = ParkingRepository()
) : ViewModel() {

    var parkingAreas by mutableStateOf<List<ParkingArea>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var currentArea by mutableStateOf<ParkingArea?>(null)
        private set

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    var activityLogs by mutableStateOf<List<id.ac.umkt.kel_10_mk.projectuas.models.ActivityLog>>(emptyList())
        private set

    init {
        observeParkingAreas()
        observeActivityLogs()
    }

    private fun observeActivityLogs() {
        viewModelScope.launch {
            repository.getActivityLogs()
                .catch {
                    // Fallback
                    activityLogs = emptyList()
                }
                .collect { logs ->
                    activityLogs = logs
                }
        }
    }

    private fun observeParkingAreas() {
        viewModelScope.launch {
            repository.getParkingAreas()
                .catch {
                    // Jika gagal (misal koneksi bermasalah), gunakan data mock sebagai fallback
                    useMockFallback()
                }
                .collect { areas ->
                    if (areas.isEmpty()) {
                        // Jika Firestore kosong, inisialisasi dengan mock/template default
                        useMockFallback()
                    } else {
                        parkingAreas = areas
                    }
                }
        }
    }

    private fun useMockFallback() {
        parkingAreas = listOf(
            ParkingArea("Parkiran A", "Gedung A", ParkingStatus.SEPI, 0, "parkiran_a"),
            ParkingArea("Parkiran B", "Gedung B", ParkingStatus.SEDANG, 0, "parkiran_b"),
            ParkingArea("Parkiran C", "Gedung C", ParkingStatus.PENUH, 0, "parkiran_c"),
            ParkingArea("Parkiran D", "Gedung D", ParkingStatus.SEPI, 0, "parkiran_d")
        )
    }

    fun loadParkingArea(id: String) {
        viewModelScope.launch {
            isLoading = true
            currentArea = repository.getParkingArea(id)
            isLoading = false
        }
    }

    fun updateParkingArea(id: String, status: ParkingStatus, notes: String, officerName: String) {
        viewModelScope.launch {
            isLoading = true
            repository.updateParkingArea(id, status, notes, officerName)
                .onSuccess {
                    _uiEvent.emit("UPDATE_SUCCESS")
                }
                .onFailure {
                    _uiEvent.emit("UPDATE_FAILURE")
                }
            isLoading = false
        }
    }
}
