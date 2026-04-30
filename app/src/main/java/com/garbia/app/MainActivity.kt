package com.garbia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIntoContainer
import androidx.compose.animation.slideOutOfContainer
import androidx.compose.animation.AnimatedContentTransitionScope
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.garbia.app.ui.viewmodel.OnboardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GarbiaAppMain()
        }
    }
}

// -----------------------------------------------------------------------------
// 1. GARBIA APP: Maneja el Tema, el Onboarding y el contenedor principal
// -----------------------------------------------------------------------------
@Composable
fun GarbiaAppMain(
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val hasSeenOnboarding by onboardingViewModel.hasSeenOnboarding.collectAsStateWithLifecycle()
    var currentTheme by remember { mutableStateOf(AppThemeColor.GREEN) }

    GarbiaAppTheme(themeColor = currentTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Espera a que DataStore cargue antes de navegar
            if (hasSeenOnboarding == null) return@Surface

            val startRoute = if (hasSeenOnboarding == true) Screen.Home.route else Screen.Onboarding.route
            MainScreen(
                startRoute = startRoute,
                currentTheme = currentTheme,
                onThemeChanged = { currentTheme = it }
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 2. MAIN SCREEN: Estructura (Scaffold), Barras y Lógica de UI
// -----------------------------------------------------------------------------
@Composable
fun MainScreen(
    startRoute: String,
    currentTheme: AppThemeColor,
    onThemeChanged: (AppThemeColor) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showTopBar = currentRoute?.startsWith("result_screen") == true
    val showBottomNav = currentRoute == Screen.Home.route ||
            currentRoute == Screen.Profile.route ||
            currentRoute == Screen.Ranking.route ||
            currentRoute == Screen.Premios.route ||
            currentRoute == Screen.Tips.route ||
            currentRoute == Screen.Mapas.route ||
            currentRoute?.startsWith("result_screen") == true

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

        val topPadding = if (currentRoute == Screen.Camera.route) 0.dp else innerPadding.calculateTopPadding()
        val bottomPadding = when {
            currentRoute?.startsWith("result_screen") == true -> innerPadding.calculateBottomPadding()
            showBottomNav -> 0.dp
            else -> innerPadding.calculateBottomPadding()
        }

        GarbiaNav(
            navController = navController,
            startDestination = startRoute,
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
    startDestination: String,
    modifier: Modifier = Modifier,
    currentTheme: AppThemeColor,
    onThemeChanged: (AppThemeColor) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) +
                    fadeIn(tween(300))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) +
                    fadeOut(tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) +
                    fadeIn(tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) +
                    fadeOut(tween(300))
        }
    ) {
        // ONBOARDING
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }

        // 1. HOME SCREEN
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        // 2. PROFILE SCREEN
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController  = navController,
                currentTheme   = currentTheme,
                onThemeChanged = onThemeChanged
            )
        }

        // LOGROS
        composable(Screen.Logros.route) {
            LogrosScreen(navController)
        }

        // ESTADÍSTICAS
        composable(Screen.Estadisticas.route) {
            EstadisticasScreen(navController)
        }

        // HISTORIAL COMPLETO
        composable(Screen.Historial.route) {
            HistorialScreen(navController)
        }

        // EDITAR PERFIL
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController)
        }

        // 3. CÁMARA
        composable(Screen.Camera.route) {
            CameraScreen(navController)
        }

        // 7. RANKING
        composable(Screen.Ranking.route) { RankingScreen(navController) }

        // 8. PREMIOS
        composable(Screen.Premios.route) { PremiosScreen(navController) }

        // 9. TIPS
        composable(Screen.Tips.route) { TipsScreen(navController) }

        // 10. MAPAS
        composable(Screen.Mapas.route) { MapasScreen(navController) }

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
