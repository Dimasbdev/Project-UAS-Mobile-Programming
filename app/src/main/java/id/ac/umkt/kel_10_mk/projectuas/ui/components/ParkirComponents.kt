package id.ac.umkt.kel_10_mk.projectuas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import id.ac.umkt.kel_10_mk.projectuas.ParkingStatus
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirAccent
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirBackground
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirBottomNav
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirDivider
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirInactive
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirChipSurface
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirSurface
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.ParkirTextPrimary
import id.ac.umkt.kel_10_mk.projectuas.ui.theme.SpaceGroteskFamily

@Composable
fun ParkirTopBar(
    title: String = "PARKIRUMKT",
    showAction: Boolean = true,
    actionIcon: ImageVector = Icons.Default.Notifications,
    onActionClick: () -> Unit = {},
) {
    val arrangement = if (showAction) Arrangement.SpaceBetween else Arrangement.Start
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = ParkirAccent,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            letterSpacing = 1.4.sp,
        )
        if (showAction) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(ParkirSurface, CircleShape)
                    .border(BorderStroke(1.dp, ParkirDivider), CircleShape)
                    .clickable { onActionClick() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = "Aksi",
                    tint = ParkirAccent,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

data class BottomNavItemData(
    val label: String,
    val icon: ImageVector,
    val route: String,
)

@Composable
fun ParkirBottomNavBar(
    navController: NavHostController,
    items: List<BottomNavItemData>,
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ParkirBottomNav)
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            BottomNavItem(
                label = item.label,
                icon = item.icon,
                isSelected = currentRoute == item.route,
                onClick = { navigateTo(navController, item.route) },
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (isSelected) ParkirAccent else ParkirInactive
    Column(
        modifier = Modifier
            .widthIn(min = 48.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label.uppercase(),
            color = tint,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp,
        )
    }
}

fun navigateTo(
    navController: NavHostController,
    route: String,
) {
    navController.navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
    }
}

@Composable
fun StatusBadge(status: ParkingStatus, color: Color) {
    val label = statusLabel(status)
    Row(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.7f)), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape),
        )
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
        )
    }
}

@Composable
fun ParkingStatusBar(status: ParkingStatus, color: Color) {
    val filledSegments = when (status) {
        ParkingStatus.SEPI -> 1
        ParkingStatus.SEDANG -> 2
        ParkingStatus.PENUH -> 3
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(3) { index ->
            val segmentColor = if (index < filledSegments) color else ParkirDivider
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(segmentColor, RoundedCornerShape(999.dp)),
            )
        }
    }
}

@Composable
fun StatusChip(label: String, color: Color) {
    Row(
        modifier = Modifier
            .background(ParkirChipSurface, RoundedCornerShape(999.dp))
            .border(BorderStroke(1.dp, ParkirDivider), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape),
        )
        Text(
            text = label,
            color = ParkirTextPrimary,
            fontSize = 12.sp,
            maxLines = 1,
        )
    }
}

fun statusLabel(status: ParkingStatus): String = when (status) {
    ParkingStatus.SEPI -> "SEPI"
    ParkingStatus.SEDANG -> "SEDANG"
    ParkingStatus.PENUH -> "PENUH"
}
