package com.garbia.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.garbia.app.ui.Screen
import com.garbia.app.ui.viewmodel.SetupNombreViewModel

@Composable
fun SetupNombreScreen(
    navController: NavController,
    viewModel: SetupNombreViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    fun confirmar() {
        if (nombre.isBlank()) return
        viewModel.guardarNombre(nombre) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.SetupNombre.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1B5E20), Color(0xFF002904))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("👋", fontSize = 80.sp)

            Spacer(Modifier.height(32.dp))

            Text(
                "¿Cómo te llamas?",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Usaremos tu nombre para personalizar tu experiencia de reciclaje.",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(40.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { if (it.length <= 30) nombre = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = {
                    Text("Tu nombre…", color = Color.White.copy(alpha = 0.5f))
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor        = Color.White,
                    unfocusedTextColor      = Color.White,
                    focusedBorderColor      = Color.White,
                    unfocusedBorderColor    = Color.White.copy(alpha = 0.4f),
                    cursorColor             = Color.White,
                    focusedContainerColor   = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction      = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { confirmar() })
            )

            Text(
                "${nombre.length}/30",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 4.dp)
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick  = ::confirmar,
                enabled  = nombre.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = Color.White,
                    contentColor           = Color(0xFF1B5E20),
                    disabledContainerColor = Color.White.copy(alpha = 0.3f),
                    disabledContentColor   = Color.White.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    "¡Empezar a Reciclar!",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
            }
        }
    }
}
