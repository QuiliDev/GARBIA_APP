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
import com.garbia.app.ui.screens.CameraScreen
import com.garbia.app.ui.screens.HomeScreen
import com.garbia.app.ui.screens.ProfileScreen
import com.garbia.app.ui.theme.AppThemeColor // NUEVO: Importamos nuestras opciones de color
import com.garbia.app.ui.theme.GarbiaAppTheme // NUEVO: Importamos nuestro tema personalizado
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // NUEVO: 1. Creamos la variable que recuerda el color elegido.
            // Por defecto empezará en PURPLE (Lila), pero podemos cambiarlo a GREEN o BLUE.
            var currentTheme by remember { mutableStateOf(AppThemeColor.GREEN) }

            // NUEVO: 2. Envolvemos la app en NUESTRO tema, no en el genérico, y le pasamos el color.
            GarbiaAppTheme(themeColor = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    // MaterialTheme.colorScheme.background ahora será el fondo blanco que definimos
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val showBottomNav = currentRoute != Screen.Camera.route

                    Scaffold(
                        bottomBar = {
                            if (showBottomNav) {
                                BottomNavigationBar(navController = navController)
                            }
                        }
                    ) { paddingValues ->
                        val bottomPadding = if (showBottomNav) paddingValues else androidx.compose.foundation.layout.PaddingValues(0.dp)

                        Box(modifier = Modifier.padding(bottomPadding)) {
                            NavHost(
                                navController = navController,
                                startDestination = Screen.Home.route
                            ) {
                                composable(Screen.Home.route) { HomeScreen(navController) }
                                composable(Screen.Camera.route) { CameraScreen(navController) }
                                composable(Screen.Profile.route) { ProfileScreen() }

                                composable("preview_screen/{photoUri}") { backStackEntry ->
                                    val encodedUri = backStackEntry.arguments?.getString("photoUri") ?: ""
                                    val decodedUri = java.net.URLDecoder.decode(encodedUri, "UTF-8")
                                    com.garbia.app.ui.screens.PhotoPreviewScreen(navController, decodedUri)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}