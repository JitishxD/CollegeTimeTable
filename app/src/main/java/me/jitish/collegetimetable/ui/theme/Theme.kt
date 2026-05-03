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

// -------- APP DARK COLOR SCHEME --------
private val DarkColorScheme = darkColorScheme(
    primary = AppBlueBright,
    onPrimary = AppCanvasDark,
    primaryContainer = AppBlueContainerDark,
    onPrimaryContainer = AppTextStrongDark,
    secondary = AppAmberBright,
    onSecondary = AppCanvasDark,
    secondaryContainer = AppAmberContainerDark,
    onSecondaryContainer = AppTextStrongDark,
    tertiary = AppGreenBright,
    onTertiary = AppCanvasDark,
    tertiaryContainer = AppGreenContainerDark,
    onTertiaryContainer = AppTextStrongDark,
    background = AppCanvasDark,
    onBackground = AppTextStrongDark,
    surface = AppSurfaceDark,
    onSurface = AppTextStrongDark,
    surfaceVariant = AppSurfaceVariantDark,
    onSurfaceVariant = AppTextMutedDark,
    outline = AppOutlineDark,
    outlineVariant = Color(0xFF4B5568)
)

// -------- APP LIGHT COLOR SCHEME --------
private val LightColorScheme = lightColorScheme(
    primary = AppBlue,
    onPrimary = Color.White,
    primaryContainer = AppBlueContainer,
    onPrimaryContainer = AppTextStrongLight,
    secondary = AppAmber,
    onSecondary = Color.White,
    secondaryContainer = AppAmberContainer,
    onSecondaryContainer = AppTextStrongLight,
    tertiary = AppGreen,
    onTertiary = Color.White,
    tertiaryContainer = AppGreenContainer,
    onTertiaryContainer = AppTextStrongLight,
    background = AppCanvasLight,
    onBackground = AppTextStrongLight,
    surface = AppSurfaceLight,
    onSurface = AppTextStrongLight,
    surfaceVariant = AppSurfaceVariantLight,
    onSurfaceVariant = AppTextMutedLight,
    outline = AppOutlineLight,
    outlineVariant = AppSurfaceVariantLight
)

@Composable
fun CollegeTimeTableTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use the custom app theme
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
