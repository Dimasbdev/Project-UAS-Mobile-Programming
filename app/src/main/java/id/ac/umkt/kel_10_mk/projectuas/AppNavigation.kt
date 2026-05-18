package id.ac.umkt.kel_10_mk.projectuas

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = RouteLogin,
    ) {
        composable(RouteLogin) {
            LoginScreen(
                onLoginMahasiswa = { _, _ -> navController.navigate(RouteDashboardMahasiswa) },
                onLoginPetugas = { _, _ -> navController.navigate(RouteDashboardPetugas) },
                onRegisterClick = { navController.navigate(RouteRegister) },
                onForgotClick = { navController.navigate(RouteForgot) },
            )
        }
        composable(RouteRegister) {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { _, _, _ -> },
                onLoginClick = { navController.popBackStack() },
            )
        }
        composable(RouteForgot) {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onSendClick = { },
                onLoginClick = { navController.popBackStack() },
            )
        }
        composable(RouteDashboardMahasiswa) {
            DashboardMahasiswaScreen(navController)
        }
        composable(RouteDashboardPetugas) {
            DashboardPetugasScreen(navController)
        }
        composable(RouteMapPetugas) {
            MapPetugasScreen(navController)
        }
        composable(RouteHistoryPetugas) {
            HistoryPetugasScreen(navController)
        }
        composable(RouteProfilePetugas) {
            ProfilePetugasScreen(navController)
        }
        composable(RouteUpdateKondisiPetugas) {
            UpdateKondisiScreen(navController)
        }
        composable(RouteMapMahasiswa) {
            MapMahasiswaScreen(navController)
        }
        composable(RouteHistoryMahasiswa) {
            HistoryMahasiswaScreen(navController)
        }
        composable(RouteProfileMahasiswa) {
            ProfileMahasiswaScreen(navController)
        }
        composable(RouteNotificationsMahasiswa) {
            NotificationsMahasiswaScreen(navController)
        }
        composable(RouteLocationPermissionMahasiswa) {
            LocationPermissionMahasiswaScreen(navController)
        }
    }
}
