package com.garbia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.garbia.app.ui.Screen
import com.garbia.app.ui.components.BottomNavigationBar
import com.garbia.app.ui.components.GarbiaTopBar
import com.garbia.app.ui.screens.*
import com.garbia.app.ui.theme.AppThemeColor
import com.garbia.app.ui.theme.GarbiaAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Punto de entrada limpio
            GarbiaAppMain()
        }
    }
}

// -----------------------------------------------------------------------------
// 1. GARBIA APP: Maneja el Tema y el contenedor principal (Surface)
// -----------------------------------------------------------------------------
@Composable
fun GarbiaAppMain() {
    // Estado del tema elevado al nivel más alto
    var currentTheme by remember { mutableStateOf(AppThemeColor.GREEN) }

    GarbiaAppTheme(themeColor = currentTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen(
                currentTheme = currentTheme,
                onThemeChanged = { newTheme -> currentTheme = newTheme }
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 2. MAIN SCREEN: Estructura (Scaffold), Barras y Lógica de UI
// -----------------------------------------------------------------------------
@Composable
fun MainScreen(
    currentTheme: AppThemeColor,
    onThemeChanged: (AppThemeColor) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // --- LÓGICA DE VISIBILIDAD DE BARRAS ---

    // TopBar: Solo visible en pantallas de resultado (Home tiene la suya propia)
    val showTopBar = currentRoute?.startsWith("result_screen") == true

    // BottomBar: Visible en Home, Perfil y Resultado
    val showBottomNav = currentRoute == Screen.Home.route ||
            currentRoute == Screen.Profile.route ||
            currentRoute?.startsWith("result_screen") == true

    // Tema Oscuro: Para ajustar colores de la TopBar si es necesario
    val isDarkTheme = currentTheme == AppThemeColor.ORANGE_DARK

    Scaffold(
        topBar = {
            if (showTopBar) {
                GarbiaTopBar(
                    navController = navController,
                    isOverDarkBackground = isDarkTheme
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->

        // --- LÓGICA DE PADDING TRANSPARENTE ---
        // Si es HOME, anulamos el padding (0.dp) para que el contenido se dibuje detrás de las barras.
        // En el resto de pantallas, respetamos el padding del sistema.
        val topPadding = if (currentRoute == Screen.Home.route || currentRoute == Screen.Home.route) 0.dp else innerPadding.calculateTopPadding()
        val bottomPadding = if (currentRoute == Screen.Home.route) 0.dp else innerPadding.calculateBottomPadding()

        GarbiaNav(
            navController = navController,
            modifier = Modifier.padding(top = topPadding, bottom = bottomPadding),
            currentTheme = currentTheme,
            onThemeChanged = onThemeChanged
        )
    }
}

// -----------------------------------------------------------------------------
// 3. NAV GRAPH: Definición de rutas y pantallas
// -----------------------------------------------------------------------------
@Composable
fun GarbiaNav(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    currentTheme: AppThemeColor,
    onThemeChanged: (AppThemeColor) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // 1. HOME SCREEN
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        // 2. PROFILE SCREEN (Necesita callbacks del tema)
        composable(Screen.Profile.route) {
            ProfileScreen(
                currentTheme = currentTheme,
                onThemeChanged = onThemeChanged
            )
        }

        // 3. CÁMARA
        composable(Screen.Camera.route) {
            CameraScreen(navController)
        }

        // 4. PREVISUALIZACIÓN DE FOTO
        composable("preview_screen/{photoUri}") { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("photoUri") ?: ""
            val decodedUri = URLDecoder.decode(encodedUri, "UTF-8")
            PhotoPreviewScreen(navController, decodedUri)
        }

        // 5. PANTALLA DE PROCESAMIENTO (IA)
        composable("processing_screen/{photoUri}") { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("photoUri") ?: ""
            ProcessingScreen(navController, encodedUri)
        }

        // 6. PANTALLA DE RESULTADO FINAL
        composable("result_screen/{photoUri}") { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("photoUri") ?: ""
            ResultScreen(navController, encodedUri)
        }
    }
}