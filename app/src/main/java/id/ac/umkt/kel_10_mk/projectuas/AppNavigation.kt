package id.ac.umkt.kel_10_mk.projectuas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirAccent
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirBackground
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.SpaceGroteskFamily

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    factory: ViewModelProvider.Factory? = null,
) {
    val authViewModel: AuthViewModel = if (factory != null) viewModel(factory = factory) else viewModel()
    val parkingViewModel: ParkingViewModel = if (factory != null) viewModel(factory = factory) else viewModel()

    val context = LocalContext.current



    LaunchedEffect(Unit) {
        authViewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(authViewModel.uiState) {
        when (val state = authViewModel.uiState) {
            is AuthUiState.Success -> {
                parkingViewModel.refreshParkingData()
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            is AuthUiState.Idle -> {
                parkingViewModel.clearData()
            }
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        authViewModel.navigationEvent.collect { route ->
            navController.navigate(route) {
                if (route == RouteDashboardMahasiswa || route == RouteDashboardPetugas) {
                    popUpTo(RouteLogin) { inclusive = true }
                }
            }
        }
    }

    if (authViewModel.isCheckingSession) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ParkirBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "PARKIRUMKT",
                    color = ParkirAccent,
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    letterSpacing = 2.6.sp,
                )
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(
                    color = ParkirAccent,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    } else {
        val startDest = when (val state = authViewModel.uiState) {
            is AuthUiState.Success -> {
                if (state.user.role == "petugas") RouteDashboardPetugas else RouteDashboardMahasiswa
            }
            else -> RouteLogin
        }

        NavHost(
            navController = navController,
            startDestination = startDest,
        ) {
            composable(RouteLogin) {
                val isLoading = authViewModel.uiState is AuthUiState.Loading
                LoginScreen(
                    onLogin = { email, password ->
                        authViewModel.login(email, password)
                    },
                    onRegisterClick = { navController.navigate(RouteRegister) },
                    onForgotClick = { navController.navigate(RouteForgot) },
                    isLoading = isLoading,
                )
            }
            composable(RouteRegister) {
                val isLoading = authViewModel.uiState is AuthUiState.Loading
                RegisterScreen(
                    onBackClick = { navController.popBackStack() },
                    onRegisterClick = { name, email, password ->
                        authViewModel.register(name, email, password)
                    },
                    onLoginClick = { navController.popBackStack() },
                    isLoading = isLoading,
                )
            }
            composable(RouteForgot) {
                val isLoading = authViewModel.uiState is AuthUiState.Loading
                ForgotPasswordScreen(
                    onBackClick = { navController.popBackStack() },
                    onSendClick = { email ->
                        authViewModel.resetPassword(email)
                    },
                    onLoginClick = { navController.popBackStack() },
                    isLoading = isLoading,
                )
            }
            composable(RouteDashboardMahasiswa) {
                val studentName = authViewModel.currentUser?.name ?: "Mahasiswa"
                DashboardMahasiswaScreen(navController, parkingViewModel, studentName)
            }
            composable(RouteDashboardPetugas) {
                val officerName = authViewModel.currentUser?.name ?: "Petugas"
                DashboardPetugasScreen(navController, parkingViewModel, officerName)
            }
            composable(RouteMapPetugas) {
                MapPetugasScreen(navController, parkingViewModel)
            }
            composable(RouteHistoryPetugas) {
                HistoryPetugasScreen(navController, parkingViewModel)
            }
            composable(RouteProfilePetugas) {
                ProfilePetugasScreen(
                    navController = navController,
                    onLogoutClick = { authViewModel.logout() },
                    currentUser = authViewModel.currentUser,
                    viewModel = parkingViewModel
                )
            }
            composable(
                route = RouteUpdateKondisiPetugas,
                arguments = listOf(
                    androidx.navigation.navArgument("areaId") {
                        type = androidx.navigation.NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val areaId = backStackEntry.arguments?.getString("areaId") ?: ""
                UpdateKondisiScreen(navController, areaId, parkingViewModel, authViewModel)
            }
            composable(RouteMapMahasiswa) {
                MapMahasiswaScreen(navController, parkingViewModel)
            }
            composable(RouteHistoryMahasiswa) {
                HistoryMahasiswaScreen(navController, parkingViewModel)
            }
            composable(RouteProfileMahasiswa) {
                ProfileMahasiswaScreen(
                    navController = navController,
                    onLogoutClick = { authViewModel.logout() },
                    currentUser = authViewModel.currentUser
                )
            }
            composable(RouteNotificationsMahasiswa) {
                NotificationsMahasiswaScreen(navController, parkingViewModel)
            }
            composable(RouteLocationPermissionMahasiswa) {
                LocationPermissionMahasiswaScreen(navController)
            }
        }
    }
}
