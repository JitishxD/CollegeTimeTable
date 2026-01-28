package me.jitish.collegetimetable.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ===========================================
// MATTE DARK COLOR SCHEME
// Deep, muted tones for a calm night mode
// ===========================================
private val DarkColorScheme = darkColorScheme(
    primary = MatteDustyBlueDark,
    onPrimary = MatteNightText,
    primaryContainer = MatteNightBorder,
    onPrimaryContainer = MatteNightText,
    secondary = MatteTaupeLight,
    onSecondary = MatteNightSurface,
    secondaryContainer = MatteNightBorder,
    onSecondaryContainer = MatteNightText,
    tertiary = MatteSageDark,
    onTertiary = MatteNightSurface,
    background = MatteNightSurface,
    onBackground = MatteNightText,
    surface = MatteNightCard,
    onSurface = MatteNightText,
    surfaceVariant = MatteNightBorder,
    onSurfaceVariant = MatteNightMuted,
    outline = MatteNightBorder,
    outlineVariant = Color(0xFF48484A)
)

// -------- MATTE LIGHT COLOR SCHEME --------
private val LightColorScheme = lightColorScheme(
    primary = MatteDustyBlueLight,
    onPrimary = MatteWhite,
    primaryContainer = MatteDustyBlueSubtle,
    onPrimaryContainer = MatteCharcoal,
    secondary = MatteTaupe,
    onSecondary = MatteWhite,
    secondaryContainer = MatteTaupeSubtle,
    onSecondaryContainer = MatteCharcoal,
    tertiary = MatteSageLight,
    onTertiary = MatteWhite,
    tertiaryContainer = MatteSageSubtle,
    onTertiaryContainer = MatteCharcoal,
    background = MatteWhite,
    onBackground = MatteCharcoal,
    surface = MatteCream,
    onSurface = MatteCharcoal,
    surfaceVariant = MatteStone,
    onSurfaceVariant = MatteSlate,
    outline = MattePebble,
    outlineVariant = MatteStone
)

@Composable
fun CollegeTimeTableTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our custom matte theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}