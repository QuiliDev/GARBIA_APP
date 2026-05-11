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
import androidx.compose.ui.unit.sp
import com.garbia.app.ui.Screen
import com.garbia.app.ui.components.*
import com.garbia.app.ui.theme.AppThemeColor
import com.garbia.app.ui.viewmodel.AuthViewModel
import com.garbia.app.ui.viewmodel.EstadoAuth
import com.garbia.app.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    currentTheme: AppThemeColor,
    onThemeChanged: (AppThemeColor) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val usuario      by viewModel.usuario.collectAsStateWithLifecycle()
    val estadoAuth   by authViewModel.estado.collectAsStateWithLifecycle()
    val scrollState  = rememberScrollState()
    var showThemeDialog      by remember { mutableStateOf(false) }
    var showAyudaDialog      by remember { mutableStateOf(false) }
    var showPrivacidadDialog by remember { mutableStateOf(false) }

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

    if (showAyudaDialog) {
        AlertDialog(
            onDismissRequest = { showAyudaDialog = false },
            confirmButton = {
                TextButton(onClick = { showAyudaDialog = false }) { Text("Entendido") }
            },
            title = { Text("Preguntas frecuentes", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("¿Cómo funciona el escáner?\nApunta la cámara a un residuo y Garbia lo clasifica con IA.", fontSize = 14.sp)
                    Text("¿Qué son los puntos?\nCada escaneo exitoso suma puntos según el tipo de residuo.", fontSize = 14.sp)
                    Text("¿Cómo mejoro mi nivel?\nAcumula puntos escaneando residuos a diario.", fontSize = 14.sp)
                    Text("¿Mis datos se sincronizan?\nSí, si tienes conexión tus datos se guardan en la nube.", fontSize = 14.sp)
                }
            }
        )
    }

    if (showPrivacidadDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacidadDialog = false },
            confirmButton = {
                TextButton(onClick = { showPrivacidadDialog = false }) { Text("Cerrar") }
            },
            title = { Text("Política de privacidad", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Garbia recopila únicamente los datos necesarios para ofrecerte la experiencia de reciclaje.", fontSize = 14.sp)
                    Text("• Fotos: procesadas para identificar el residuo, no se almacenan en servidores.", fontSize = 14.sp)
                    Text("• Puntos e historial: guardados en tu dispositivo y sincronizados de forma anónima.", fontSize = 14.sp)
                    Text("• No compartimos datos con terceros ni los vendemos.", fontSize = 14.sp)
                }
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
                        icon      = Icons.Outlined.Edit,
                        title     = "Editar perfil",
                        subtitle  = "Cambiar nombre de usuario",
                        iconColor = Color(0xFF9C27B0),
                        onClick   = { navController.navigate(Screen.EditProfile.route) }
                    )
                    Divider(modifier = Modifier.padding(start = 56.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ProfileOptionItem(
                        icon      = Icons.Outlined.History,
                        title     = "Mi Historial",
                        subtitle  = "Todos tus escaneos",
                        iconColor = Color(0xFF00BCD4),
                        onClick   = { navController.navigate(Screen.Historial.route) }
                    )
                    Divider(modifier = Modifier.padding(start = 56.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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

                SectionTitle("Cuenta")
                ProfileMenuCard {
                    when (estadoAuth) {
                        is EstadoAuth.Autenticado -> {
                            val uid = (estadoAuth as EstadoAuth.Autenticado).uid
                            ProfileOptionItem(
                                icon      = Icons.Outlined.CloudDone,
                                title     = "Sincronizado con Firebase",
                                subtitle  = "UID: ${uid.take(12)}…",
                                iconColor = Color(0xFF4CAF50),
                                onClick   = {}
                            )
                            Divider(modifier = Modifier.padding(start = 56.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            ProfileOptionItem(
                                icon      = Icons.Outlined.Logout,
                                title     = "Cerrar sesión Firebase",
                                subtitle  = "Volver a modo local",
                                iconColor = MaterialTheme.colorScheme.error,
                                onClick   = { authViewModel.cerrarSesion() }
                            )
                        }
                        EstadoAuth.Cargando -> {
                            Row(modifier = Modifier.padding(16.dp)) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("Conectando con Firebase…", fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        EstadoAuth.Local -> {
                            ProfileOptionItem(
                                icon      = Icons.Outlined.CloudOff,
                                title     = "Modo local",
                                subtitle  = "Datos guardados solo en este dispositivo",
                                iconColor = Color(0xFF9E9E9E),
                                onClick   = {}
                            )
                        }
                    }
                }

                SectionTitle("Soporte")
                ProfileMenuCard {
                    ProfileOptionItem(
                        icon      = Icons.Outlined.Help,
                        title     = "Ayuda",
                        subtitle  = "Preguntas frecuentes",
                        iconColor = Color(0xFF2196F3),
                        onClick   = { showAyudaDialog = true }
                    )
                    Divider(modifier = Modifier.padding(start = 56.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ProfileOptionItem(
                        icon      = Icons.Outlined.PrivacyTip,
                        title     = "Privacidad",
                        subtitle  = "Política de privacidad",
                        iconColor = Color(0xFF4CAF50),
                        onClick   = { showPrivacidadDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Button(
                        onClick = { authViewModel.cerrarSesion() },
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
