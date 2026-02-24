package com.garbia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
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
import com.garbia.app.ui.components.GarbiaTopBar
import com.garbia.app.ui.screens.* // Importa tus pantallas
import com.garbia.app.ui.theme.AppThemeColor
import com.garbia.app.ui.theme.GarbiaAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentTheme by remember { mutableStateOf(AppThemeColor.GREEN) }

            GarbiaAppTheme(themeColor = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    // ✅ CAMBIO CRÍTICO 1: LOGICA DE LA BARRA SUPERIOR
                    // Antes: currentRoute == Screen.Home.route || ...
                    // Ahora: ELIMINAMOS Screen.Home.route de aquí.
                    // La Home se encarga de su propia barra. Solo la mostramos en ResultScreen (u otras).
                    val showTopBar = currentRoute?.startsWith("result_screen") == true

                    // Lógica del tema oscuro (se mantiene igual)
                    val isDarkTheme = currentTheme == AppThemeColor.ORANGE_DARK

                    // Lógica de la barra inferior (se mantiene igual)
                    val showBottomNav = currentRoute == Screen.Home.route ||
                            currentRoute == Screen.Profile.route ||
                            currentRoute?.startsWith("result_screen") == true

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
                                // Asegúrate de usar el nombre correcto de tu componente BottomBar
                                BottomNavigationBar(navController = navController)
                            }
                        }
                    ) { innerPadding ->

                        val topPadding = if (currentRoute == Screen.Home.route) 0.dp else innerPadding.calculateTopPadding()
                        val bottomPadding = if (currentRoute == Screen.Home.route) 0.dp else innerPadding.calculateBottomPadding()

                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(top = topPadding, bottom = bottomPadding)
                        ) {
                            // 1. HOME SCREEN
                            composable(Screen.Home.route) {
                                HomeScreen(navController)
                            }

                            // 2. OTRAS PANTALLAS
                            composable(Screen.Profile.route) {
                                ProfileScreen(
                                    currentTheme = currentTheme,
                                    onThemeChanged = { newColor -> currentTheme = newColor }
                                )
                            }
                            composable(Screen.Camera.route) { CameraScreen(navController) }

                            composable("preview_screen/{photoUri}") { backStackEntry ->
                                val encodedUri = backStackEntry.arguments?.getString("photoUri") ?: ""
                                val decodedUri = URLDecoder.decode(encodedUri, "UTF-8")
                                PhotoPreviewScreen(navController, decodedUri)
                            }

                            composable("processing_screen/{photoUri}") { backStackEntry ->
                                val encodedUri = backStackEntry.arguments?.getString("photoUri") ?: ""
                                ProcessingScreen(navController, encodedUri)
                            }

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