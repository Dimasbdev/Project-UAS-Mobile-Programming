package id.ac.umkt.kel_10_mk.projectuas

import android.app.Activity
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import id.ac.umkt.kel_10_mk.projectuas.ui.components.BottomNavItemData
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirBottomNavBar
import id.ac.umkt.kel_10_mk.projectuas.ui.components.ParkirTopBar
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirAccent
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirBackground
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDanger
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDivider
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirIconChip
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirSurface
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextPrimary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextSecondary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.SpaceGroteskFamily
import id.ac.umkt.kel_10_mk.projectuas.models.User

private data class ProfileMenuItem(
    val label: String,
    val icon: ImageVector,
    val tint: Color,
    val onClick: () -> Unit,
)

@Composable
fun ProfileMahasiswaScreen(
    navController: NavHostController,
    onLogoutClick: () -> Unit = {},
    currentUser: User? = null,
) {
    val view = LocalView.current
    val context = LocalContext.current

    SideEffect {
        (context as? Activity)?.window?.run {
            statusBarColor = ParkirBackground.toArgb()
            WindowCompat.getInsetsController(this, view).isAppearanceLightStatusBars = false
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    val menuItems = listOf(
        ProfileMenuItem(
            "Notifikasi",
            Icons.Default.Notifications,
            ParkirAccent,
            onClick = { navController.navigate(RouteNotificationsMahasiswa) },
        ),
        ProfileMenuItem(
            "Perizinan Lokasi",
            Icons.Default.LocationOn,
            ParkirAccent,
            onClick = { navController.navigate(RouteLocationPermissionMahasiswa) },
        ),
        ProfileMenuItem(
            "Info Kampus",
            Icons.Default.Info,
            ParkirAccent,
            onClick = { },
        ),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ParkirBackground,
        bottomBar = {
            ParkirBottomNavBar(
                navController = navController,
                items = listOf(
                    BottomNavItemData("Home", Icons.Default.Home, RouteDashboardMahasiswa),
                    BottomNavItemData("Map", Icons.Default.Map, RouteMapMahasiswa),
                    BottomNavItemData("History", Icons.Default.History, RouteHistoryMahasiswa),
                    BottomNavItemData("Profile", Icons.Default.AccountCircle, RouteProfileMahasiswa),
                ),
            )
        },
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
            ParkirTopBar()
            Text(
                text = "Profil Saya",
                color = ParkirTextPrimary,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
            )
            val initials = currentUser?.name?.split(" ")
                ?.filter { it.isNotBlank() }
                ?.mapNotNull { it.firstOrNull() }
                ?.joinToString("")
                ?.take(2)
                ?.uppercase() ?: "MH"

            ProfileHeader(
                initials = if (initials.isEmpty()) "MH" else initials,
                name = currentUser?.name ?: "Mahasiswa",
                email = currentUser?.email ?: "mahasiswa@umkt.ac.id",
            )
            MenuCard(items = menuItems)
            LogoutCard(onLogoutClick = { showLogoutDialog = true })
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = ParkirSurface,
            title = {
                Text(
                    text = "Konfirmasi Logout",
                    color = ParkirTextPrimary,
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            text = {
                Text(
                    text = "Kamu yakin ingin keluar dari akun ini?",
                    color = ParkirTextSecondary,
                    fontFamily = SpaceGroteskFamily,
                    fontSize = 13.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogoutClick()
                }) {
                    Text(
                        text = "Logout",
                        color = ParkirDanger,
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(
                        text = "Batal",
                        color = ParkirTextSecondary,
                        fontFamily = SpaceGroteskFamily,
                    )
                }
            },
        )
    }
}

@Composable
private fun ProfileHeader(
    initials: String,
    name: String,
    email: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(CircleShape)
                .background(ParkirAccent.copy(alpha = 0.9f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                color = ParkirBackground,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                letterSpacing = 1.2.sp,
            )
        }
        Text(
            text = name,
            color = ParkirTextPrimary,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
        Text(
            text = email,
            color = ParkirTextSecondary,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun MenuCard(items: List<ProfileMenuItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(22.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(22.dp))
            .padding(vertical = 8.dp),
    ) {
        items.forEachIndexed { index, item ->
            ProfileMenuRow(item)
            if (index < items.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 72.dp)
                        .height(1.dp)
                        .background(ParkirDivider),
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(item: ProfileMenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .clickable { item.onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.widthIn(min = 200.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(ParkirIconChip, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = item.tint,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = item.label,
                color = ParkirTextPrimary,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "Buka",
            tint = ParkirTextSecondary,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun LogoutCard(onLogoutClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(20.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(20.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .clickable { onLogoutClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(ParkirIconChip, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = ParkirDanger,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = "Logout",
                color = ParkirDanger,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "Keluar",
            tint = ParkirDanger.copy(alpha = 0.8f),
            modifier = Modifier.size(16.dp),
        )
    }
}
