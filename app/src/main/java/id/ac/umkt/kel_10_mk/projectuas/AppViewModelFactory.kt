package id.ac.umkt.kel_10_mk.projectuas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AppViewModelFactory(
    private val authRepository: AuthRepository,
    private val parkingRepository: ParkingRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(authRepository) as T
            modelClass.isAssignableFrom(ParkingViewModel::class.java) ->
                ParkingViewModel(parkingRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: $modelClass")
        }
    }
}
