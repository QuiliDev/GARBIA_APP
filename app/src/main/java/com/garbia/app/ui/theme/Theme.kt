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

// 1. CREAMOS UNA LISTA CON LAS 3 OPCIONES POSIBLES
enum class AppThemeColor {
    GREEN, PURPLE, BLUE
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

// 3. EL COMPONENTE PRINCIPAL DEL TEMA (El Interruptor)
@Composable
fun GarbiaAppTheme(
    // Por defecto, la app arrancará en LILA, pero podemos pasarle otro
    themeColor: AppThemeColor = AppThemeColor.PURPLE,
    content: @Composable () -> Unit
) {
    // Aquí decidimos qué cubo de pintura usar según lo que elija el usuario
    val colorScheme = when (themeColor) {
        AppThemeColor.GREEN -> GreenColorScheme
        AppThemeColor.PURPLE -> PurpleColorScheme
        AppThemeColor.BLUE -> BlueColorScheme
    }

    // (Esto es solo para pintar la barra de arriba del móvil donde sale la hora y la batería)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    // APLICAMOS EL COLOR AL TEMA DE LA APP
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // (Si tienes tipografías personalizadas)
        content = content
    )
}