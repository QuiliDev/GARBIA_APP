package com.garbia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.garbia.app.ui.Screen
import com.garbia.app.ui.components.*
import com.garbia.app.ui.theme.AppThemeColor
import com.garbia.app.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    currentTheme: AppThemeColor,
    onThemeChanged: (AppThemeColor) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val usuario by viewModel.usuario.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = currentTheme,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { newTheme ->
                onThemeChanged(newTheme)
                showThemeDialog = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // 1. CABECERA
            ProfileHeader(
                username = usuario?.nombre ?: "Usuario",
                level    = usuario?.nivelLabel ?: "Nivel 1: Novato"
            )

            // 2. ESTADÍSTICAS
            ProfileStatsCard(
                puntos   = usuario?.puntosTotales ?: 0,
                escaneos = usuario?.escaneosTotales ?: 0,
                co2      = usuario?.co2Ahorrado ?: 0f,
                modifier = Modifier
                    .offset(y = (-50).dp)
                    .padding(horizontal = 24.dp)
            )

            // 3. MENÚ DE OPCIONES
            Column(modifier = Modifier.offset(y = (-30).dp)) {

                SectionTitle("General")
                ProfileMenuCard {
                    ProfileOptionItem(
                        icon      = Icons.Outlined.EmojiEvents,
                        title     = "Mis Logros",
                        subtitle  = "Badges y recompensas",
                        iconColor = Color(0xFFFFC107),
                        onClick   = { navController.navigate(Screen.Logros.route) }
                    )
                    Divider(modifier = Modifier.padding(start = 56.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ProfileOptionItem(
                        icon      = Icons.Outlined.Palette,
                        title     = "Apariencia",
                        subtitle  = "Cambiar tema de color",
                        iconColor = Color(0xFFE91E63),
                        onClick   = { showThemeDialog = true }
                    )
                    Divider(modifier = Modifier.padding(start = 56.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ProfileOptionItem(
                        icon      = Icons.Outlined.Notifications,
                        title     = "Notificaciones",
                        subtitle  = "Gestor de alertas",
                        iconColor = Color(0xFFFF9800),
                        onClick   = { }
                    )
                }

                SectionTitle("Progreso")
                ProfileMenuCard {
                    ProfileOptionItem(
                        icon      = Icons.Outlined.BarChart,
                        title     = "Estadísticas",
                        subtitle  = "Actividad de los últimos 7 días",
                        iconColor = Color(0xFF3F51B5),
                        onClick   = { navController.navigate(Screen.Estadisticas.route) }
                    )
                }

                SectionTitle("Soporte")
                ProfileMenuCard {
                    ProfileOptionItem(
                        icon      = Icons.Outlined.Help,
                        title     = "Ayuda",
                        subtitle  = "Preguntas frecuentes",
                        iconColor = Color(0xFF2196F3),
                        onClick   = { }
                    )
                    Divider(modifier = Modifier.padding(start = 56.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ProfileOptionItem(
                        icon      = Icons.Outlined.PrivacyTip,
                        title     = "Privacidad",
                        subtitle  = null,
                        iconColor = Color(0xFF4CAF50),
                        onClick   = { }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Button(
                        onClick = { /* Logout logic */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor   = MaterialTheme.colorScheme.error
                        ),
                        shape    = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(Icons.Outlined.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}
