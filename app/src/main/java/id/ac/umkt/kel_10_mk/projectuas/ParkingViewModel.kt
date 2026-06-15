package id.ac.umkt.kel_10_mk.projectuas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import id.ac.umkt.kel_10_mk.projectuas.models.ActivityLog
import id.ac.umkt.kel_10_mk.projectuas.models.ParkingArea
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

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

    private val _analyticsLogs = MutableStateFlow<List<ActivityLog>>(emptyList())
    val analyticsLogs: StateFlow<List<ActivityLog>> = _analyticsLogs.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _logsLimit = MutableStateFlow(50)
    val logsLimit: StateFlow<Int> = _logsLimit.asStateFlow()

    private val _activityLogs = MutableStateFlow<List<ActivityLog>>(emptyList())
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs.asStateFlow()

    private var parkingAreasJob: Job? = null
    private var analyticsLogsJob: Job? = null
    private var activityLogsJob: Job? = null

    init {
        refreshParkingData()
    }

    fun refreshParkingData() {
        observeParkingAreas()
        observeAnalyticsLogs()
        observeActivityLogs()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun observeActivityLogs() {
        activityLogsJob?.cancel()
        activityLogsJob = viewModelScope.launch {
            _logsLimit
                .flatMapLatest { limit -> repository.getActivityLogs(limit) }
                .catch { e ->
                    android.util.Log.e("ParkingViewModel", "FIRESTORE_ERROR: Error in activityLogs flow", e)
                    emit(emptyList())
                }
                .collect { logs ->
                    _activityLogs.value = logs
                }
        }
    }

    private fun observeAnalyticsLogs() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -7)
        val cutoff = Timestamp(cal.time)
        analyticsLogsJob?.cancel()
        analyticsLogsJob = viewModelScope.launch {
            repository.getLogsAfter(cutoff)
                .catch { e ->
                    android.util.Log.e("ParkingViewModel", "FIRESTORE_ERROR: Error in observeAnalyticsLogs flow", e)
                    _analyticsLogs.value = emptyList()
                }
                .collect { logs ->
                    _analyticsLogs.value = logs
                }
        }
    }

    fun loadMoreLogs() {
        _logsLimit.value += 50
    }

    private fun observeParkingAreas() {
        parkingAreasJob?.cancel()
        parkingAreasJob = viewModelScope.launch {
            repository.getParkingAreas()
                .catch { e ->
                    android.util.Log.e("ParkingViewModel", "FIRESTORE_ERROR: Error in getParkingAreas flow", e)
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
            try {
                repository.generateDummyData()
                _uiEvent.emit("DUMMY_DATA_SUCCESS")
            } catch (e: Exception) {
                android.util.Log.e("ParkingViewModel", "Failed to generate dummy data", e)
                _uiEvent.emit("DUMMY_DATA_FAILURE")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearData() {
        parkingAreasJob?.cancel()
        analyticsLogsJob?.cancel()
        activityLogsJob?.cancel()
        _parkingAreas.value = emptyList()
        _analyticsLogs.value = emptyList()
        _activityLogs.value = emptyList()
    }
}
