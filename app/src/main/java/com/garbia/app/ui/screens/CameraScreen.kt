package com.garbia.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import com.garbia.app.R

// RECIBIMOS EL navController
@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasCameraPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        // PASAMOS EL navController AL ESCÁNER
        CameraScannerView(navController)
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(R.string.camera_permission_rationale))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                Text(stringResource(R.string.camera_btn_grant))
            }
        }
    }
}

@Composable
fun CameraScannerView(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }

    // ✅ MAGIA DEL TEMA: Obtenemos el color principal de la app
    val mainColor = MaterialTheme.colorScheme.primary

    // --- NUEVAS VARIABLES DE ESTADO ---
    var camera by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }
    var isFlashOn by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                Log.d("Camara", "Foto de galería seleccionada: $uri")
                val encodedUri = java.net.URLEncoder.encode(uri.toString(), "UTF-8")
                navController.navigate("preview_screen/$encodedUri")
            } else {
                Log.d("Camara", "No se seleccionó ninguna foto")
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // --- CAPA 1 (FONDO): EL VISOR DE LA CÁMARA ---
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    try {
                        cameraProvider.unbindAll()
                        val cam = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                        camera = cam
                    } catch (exc: Exception) {
                        Log.e("CameraScreen", "Error al encender la cámara", exc)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            }
        )

        // --- CAPA 2 (MEDIO): LA MÁSCARA DE ESCÁNER OPACO ---
        ScannerOverlay(
            scanSize = 320.dp,
            cornerRadius = 32.dp
        )

        // --- CAPA 3 (FRENTE): LA INTERFAZ DE USUARIO ---
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. BARRA SUPERIOR (Botón Cerrar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = stringResource(R.string.camera_content_desc_close), tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // 2. TEXTO DE INSTRUCCIONES
            Text(
                text = stringResource(R.string.camera_instruction),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.height(320.dp))
            Spacer(modifier = Modifier.weight(1f))

            // 4. LA BOTONERA INFERIOR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 64.dp, start = 32.dp, end = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // --- BOTÓN IZQUIERDO: GALERÍA ---
                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape).size(56.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = stringResource(R.string.camera_content_desc_gallery), tint = Color.White)
                }

                // --- BOTÓN CENTRAL: DISPARADOR ---
                IconButton(
                    onClick = {
                        val photoFile = java.io.File(
                            context.cacheDir,
                            "GarbiA_scan_${System.currentTimeMillis()}.jpg"
                        )
                        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                        imageCapture.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    val savedUri = outputFileResults.savedUri ?: android.net.Uri.fromFile(photoFile)
                                    val encodedUri = java.net.URLEncoder.encode(savedUri.toString(), "UTF-8")
                                    navController.navigate("preview_screen/$encodedUri")
                                }
                                override fun onError(exception: androidx.camera.core.ImageCaptureException) {
                                    android.widget.Toast.makeText(context, context.getString(R.string.camera_error_capture), android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    },
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().border(4.dp, Color.White, CircleShape).padding(8.dp)) {
                        // ✅ APLICAMOS EL mainColor AL CÍRCULO INTERIOR DEL BOTÓN
                        Box(modifier = Modifier.fillMaxSize().background(mainColor, CircleShape))
                    }
                }

                // --- BOTÓN DERECHO: FLASH (Linterna) ---
                IconButton(
                    onClick = {
                        isFlashOn = !isFlashOn
                        camera?.cameraControl?.enableTorch(isFlashOn)
                    },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape).size(56.dp)
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = stringResource(R.string.camera_content_desc_flash),
                        tint = if (isFlashOn) Color.Yellow else Color.White
                    )
                }
            }
        }
    }
}

// --- COMPONENTE FUSIONADO: CAPA OPACO + ESQUINAS EN "L" REDONDEADAS ---
@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    scanSize: Dp = 320.dp,          // Tamaño del área transparente central
    cornerRadius: Dp = 32.dp,       // Redondeo del agujero y de las esquinas en "L"
    overlayColor: Color = Color.Black.copy(alpha = 0.5f), // Color del fondo opaco
    cornerColor: Color = MaterialTheme.colorScheme.primary, // Color de las esquinas en "L"
    cornerStrokeWidth: Dp = 5.dp,     // Grosor de las líneas de las esquinas
    cornerLength: Dp = 40.dp          // Longitud de las "patitas" de las esquinas
) {
    androidx.compose.foundation.Canvas(modifier = modifier.fillMaxSize()) {
        val scanSizePx = scanSize.toPx()
        val cornerRadiusPx = cornerRadius.toPx()
        val screenWidth = size.width
        val screenHeight = size.height

        // 1. Calcular la posición para centrar el cuadrado
        val left = (screenWidth - scanSizePx) / 2
        val top = (screenHeight - scanSizePx) / 2
        val right = left + scanSizePx
        val bottom = top + scanSizePx

        // 2. Definir la forma del "agujero" (Rectángulo con esquinas redondeadas)
        val roundedRectPath = androidx.compose.ui.graphics.Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    rect = androidx.compose.ui.geometry.Rect(
                        offset = androidx.compose.ui.geometry.Offset(left, top),
                        size = androidx.compose.ui.geometry.Size(scanSizePx, scanSizePx)
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            )
        }

        // 3. DIBUJAR EL FONDO OPACO CON EL AGUJERO
        clipPath(roundedRectPath, clipOp = androidx.compose.ui.graphics.ClipOp.Difference) {
            drawRect(color = overlayColor)
        }

        // 4. PREPARAR EL ESTILO DE LAS ESQUINAS EN "L"
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = cornerStrokeWidth.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round,
            join = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        val lengthPx = cornerLength.toPx()
        // Ajuste fino para que la línea quede justo en el borde del agujero
        val offset = cornerStrokeWidth.toPx() / 2

        // 5. DIBUJAR LAS 4 ESQUINAS EN "L" (Acompañando la curva del agujero)

        // Esquina Superior Izquierda
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(left - offset, top + lengthPx) // Empieza abajo
                lineTo(left - offset, top + cornerRadiusPx) // Recta hasta inicio curva
                // Curva
                quadraticBezierTo(left - offset, top - offset, left + cornerRadiusPx, top - offset)
                lineTo(left + lengthPx, top - offset) // Recta hacia la derecha
            },
            color = cornerColor,
            style = stroke
        )

        // Esquina Superior Derecha
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(right - lengthPx, top - offset) // Empieza izquierda
                lineTo(right - cornerRadiusPx, top - offset) // Recta hasta inicio curva
                // Curva
                quadraticBezierTo(right + offset, top - offset, right + offset, top + cornerRadiusPx)
                lineTo(right + offset, top + lengthPx) // Recta hacia abajo
            },
            color = cornerColor,
            style = stroke
        )

        // Esquina Inferior Izquierda
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(left - offset, bottom - lengthPx) // Empieza arriba
                lineTo(left - offset, bottom - cornerRadiusPx) // Recta hasta inicio curva
                // Curva
                quadraticBezierTo(left - offset, bottom + offset, left + cornerRadiusPx, bottom + offset)
                lineTo(left + lengthPx, bottom + offset) // Recta hacia la derecha
            },
            color = cornerColor,
            style = stroke
        )

        // Esquina Inferior Derecha
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(right - lengthPx, bottom + offset) // Empieza izquierda
                lineTo(right - cornerRadiusPx, bottom + offset) // Recta hasta inicio curva
                // Curva
                quadraticBezierTo(right + offset, bottom + offset, right + offset, bottom - cornerRadiusPx)
                lineTo(right + offset, bottom - lengthPx) // Recta hacia arriba
            },
            color = cornerColor,
            style = stroke
        )
    }
}