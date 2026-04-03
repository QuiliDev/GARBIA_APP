package com.garbia.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// --- MODELOS ---
data class Tip(
    val title: String,
    val shortDesc: String,
    val fullDesc: String,
    val category: String,
    val icon: ImageVector,
    val color: Color,
    val impact: String  // "Alto" | "Medio" | "Bajo"
)

// --- DATOS ESTÁTICOS ---
private val tipsData = listOf(
    // Plástico
    Tip("Aplasta antes de tirar",    "Reduce volumen hasta un 70%",
        "Aplastar botellas y envases de plástico antes de depositarlos en el contenedor amarillo ahorra hasta un 70% de espacio, permitiendo recoger más residuos por viaje y reduciendo las emisiones del transporte.",
        "Plástico", Icons.Outlined.Compress, Color(0xFFFFC107), "Alto"),
    Tip("Retira los tapones",        "Son de un plástico diferente",
        "Los tapones suelen ser de polipropileno (PP), mientras que la botella es PET. Sepáralos y deposítalos por separado. Algunas marcas ya los recogen en campañas solidarias.",
        "Plástico", Icons.Outlined.Layers, Color(0xFFFF9800), "Medio"),
    Tip("Solo envases limpios",      "Un envase sucio contamina todo el lote",
        "Basta con un enjuague rápido con agua fría. No es necesario lavar a fondo. Un envase sucio de grasa o restos de comida puede inutilizar todo el lote de reciclaje al que llega.",
        "Plástico", Icons.Outlined.WaterDrop, Color(0xFF2979FF), "Alto"),
    Tip("Lee el número del triángulo", "Indica el tipo exacto de plástico",
        "El número dentro del triángulo de flechas va del 1 al 7. PET (1) y HDPE (2) son los más reciclados. El 6 (poliestireno) y el 7 (otros) suelen rechazarse en muchos puntos verdes.",
        "Plástico", Icons.Outlined.Info, Color(0xFF9C27B0), "Medio"),

    // Vidrio
    Tip("El vidrio es eterno",       "Reciclable infinitas veces sin perder calidad",
        "El vidrio es uno de los pocos materiales que se puede reciclar de forma indefinida sin ninguna pérdida de calidad o pureza. Cada tonelada de vidrio reciclado ahorra 1.2 toneladas de materias primas.",
        "Vidrio", Icons.Outlined.LocalDrink, Color(0xFF00A550), "Alto"),
    Tip("Sin cerámica ni cristal",   "Contaminan el proceso de fundición",
        "La cerámica y el cristal templado (de ventanas o vajillas) tienen un punto de fusión diferente al del vidrio de envases. Si se mezclan, arruinan toda la hornada. Van al punto limpio, no al contenedor verde.",
        "Vidrio", Icons.Outlined.Block, Color(0xFFE53935), "Alto"),
    Tip("Sin importar el color",     "Mezcla todos los colores sin problema",
        "A diferencia de lo que mucha gente cree, puedes mezclar vidrio transparente, verde y ámbar en el mismo contenedor verde. Las plantas de reciclaje los clasifican automáticamente por color.",
        "Vidrio", Icons.Outlined.ColorLens, Color(0xFF039BE5), "Bajo"),

    // Papel
    Tip("Aplana las cajas",          "Evita el efecto palomita que ocupa espacio",
        "Desarmar completamente las cajas de cartón antes de depositarlas permite aprovechar 5 veces más el espacio del contenedor azul. Retira también las grapas metálicas si es posible.",
        "Papel", Icons.Outlined.Layers, Color(0xFF5D4037), "Medio"),
    Tip("El papel sucio no recicla", "La grasa inutiliza las fibras del papel",
        "La pizza, las servilletas grasientas o el papel de cocina manchado no se pueden reciclar porque la grasa impide que las fibras de celulosa se separen correctamente. Van a la basura orgánica o general.",
        "Papel", Icons.Outlined.NoFood, Color(0xFFFF7043), "Alto"),
    Tip("Tetra Pak al amarillo",     "Es una sorpresa común: va con los envases",
        "Los envases Tetra Pak (zumo, leche, sopas) son una mezcla de cartón, plástico y aluminio. Van al contenedor amarillo de envases, no al azul de papel. Ábrelos y aplástalos antes.",
        "Papel", Icons.Outlined.Inventory2, Color(0xFF26A69A), "Medio"),

    // Orgánico
    Tip("Todo resto vegetal sí",     "Piel, semillas, huesos blandos y más",
        "Cáscaras de frutas y verduras, restos de verdura cocinada, posos de café, bolsitas de infusión de papel, servilletas sucias de comida... todo esto pertenece al contenedor marrón de orgánico.",
        "Orgánico", Icons.Outlined.Eco, Color(0xFF8D6E63), "Alto"),
    Tip("Sin bolsas de plástico",    "Dificultan el compostaje industrial",
        "Aunque parezca práctico, no metas los restos orgánicos en bolsas de plástico convencional. Usa bolsas compostables certificadas (llevan el logo de la hoja) o deposita los restos directamente.",
        "Orgánico", Icons.Outlined.Delete, Color(0xFF43A047), "Alto"),
    Tip("Posos de café son oro",     "Fantásticos para el compostaje",
        "Los posos de café son ricos en nitrógeno y son un excelente acelerador del compostaje. Las bolsitas de infusión de papel también van al orgánico. Las de plástico o nylon, a la basura general.",
        "Orgánico", Icons.Outlined.LocalCafe, Color(0xFF6D4C41), "Bajo"),

    // Especiales
    Tip("Pilas al punto limpio",     "Contienen metales altamente tóxicos",
        "Las pilas contienen mercurio, plomo y cadmio. Una sola pila botón puede contaminar 600.000 litros de agua. Deposítalas en los puntos de recogida habilitados en supermercados y grandes superficies.",
        "Especiales", Icons.Outlined.BatteryAlert, Color(0xFFFF6F00), "Alto"),
    Tip("Medicamentos a la farmacia","Sistema SIGRE de recogida específica",
        "Los medicamentos caducados o sobrantes tienen su propio circuito de gestión: los puntos SIGRE ubicados en todas las farmacias españolas. Nunca al inodoro ni a la basura general.",
        "Especiales", Icons.Outlined.MedicalServices, Color(0xFFE91E63), "Alto"),
    Tip("Electrónica al RAEE",       "Residuos de Aparatos Eléctricos y Electrónicos",
        "Móviles, tablets, ordenadores y electrodomésticos van a puntos de recogida RAEE. Muchas tiendas de electrónica están obligadas por ley a recoger el aparato viejo cuando compras uno nuevo.",
        "Especiales", Icons.Outlined.PhoneAndroid, Color(0xFF5C6BC0), "Alto"),
)

private val teal     = Color(0xFF00796B)
private val tealDark = Color(0xFF004D40)

private val categoryList = listOf("Todos", "Plástico", "Vidrio", "Papel", "Orgánico", "Especiales")

private val impactColor = mapOf(
    "Alto"  to Color(0xFF4CAF50),
    "Medio" to Color(0xFFFFC107),
    "Bajo"  to Color(0xFF78909C)
)

// --- PANTALLA ---
@Composable
fun TipsScreen(navController: NavController) {
    var selectedCat by remember { mutableStateOf("Todos") }
    val filtered = if (selectedCat == "Todos") tipsData else tipsData.filter { it.category == selectedCat }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            // ── CABECERA ──────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.linearGradient(listOf(teal, tealDark)),
                            RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                        )
                ) {
                    Box(Modifier.offset(170.dp, (-50).dp).size(200.dp).background(Color.White.copy(.07f), CircleShape))
                    Box(Modifier.offset((-30).dp, 60.dp).size(140.dp).background(Color.White.copy(.05f), CircleShape))

                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Outlined.ArrowBackIosNew, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Icon(Icons.Outlined.Lightbulb, null, tint = Color(0xFFFFC107), modifier = Modifier.size(26.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Tips de Reciclaje", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.padding(start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Info, null, tint = Color.White.copy(.7f), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Toca cada tarjeta para ampliar el consejo",
                                color = Color.White.copy(.75f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // ── FILTROS ───────────────────────────────────────────────
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categoryList) { cat ->
                        val selected = selectedCat == cat
                        Surface(
                            modifier = Modifier
                                .clickable { selectedCat = cat }
                                .border(1.dp,
                                    if (selected) teal else MaterialTheme.colorScheme.outline.copy(.3f),
                                    RoundedCornerShape(50)),
                            shape = RoundedCornerShape(50),
                            color = if (selected) teal else MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                cat, fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface.copy(.7f),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // ── CONTADOR ──────────────────────────────────────────────
            item {
                Text(
                    "${filtered.size} consejos",
                    fontSize = 13.sp, color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }

            // ── TARJETAS TIP ──────────────────────────────────────────
            items(filtered) { tip ->
                TipCard(tip)
                Spacer(Modifier.height(12.dp))
            }

            item { Spacer(Modifier.height(120.dp)) }
        }
    }
}

// --- TARJETA TIP (expandible) ---
@Composable
private fun TipCard(tip: Tip) {
    var expanded by remember { mutableStateOf(false) }
    val iColor = impactColor[tip.impact] ?: Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { expanded = !expanded }
            .animateContentSize(animationSpec = spring()),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icono
                Box(
                    Modifier.size(50.dp).background(tip.color.copy(.12f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(tip.icon, null, tint = tip.color, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(tip.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(3.dp))
                    Text(tip.shortDesc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(.55f))
                }
                // Icono expandir
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Badges: categoría + impacto
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(color = tip.color.copy(.1f), shape = RoundedCornerShape(6.dp)) {
                    Text(tip.category, fontSize = 10.sp, color = tip.color, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
                Surface(color = iColor.copy(.12f), shape = RoundedCornerShape(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(iColor))
                        Spacer(Modifier.width(4.dp))
                        Text("Impacto ${tip.impact}", fontSize = 10.sp, color = iColor, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Contenido expandido
            if (expanded) {
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = tip.color.copy(.15f))
                Spacer(Modifier.height(12.dp))
                Text(
                    tip.fullDesc,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(.75f),
                    lineHeight = 21.sp
                )
            }
        }
    }
}
