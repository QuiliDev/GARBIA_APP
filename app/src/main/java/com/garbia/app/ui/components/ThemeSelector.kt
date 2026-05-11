package com.garbia.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.garbia.app.ui.theme.AppThemeColor

@Composable
fun ThemeSelectionDialog(
    currentTheme: AppThemeColor,
    onDismiss: () -> Unit,
    onThemeSelected: (AppThemeColor) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Elige tu estilo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Fila de colores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ThemeOption(color = Color(0xFF00A550), name = "Eco", isSelected = currentTheme == AppThemeColor.GREEN) { onThemeSelected(AppThemeColor.GREEN) }
                    ThemeOption(color = Color(0xFF6200EE), name = "Lila", isSelected = currentTheme == AppThemeColor.PURPLE) { onThemeSelected(AppThemeColor.PURPLE) }
                    ThemeOption(color = Color(0xFF2979FF), name = "Azul", isSelected = currentTheme == AppThemeColor.BLUE) { onThemeSelected(AppThemeColor.BLUE) }
                    ThemeOption(color = Color(0xFFFF6F00), name = "Dark", isSelected = currentTheme == AppThemeColor.ORANGE_DARK) { onThemeSelected(AppThemeColor.ORANGE_DARK) }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun ThemeOption(color: Color, name: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(color)
                .clickable { onClick() }
                .then(
                    if (isSelected) Modifier.border(4.dp, MaterialTheme.colorScheme.onSurface, CircleShape) else Modifier
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}