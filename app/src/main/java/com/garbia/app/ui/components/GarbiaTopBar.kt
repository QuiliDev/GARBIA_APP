package com.garbia.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarbiaTopBar(
    navController: NavController,
    isOverDarkBackground: Boolean = false
) {
    // 1. LÓGICA DE COLORES DEL TEMA
    val primaryColor = MaterialTheme.colorScheme.primary

    // Si el fondo es oscuro (Home), usamos BLANCO.
    // Si el fondo es claro, usamos el COLOR DEL TEMA (Naranja/Verde...)
    val contentColor = if (isOverDarkBackground) Color.White else primaryColor

    // Fondo de la cápsula: Cristal blanco en Home, o Tinte del tema en otras pantallas
    val capsuleColor = if (isOverDarkBackground) {
        Color.White.copy(alpha = 0.15f)
    } else {
        primaryColor.copy(alpha = 0.08f)
    }

    // Borde sutil
    val borderColor = if (isOverDarkBackground) {
        Color.White.copy(alpha = 0.3f)
    } else {
        primaryColor.copy(alpha = 0.2f)
    }

    CenterAlignedTopAppBar(
        title = { },

        // --- IZQUIERDA: LOGO DE MARCA ---
        navigationIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 24.dp)
                    .clickable {
                        // Clic en el logo te lleva al inicio
                        navController.navigate("home_screen") {
                            popUpTo("home_screen") { inclusive = true }
                        }
                    }
            ) {
                // Icono Hoja (Eco)
                Icon(
                    imageVector = Icons.Outlined.Eco,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Texto GarbIA
                Text(
                    text = "GarbIA",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                    color = contentColor,
                    // Kerning negativo para que las letras estén juntitas y modernas
                    letterSpacing = (-1).sp
                )
            }
        },

        // --- DERECHA: CÁPSULA UNIFICADA ---
        actions = {
            Surface(
                color = capsuleColor,
                shape = CircleShape, // Totalmente redondo
                border = BorderStroke(1.dp, borderColor), // Borde fino
                modifier = Modifier.padding(end = 24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    // 1. CAMPANA
                    Box {
                        IconButton(
                            onClick = { /* Abrir notificaciones */ },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notificaciones",
                                tint = contentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        // Punto rojo decorativo
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(8.dp)
                                .background(Color(0xFFFF5252), CircleShape)
                        )
                    }

                    // Línea divisoria vertical
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(16.dp)
                            .background(contentColor.copy(alpha = 0.3f))
                    )

                    // 2. PERFIL
                    IconButton(
                        onClick = {
                            navController.navigate("profile_screen") { launchSingleTop = true }
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(contentColor.copy(alpha = 0.2f)), // Fondo del iconito
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = contentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        modifier = Modifier.statusBarsPadding()
    )
}