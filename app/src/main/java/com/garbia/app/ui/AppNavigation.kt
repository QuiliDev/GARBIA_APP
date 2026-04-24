package com.garbia.app.ui

// Define las rutas disponibles en la app
sealed class Screen(val route: String) {
    object Home    : Screen("home_screen")
    object Camera  : Screen("camera_screen")
    object Profile : Screen("profile_screen")
    object Result  : Screen("result_screen/{photoUri}")

    // Secciones de acceso rápido
    object Ranking    : Screen("ranking_screen")
    object Premios    : Screen("premios_screen")
    object Tips       : Screen("tips_screen")
    object Mapas      : Screen("mapas_screen")
    object Onboarding : Screen("onboarding_screen")
    object Logros       : Screen("logros_screen")
    object Estadisticas : Screen("estadisticas_screen")
    object Historial    : Screen("historial_screen")
}