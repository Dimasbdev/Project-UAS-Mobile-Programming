package id.ac.umkt.kel_10_mk.projectuas

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirAccent
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirBackground
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDanger
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDivider
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirIconChip
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirSurface
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextPrimary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextSecondary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.SpaceGroteskFamily

@Composable
fun LocationPermissionMahasiswaScreen(navController: NavHostController) {
    val view = LocalView.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isGranted by remember { mutableStateOf(hasLocationPermission(context)) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        isGranted = result.values.any { it }
    }

    SideEffect {
        (context as? Activity)?.window?.run {
            statusBarColor = ParkirBackground.toArgb()
            WindowCompat.getInsetsController(this, view).isAppearanceLightStatusBars = false
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isGranted = hasLocationPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ParkirBackground,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ParkirBackground)
                .statusBarsPadding()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            TopBar(
                title = "Perizinan Lokasi",
                onBack = { navController.popBackStack() },
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ParkirSurface, RoundedCornerShape(22.dp))
                    .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(22.dp))
                    .padding(20.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(ParkirIconChip, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Lokasi",
                            tint = ParkirAccent,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Akses lokasi diperlukan",
                            color = ParkirTextPrimary,
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = "Kami butuh lokasi kamu untuk menampilkan rute dan area parkir terdekat.",
                            color = ParkirTextSecondary,
                            fontFamily = SpaceGroteskFamily,
                            fontSize = 13.sp,
                        )
                        PermissionStatusChip(isGranted = isGranted)
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ActionButton(
                    label = "Izinkan Lokasi",
                    icon = Icons.Default.LocationOn,
                    background = ParkirAccent,
                    contentColor = ParkirBackground,
                    onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            )
                        )
                    },
                )
                ActionButton(
                    label = "Buka Pengaturan",
                    icon = Icons.Default.Settings,
                    background = ParkirSurface,
                    contentColor = ParkirTextPrimary,
                    onClick = { openAppSettings(context) },
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TopBar(
    title: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(ParkirSurface, CircleShape)
                .border(BorderStroke(1.dp, ParkirDivider), CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                tint = ParkirTextPrimary,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = title,
            color = ParkirTextPrimary,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
        )
    }
}

@Composable
private fun PermissionStatusChip(isGranted: Boolean) {
    val label = if (isGranted) "Status: Diizinkan" else "Status: Belum diizinkan"
    val tint = if (isGranted) ParkirAccent else ParkirDanger
    Row(
        modifier = Modifier
            .background(ParkirSurface, RoundedCornerShape(999.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(tint, CircleShape),
        )
        Text(
            text = label,
            color = tint,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun ActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    background: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = label,
                color = contentColor,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
    return fine || coarse
}

private fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null),
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
