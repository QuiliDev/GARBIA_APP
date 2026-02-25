package com.garbia.app.ui

// Define las rutas disponibles en la app
sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Camera : Screen("camera_screen")
    object Profile : Screen("profile_screen")
}