package id.ac.umkt.kel_10_mk.projectuas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.ac.umkt.kel_10_mk.projectuas.models.ActivityLog
import id.ac.umkt.kel_10_mk.projectuas.models.ParkingArea
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ParkingViewModel(
    private val repository: ParkingRepository = ParkingRepository(),
) : ViewModel() {

    // Gunakan StateFlow agar lebih efisien dan proper untuk data stream
    private val _parkingAreas = MutableStateFlow<List<ParkingArea>>(emptyList())
    val parkingAreas: StateFlow<List<ParkingArea>> = _parkingAreas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentArea = MutableStateFlow<ParkingArea?>(null)
    val currentArea: StateFlow<ParkingArea?> = _currentArea.asStateFlow()

    private val _activityLogs = MutableStateFlow<List<ActivityLog>>(emptyList())
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _logsLimit = MutableStateFlow(50)
    val logsLimit: StateFlow<Int> = _logsLimit.asStateFlow()

    private var logsJob: Job? = null

    init {
        observeParkingAreas()
        observeActivityLogs()
    }

    private fun observeActivityLogs() {
        logsJob?.cancel()
        logsJob = viewModelScope.launch {
            repository.getActivityLogs(_logsLimit.value)
                .catch {
                    _activityLogs.value = emptyList()
                }
                .collect { logs ->
                    _activityLogs.value = logs
                }
        }
    }

    fun loadMoreLogs() {
        _logsLimit.value += 50
        observeActivityLogs()
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
                        _parkingAreas.value = areas
                    }
                }
        }
    }

    private fun useMockFallback() {
        _parkingAreas.value = listOf(
            ParkingArea("Parkiran A", "Gedung A", ParkingStatus.SEPI, 0, id = "parkiran_a"),
            ParkingArea("Parkiran B", "Gedung B", ParkingStatus.SEDANG, 0, id = "parkiran_b"),
            ParkingArea("Parkiran C", "Gedung C", ParkingStatus.PENUH, 0, id = "parkiran_c"),
            ParkingArea("Parkiran D", "Gedung D", ParkingStatus.SEPI, 0, id = "parkiran_d"),
        )
    }

    fun loadParkingArea(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _currentArea.value = repository.getParkingArea(id)
            _isLoading.value = false
        }
    }

    fun updateParkingArea(id: String, status: ParkingStatus, notes: String, officerName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateParkingArea(id, status, notes, officerName)
                .onSuccess {
                    _uiEvent.emit("UPDATE_SUCCESS")
                }
                .onFailure {
                    _uiEvent.emit("UPDATE_FAILURE")
                }
            _isLoading.value = false
        }
    }

    fun generateDummyData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.generateDummyData()
            _uiEvent.emit("DUMMY_DATA_SUCCESS")
            _isLoading.value = false
        }
    }
}
