package com.garbia.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.material3.darkColorScheme

// 1. CREAMOS UNA LISTA CON LAS 3 OPCIONES POSIBLES
enum class AppThemeColor {
    GREEN, PURPLE, BLUE, ORANGE_DARK
}

// 2. DEFINIMOS LOS 3 "CUBOS DE PINTURA" DIFERENTES
private val GreenColorScheme = lightColorScheme(
    primary = GreenGarbiA,       // El color principal (botones, iconos)
    background = BackgroundWhite, // Fondo de la app
    surface = Color.White,       // Tarjetas y barras
    onPrimary = Color.White      // Texto encima de botones verdes
)

private val PurpleColorScheme = lightColorScheme(
    primary = PurpleGarbiA,
    background = BackgroundWhite,
    surface = Color.White,
    onPrimary = Color.White
)

private val BlueColorScheme = lightColorScheme(
    primary = BlueGarbiA,
    background = BackgroundWhite,
    surface = Color.White,
    onPrimary = Color.White
)


// 2. DEFINIMOS EL ESQUEMA OSCURO
private val OrangeDarkColorScheme = darkColorScheme(
    primary = OrangeNeon,          // Botones y acciones en Naranja
    background = DarkBackground,   // Fondo general NEGRO/GRIS
    surface = DarkSurface,         // Fondo de la barra de navegación y tarjetas
    onPrimary = Color.White,       // Texto sobre el naranja
    onBackground = TextWhite,      // Texto sobre el fondo negro (automático blanco)
    onSurface = TextWhite          // Texto sobre las tarjetas (automático blanco)
)

// 3. EL COMPONENTE PRINCIPAL DEL TEMA (El Interruptor)
@Composable
fun GarbiaAppTheme(
    themeColor: AppThemeColor = AppThemeColor.PURPLE,
    content: @Composable () -> Unit
) {
    // 3. ELEGIMOS EL ESQUEMA
    val colorScheme = when (themeColor) {
        AppThemeColor.GREEN -> GreenColorScheme
        AppThemeColor.PURPLE -> PurpleColorScheme
        AppThemeColor.BLUE -> BlueColorScheme
        AppThemeColor.ORANGE_DARK -> OrangeDarkColorScheme // <--- ¡NUEVO!
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()

            // TRUCO: Si es el tema naranja, los iconos de la barra de estado (batería, hora)
            // deben ser claros (false), si no, oscuros (true).
            val isDark = themeColor == AppThemeColor.ORANGE_DARK
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}