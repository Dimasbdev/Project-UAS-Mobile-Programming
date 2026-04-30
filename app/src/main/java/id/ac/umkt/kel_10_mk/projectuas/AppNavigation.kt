package id.ac.umkt.kel_10_mk.projectuas

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

private const val RouteLogin = "login"
private const val RouteRegister = "register"
private const val RouteForgot = "forgot"

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
                onLoginClick = { _, _ -> },
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
    }
}
