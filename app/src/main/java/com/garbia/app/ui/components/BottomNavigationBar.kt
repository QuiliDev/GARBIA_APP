package com.garbia.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.garbia.app.ui.Screen
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection


// Asegúrate de tener este import arriba del todo para la sombra:
import androidx.compose.ui.draw.shadow

@Composable
fun BottomNavigationBar(navController: NavController) {
    val mainColor = MaterialTheme.colorScheme.primary
    val inactiveColor = Color.Gray

    // --- CONFIGURACIÓN DE TAMAÑOS ---
    val fabSize = 64.dp
    val margin = 8.dp
    val cutoutRadius = (fabSize / 2) + margin
    val barHeight = 80.dp
    val fabOffset = -(barHeight - (fabSize / 2))

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Guardamos la forma en una variable para aplicarla tanto a la Surface como a la sombra
    val navShape = FilletedCircleCutoutShape(
        cutoutRadius = cutoutRadius,
        filletRadius = 12.dp,
        cornerRadius = 32.dp  // Nota: Lo he bajado a 32dp. 48dp a veces es demasiado grande y deforma pantallas estrechas.
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // 1. LA BARRA BLANCA
        Surface(
            shape = navShape,
            color = MaterialTheme.colorScheme.surface,
            // MAGIA DE LA SOMBRA: Usamos modifier.shadow en lugar de shadowElevation
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .shadow(
                    elevation = 30.dp,        // la sombra
                    shape = navShape,
                    ambientColor = Color.Black, // sombra ambiental oscura
                    spotColor = Color.Black,    // Sombra principal fuerte
                    clip = false
                )
        ) {
            BottomAppBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                // ICONO HOME
                IconButton(
                    onClick = {
                        if (currentRoute != Screen.Home.route) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Inicio",
                        modifier = Modifier.size(30.dp),
                        tint = if (currentRoute == Screen.Home.route) mainColor else inactiveColor
                    )
                }

                Spacer(modifier = Modifier.weight(1.5f))

                // ICONO PERFIL (Con navegación restaurada)
                IconButton(
                    onClick = {
                        if (currentRoute != Screen.Profile.route) {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        modifier = Modifier.size(30.dp),
                        tint = if (currentRoute == Screen.Profile.route) mainColor else inactiveColor
                    )
                }
            }
        }

        // 2. EL BOTÓN FLOTANTE
        CameraFloatingButton(
            navController = navController,
            modifier = Modifier
                .offset(y = fabOffset)
                .size(fabSize)
        )
    }
}

// --- CLASE DEFINITIVA: RECORTE CON "FILLETS" (Esquinas interiores y exteriores redondeadas) ---
class FilletedCircleCutoutShape(
    private val cutoutRadius: Dp,       // Radio del hueco para el botón
    private val filletRadius: Dp = 16.dp, // Radio de las esquinas interiores (junto al botón)
    private val cornerRadius: Dp = 16.dp  // Radio de las esquinas exteriores (laterales del móvil)
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val r = with(density) { cutoutRadius.toPx() }
        val f = with(density) { filletRadius.toPx() }
        val cr = with(density) { cornerRadius.toPx() }

        val barWidth = size.width
        val barHeight = size.height
        val cX = barWidth / 2f

        // Calcula exactamente dónde deben tocarse los círculos
        val dx = kotlin.math.sqrt(r * r + 2 * r * f)
        val theta = Math.toDegrees(kotlin.math.atan2(f.toDouble(), dx.toDouble())).toFloat()

        val path = Path().apply {
            // 1. Esquina superior izquierda (Exterior)
            arcTo(
                rect = Rect(center = Offset(cr, cr), radius = cr),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // 2. Esquina interior izquierda (El "Fillet" junto al botón)
            arcTo(
                rect = Rect(center = Offset(cX - dx, f), radius = f),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f - theta,
                forceMoveTo = false
            )

            // 3. El Hueco Central (Cutout)
            arcTo(
                rect = Rect(center = Offset(cX, 0f), radius = r),
                startAngleDegrees = 180f - theta,
                sweepAngleDegrees = 2f * theta - 180f,
                forceMoveTo = false
            )

            // 4. Esquina interior derecha (El "Fillet" junto al botón)
            arcTo(
                rect = Rect(center = Offset(cX + dx, f), radius = f),
                startAngleDegrees = 180f + theta,
                sweepAngleDegrees = 90f - theta,
                forceMoveTo = false
            )

            // 5. Esquina superior derecha (Exterior)
            arcTo(
                rect = Rect(center = Offset(barWidth - cr, cr), radius = cr),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // 6. Cerramos la barra por la parte de abajo
            lineTo(barWidth, barHeight)
            lineTo(0f, barHeight)
            close()
        }
        return Outline.Generic(path)
    }
}
