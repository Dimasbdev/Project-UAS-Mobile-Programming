package id.ac.umkt.kel_10_mk.projectuas.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ParkirAccent,
    secondary = ParkirWarning,
    tertiary = ParkirDanger,
    background = ParkirBackground,
    surface = ParkirSurface,
    onPrimary = ParkirBackground,
    onSecondary = ParkirBackground,
    onTertiary = ParkirBackground,
    onBackground = ParkirTextPrimary,
    onSurface = ParkirTextPrimary,
)

@Composable
fun PROJECTUASTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}