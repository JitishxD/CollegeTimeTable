package me.jitish.collegetimetable.ui.theme

import androidx.compose.ui.graphics.Color

// -------- MATTE DESIGN PALETTE --------

// --- Neutral Base Colors (Light Theme) ---
val MatteWhite = Color(0xFFFAFAF9)           // background
val MatteCream = Color(0xFFF5F4F0)           // Soft cream surface
val MatteStone = Color(0xFFE8E6E1)           // Light stone for cards
val MattePebble = Color(0xFFD4D2CD)          // Subtle borders/dividers
val MatteSlate = Color(0xFF9C9A94)           // Muted text secondary
val MatteCharcoal = Color(0xFF52504A)        // Primary text light mode

// --- Neutral Base Colors (Dark Theme) ---
val MatteNightSurface = Color(0xFF1C1C1E)    // Deep charcoal background
val MatteNightCard = Color(0xFF2C2C2E)       // Elevated surface dark
val MatteNightBorder = Color(0xFF3A3A3C)     // Subtle borders dark
val MatteNightMuted = Color(0xFF8E8E93)      // Secondary text dark
val MatteNightText = Color(0xFFE5E5E7)       // Primary text dark

// --- Accent Colors (Muted & Desaturated) ---
// Sage Green
val MatteSageLight = Color(0xFF8BA888)       // Muted sage for light mode
val MatteSageDark = Color(0xFF9CB699)        // Slightly lighter for dark mode
val MatteSageSubtle = Color(0xFFE8EFE7)      // Very light sage tint

// Dusty Blue
val MatteDustyBlueLight = Color(0xFF7A9BB5)  // Muted blue for light mode
val MatteDustyBlueDark = Color(0xFF8FAEC4)   // Slightly lighter for dark mode
val MatteDustyBlueSubtle = Color(0xFFE6EEF4) // Very light blue tint

// Warm Taupe
val MatteTaupe = Color(0xFF9A8F85)           // Warm neutral accent
val MatteTaupeLight = Color(0xFFBFB5AB)      // Lighter taupe
val MatteTaupeSubtle = Color(0xFFF0ECE8)     // Very light taupe tint

// Soft Terracotta
val MatteTerracotta = Color(0xFFBFA094)      // Muted terracotta
val MatteTerracottaSubtle = Color(0xFFF5EFEB) // Very light terracotta tint

// --- Legacy Color Mappings (for backward compatibility) ---
// Light theme colors (remapped to matte palette)
val Purple80 = MatteDustyBlueSubtle
val PurpleGrey80 = MatteStone
val Pink80 = MatteTerracottaSubtle

val Purple40 = MatteDustyBlueLight
val PurpleGrey40 = MatteSlate
val Pink40 = MatteTaupe

// Dark theme base colors
val MidnightBlue = MatteNightSurface
val MidnightBlueDark = MatteNightCard
val MidnightBlueLight = MatteNightBorder
val MidnightAccent = MatteDustyBlueDark
val MidnightSurface = MatteNightCard
val MidnightSurfaceVariant = MatteNightBorder

// App-specific colors - Light mode
val CurrentClassGreen = MatteSageLight
val UpcomingClassBlue = MatteDustyBlueLight
val CardBackgroundLight = MatteCream

// App-specific colors - Dark mode
val CurrentClassGreenDark = MatteSageDark
val UpcomingClassBlueDark = MatteDustyBlueDark
val CardBackgroundDark = MatteNightCard

// --- Text Colors ---
val TextPrimaryLight = MatteCharcoal
val TextSecondaryLight = MatteSlate
val TextPrimaryDark = MatteNightText
val TextSecondaryDark = MatteNightMuted

// --- Shadow & Overlay Colors ---
val SoftShadow = Color(0x0A000000)           // Very subtle shadow (4% opacity)
val MediumShadow = Color(0x12000000)         // Medium shadow (7% opacity)

