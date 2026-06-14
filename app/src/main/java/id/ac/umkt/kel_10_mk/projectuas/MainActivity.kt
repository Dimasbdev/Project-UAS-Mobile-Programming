package id.ac.umkt.kel_10_mk.projectuas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.PROJECTUASTheme

class MainActivity : ComponentActivity() {
    private val authRepository by lazy { AuthRepository() }
    private val parkingRepository by lazy { ParkingRepository() }
    private val factory by lazy { AppViewModelFactory(authRepository, parkingRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PROJECTUASTheme {
                AppNavigation(factory = factory)
            }
        }
    }
}
