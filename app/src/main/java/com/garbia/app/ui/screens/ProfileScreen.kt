package com.garbia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garbia.app.ui.theme.AppThemeColor

@Composable
fun ProfileScreen(
    currentTheme: AppThemeColor,
    onThemeChanged: (AppThemeColor) -> Unit
) {
    // Usamos un Scroll vertical por si la pantalla es pequeña
    val scrollState = rememberScrollState()
    var showThemeDialog by remember { mutableStateOf(false) }
    if (showThemeDialog) {
        com.garbia.app.ui.components.ThemeSelectionDialog(
            currentTheme = currentTheme,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { newTheme ->
                onThemeChanged(newTheme)
                showThemeDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // Importante: Usamos el color de fondo del tema (blanco o negro)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // 1. CABECERA (Usuario y Puntos Totales)
        ProfileHeader()

        Spacer(modifier = Modifier.height(24.dp))

        // 2. SECCIÓN DE ESTADÍSTICAS (Dashboard)
        StatsSection()

        Spacer(modifier = Modifier.height(24.dp))

        // 3. MENÚ DE AJUSTES
        SettingsSection(onOpenThemeSelector = { showThemeDialog = true })

        // Espacio extra al final para que no se pegue a la barra de navegación
        Spacer(modifier = Modifier.height(100.dp))
    }
}

// --- COMPONENTE 1: CABECERA ---
@Composable
fun ProfileHeader() {
    val mainColor = MaterialTheme.colorScheme.primary
    val onMainColor = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                color = mainColor,
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // --- NUEVO CONTENEDOR "PADRE" (Sin recorte) ---
            Box(contentAlignment = Alignment.BottomEnd) {

                // CAPA 1: La Foto de Perfil (Esta SÍ se recorta en círculo)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape) // Recorte solo a la foto
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto de perfil",
                        tint = onMainColor,
                        modifier = Modifier.size(60.dp)
                    )
                }

                // CAPA 2: El Botón de Editar (Flotando ENCIMA, sin recorte)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        // Le añadimos un pequeño desplazamiento u offset si quieres ajustarlo más
                        // .offset(x = 4.dp, y = 4.dp)
                        .clip(CircleShape)
                        .background(onMainColor) // Fondo Blanco
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = mainColor // Lápiz Naranja
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del Usuario
            Text(
                text = "Garbiñe López",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = onMainColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta de Puntos Totales
            Card(
                colors = CardDefaults.cardColors(containerColor = onMainColor),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MonetizationOn,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "1.250 Puntos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = mainColor
                    )
                }
            }
        }
    }
}

// --- COMPONENTE 2: ESTADÍSTICAS ---
@Composable
fun StatsSection() {
    Padding(padding = PaddingValues(horizontal = 24.dp)) {
        Column {
            Text(
                text = "Tu Impacto",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp) // Este espacio ahora sí funcionará
            )

            // Fila de 3 tarjetas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard(icon = Icons.Outlined.LocalDrink, count = "85", label = "Vidrio", color = Color(0xFF00A550))
                StatCard(icon = Icons.Outlined.ShoppingBag, count = "120", label = "Plástico", color = Color(0xFFFFC107))
                StatCard(icon = Icons.Outlined.Description, count = "45", label = "Papel", color = Color(0xFF2979FF))
            }
        }
    }
}

// Sub-componente para una tarjeta de estadística individual
@Composable
fun StatCard(icon: ImageVector, count: String, label: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

// --- COMPONENTE 3: AJUSTES (Menú) ---
@Composable
fun SettingsSection(onOpenThemeSelector: () -> Unit) {
    Padding(padding = PaddingValues(horizontal = 24.dp)) {
        Text(
            text = "Ajustes",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {

                SettingsItem(icon = Icons.Outlined.Palette, title = "Apariencia / Tema") { onOpenThemeSelector() }
                Divider(color = Color.Gray.copy(alpha = 0.1f))
                SettingsItem(icon = Icons.Outlined.Notifications, title = "Notificaciones") { }
                Divider(color = Color.Gray.copy(alpha = 0.1f))
                SettingsItem(icon = Icons.Outlined.Help, title = "Ayuda y Soporte") { }
                Divider(color = Color.Gray.copy(alpha = 0.1f))
                SettingsItem(icon = Icons.Outlined.Logout, title = "Cerrar Sesión", isDestructive = true) { }
            }
        }
    }

}

// Sub-componente para una fila del menú
@Composable
fun SettingsItem(icon: ImageVector, title: String, isDestructive: Boolean = false, onClick: () -> Unit) {
    val contentColor = if (isDestructive) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = contentColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 16.sp, color = contentColor, modifier = Modifier.weight(1f))
        if (!isDestructive) {
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

// Función de ayuda rápida para el padding
@Composable
fun Padding(padding: PaddingValues, content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(padding)) {
        content()
    }
}