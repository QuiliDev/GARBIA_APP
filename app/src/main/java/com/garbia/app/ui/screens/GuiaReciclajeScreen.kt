package com.garbia.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private data class ContenedorInfo(
    val emoji: String,
    val nombre: String,
    val color: Color,
    val descripcion: String,
    val materiales: List<Pair<String, String>>  // nombre, ejemplo
)

private val CONTENEDORES = listOf(
    ContenedorInfo(
        emoji = "🟡", nombre = "Amarillo — Envases", color = Color(0xFFFFC107),
        descripcion = "Envases de plástico, metal y bricks de bebida.",
        materiales = listOf(
            "Botellas de agua y refresco" to "PET, PEAD",
            "Film plástico" to "Bolsas de supermercado",
            "Bricks" to "Zumo, leche, batidos",
            "Latas" to "Refrescos, conservas",
            "Botes metálicos" to "Spray, desodorante",
            "Envases de yogur" to "Polipropileno PP",
            "Tapones de corcho/plástico" to "De botellas",
            "Bandejas de porexpán" to "Carne, pescado (limpias)"
        )
    ),
    ContenedorInfo(
        emoji = "🔵", nombre = "Azul — Papel y Cartón", color = Color(0xFF2196F3),
        descripcion = "Papel, cartón y derivados en buen estado.",
        materiales = listOf(
            "Periódicos y revistas" to "Sin plastificar",
            "Cajas de cartón" to "Pizza, cereales, zapatos",
            "Papel de oficina" to "Folios, cuadernos",
            "Bolsas de papel" to "Comercios, panaderías",
            "Envases de cartón" to "Hueveras, tubos",
            "Papel de regalo" to "Sin brillos ni láminas",
            "Tetra Brik vacío" to "Aplastado y limpio",
            "Catálogos y folletos" to "Sin espiral metálica"
        )
    ),
    ContenedorInfo(
        emoji = "🟢", nombre = "Verde — Vidrio", color = Color(0xFF4CAF50),
        descripcion = "Envases de vidrio vacíos y limpios.",
        materiales = listOf(
            "Botellas de vino/cava" to "Limpias, sin tapón",
            "Botellas de cerveza" to "Limpias",
            "Tarros de mermelada" to "Sin tapa metálica",
            "Frascos de salsas" to "Vacíos y aclarados",
            "Botellas de licor" to "Cualquier tamaño",
            "Frascos de perfume" to "Vacíos"
        )
    ),
    ContenedorInfo(
        emoji = "🟤", nombre = "Marrón — Orgánico", color = Color(0xFF795548),
        descripcion = "Restos de comida y materia orgánica.",
        materiales = listOf(
            "Restos de comida" to "Cocida o cruda",
            "Cáscaras de fruta" to "Naranja, plátano…",
            "Posos de café" to "Y filtros de papel",
            "Cáscaras de huevo" to "",
            "Restos de jardín" to "Hierba, hojas, flores",
            "Servilletas usadas" to "Papel manchado de comida",
            "Pan duro" to "Sin envoltorio"
        )
    ),
    ContenedorInfo(
        emoji = "⚫", nombre = "Gris/Negro — Resto", color = Color(0xFF607D8B),
        descripcion = "Todo lo que no tiene contenedor específico.",
        materiales = listOf(
            "Cerámicas y porcelana" to "Tazas, platos rotos",
            "Colillas" to "Sin agua",
            "Pañales" to "Higiénicos",
            "Ropa y textil usado" to "Van a punto limpio",
            "Papel higiénico" to "Y toallitas húmedas",
            "Chicles" to "",
            "Fotografías" to "Papel fotográfico",
            "Radiografías" to ""
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuiaReciclajeScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var expandido by remember { mutableStateOf<String?>(null) }

    val contenedoresFiltrados = remember(query) {
        if (query.isBlank()) CONTENEDORES
        else CONTENEDORES.filter { c ->
            c.nombre.contains(query, ignoreCase = true) ||
                    c.materiales.any { (mat, ej) ->
                        mat.contains(query, ignoreCase = true) ||
                                ej.contains(query, ignoreCase = true)
                    }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guía de Reciclaje", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value         = query,
                    onValueChange = { query = it },
                    placeholder   = { Text("Buscar material…") },
                    leadingIcon   = { Icon(Icons.Outlined.Search, null) },
                    trailingIcon  = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Outlined.Close, null)
                            }
                        }
                    },
                    singleLine  = true,
                    shape       = RoundedCornerShape(14.dp),
                    modifier    = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }

            if (contenedoresFiltrados.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sin resultados para \"$query\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(contenedoresFiltrados, key = { it.nombre }) { contenedor ->
                    ContenedorCard(
                        contenedor = contenedor,
                        query      = query,
                        expandido  = expandido == contenedor.nombre,
                        onToggle   = {
                            expandido = if (expandido == contenedor.nombre) null else contenedor.nombre
                        }
                    )
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun ContenedorCard(
    contenedor: ContenedorInfo,
    query: String,
    expandido: Boolean,
    onToggle: () -> Unit
) {
    val materialesMostrados = if (query.isBlank()) contenedor.materiales
    else contenedor.materiales.filter { (mat, ej) ->
        mat.contains(query, ignoreCase = true) || ej.contains(query, ignoreCase = true)
    }

    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Cabecera clicable
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(contenedor.color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(contenedor.emoji, fontSize = 24.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(contenedor.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        contenedor.descripcion,
                        fontSize = 12.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )
                }
                Icon(
                    if (expandido) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = contenedor.color
                )
            }

            // Lista animada de materiales
            AnimatedVisibility(
                visible = expandido || query.isNotBlank(),
                enter   = expandVertically(tween(250)),
                exit    = shrinkVertically(tween(200))
            ) {
                Column(
                    modifier = Modifier
                        .background(contenedor.color.copy(alpha = 0.05f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    HorizontalDivider(color = contenedor.color.copy(alpha = 0.2f))
                    Spacer(Modifier.height(8.dp))
                    materialesMostrados.forEach { (material, ejemplo) ->
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                Modifier
                                    .padding(top = 6.dp)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(contenedor.color)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(material, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                if (ejemplo.isNotBlank()) {
                                    Text(
                                        ejemplo, fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}
