package com.garbia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.garbia.app.ui.Screen
import com.garbia.app.ui.components.BottomNavigationBar
import com.garbia.app.ui.screens.CameraScreen
import com.garbia.app.ui.screens.HomeScreen
import com.garbia.app.ui.screens.ProfileScreen
import com.garbia.app.ui.screens.ProcessingScreen
import com.garbia.app.ui.screens.ResultScreen
import com.garbia.app.ui.theme.AppThemeColor
import com.garbia.app.ui.theme.GarbiaAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Variable para el tema de color (Verde, Lila, Celeste)
            var currentTheme by remember { mutableStateOf(AppThemeColor.GREEN) }

            GarbiaAppTheme(themeColor = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Detectamos en qué pantalla estamos para ocultar la barra si hace falta
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    // Ocultamos la barra en: Cámara, Previsualización, Procesando y Resultado
                    val showBottomNav = currentRoute == Screen.Home.route || currentRoute == Screen.Profile.route
                            || currentRoute?.startsWith("result_screen") == true

                    Scaffold(
                        bottomBar = {
                            if (showBottomNav) {
                                BottomNavigationBar(navController = navController)
                            }
                        }
                    ) { paddingValues ->
                        val bottomPadding = if (showBottomNav) paddingValues else PaddingValues(0.dp)

                        Box(modifier = Modifier.padding(bottomPadding)) {
                            NavHost(
                                navController = navController,
                                startDestination = Screen.Home.route
                            ) {
                                // 1. PANTALLAS PRINCIPALES
                                composable(Screen.Home.route) { HomeScreen(navController) }
                                composable(Screen.Profile.route) { ProfileScreen() }
                                composable(Screen.Camera.route) { CameraScreen(navController) }

                                // 2. PANTALLA DE PREVISUALIZACIÓN (Foto congelada)
                                composable("preview_screen/{photoUri}") { backStackEntry ->
                                    val encodedUri = backStackEntry.arguments?.getString("photoUri") ?: ""
                                    val decodedUri = java.net.URLDecoder.decode(encodedUri, "UTF-8")
                                    com.garbia.app.ui.screens.PhotoPreviewScreen(navController, decodedUri)
                                }

                                // 3. PANTALLA DE PROCESANDO (Carga con IA)
                                composable("processing_screen/{photoUri}") { backStackEntry ->
                                    val encodedUri = backStackEntry.arguments?.getString("photoUri") ?: ""
                                    // Pasamos la URI codificada directamente
                                    ProcessingScreen(navController, encodedUri)
                                }

                                // 4. PANTALLA DE RESULTADO FINAL
                                composable("result_screen/{photoUri}") { backStackEntry ->
                                    val encodedUri = backStackEntry.arguments?.getString("photoUri") ?: ""
                                    ResultScreen(navController, encodedUri)
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}