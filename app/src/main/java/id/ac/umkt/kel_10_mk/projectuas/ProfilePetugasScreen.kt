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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirStatCard
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirSurface
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextPrimary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextSecondary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.SpaceGroteskFamily
import id.ac.umkt.kel_10_mk.projectuas.models.User

@Composable
fun ProfilePetugasScreen(
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ParkirBackground,
        bottomBar = {
            ParkirBottomNavBar(
                navController = navController,
                items = listOf(
                    BottomNavItemData("Home", Icons.Default.Home, RouteDashboardPetugas),
                    BottomNavItemData("Map", Icons.Default.Map, RouteMapPetugas),
                    BottomNavItemData("History", Icons.Default.History, RouteHistoryPetugas),
                    BottomNavItemData("Profile", Icons.Default.AccountCircle, RouteProfilePetugas),
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
            ParkirTopBar(showAction = false)
            val initials = currentUser?.name?.split(" ")
                ?.filter { it.isNotBlank() }
                ?.mapNotNull { it.firstOrNull() }
                ?.joinToString("")
                ?.take(2)
                ?.uppercase() ?: "PT"

            ProfileHeader(
                initials = if (initials.isEmpty()) "PT" else initials,
                name = currentUser?.name ?: "Petugas Parkir",
                email = currentUser?.email ?: "petugas@umkt.ac.id",
            )
            RoleChip()
            StatsRow()
            Text(
                text = "PENGATURAN",
                color = ParkirTextSecondary,
                fontSize = 12.sp,
                letterSpacing = 1.2.sp,
            )
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
                .size(96.dp)
                .clip(CircleShape)
                .background(ParkirIconChip)
                .border(BorderStroke(1.dp, ParkirDivider), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                color = ParkirAccent,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                letterSpacing = 1.2.sp,
            )
        }
        Text(
            text = name,
            modifier = Modifier.fillMaxWidth(),
            color = ParkirTextPrimary,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = email,
            modifier = Modifier.fillMaxWidth(),
            color = ParkirTextSecondary,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RoleChip() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(ParkirSurface)
            .border(BorderStroke(1.dp, ParkirAccent.copy(alpha = 0.6f)), RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = "Petugas",
            tint = ParkirAccent,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = "Petugas Parkir",
            color = ParkirAccent,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun StatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard(
            label = "Total\nUpdate",
            value = "128",
            modifier = Modifier.weight(1f),
        )
        StatCard(
            label = "Parkiran B\nArea Tersibuk",
            value = "B",
            modifier = Modifier.weight(1f),
        )
        StatCard(
            label = "Status\nHari Ini",
            value = "Aktif",
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(108.dp)
            .background(ParkirStatCard, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            color = ParkirAccent,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        Text(
            text = label,
            color = ParkirTextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
        )
    }
}

@Composable
private fun LogoutCard(onLogoutClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirSurface, RoundedCornerShape(18.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(18.dp))
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .clickable { onLogoutClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(ParkirIconChip, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = ParkirDanger,
                    modifier = Modifier.size(18.dp),
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
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = ParkirTextSecondary,
            modifier = Modifier.size(18.dp),
        )
    }
}
