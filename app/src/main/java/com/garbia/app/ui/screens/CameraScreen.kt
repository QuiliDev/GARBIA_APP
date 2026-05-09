package com.garbia.app.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.garbia.app.R

enum class CameraPermissionState {
    LOADING, NEEDED, PERMANENTLY_DENIED, NO_HARDWARE, READY, ERROR
}

@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity

    var permState by remember {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        mutableStateOf(if (granted) CameraPermissionState.READY else CameraPermissionState.LOADING)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            permState = if (isGranted) {
                CameraPermissionState.READY
            } else {
                val canAsk = activity?.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ?: false
                if (canAsk) CameraPermissionState.NEEDED else CameraPermissionState.PERMANENTLY_DENIED
            }
        }
    )

    LaunchedEffect(Unit) {
        if (permState == CameraPermissionState.LOADING) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    when (permState) {
        CameraPermissionState.LOADING -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        CameraPermissionState.READY -> {
            CameraScannerView(
                navController = navController,
                onNoHardware  = { permState = CameraPermissionState.NO_HARDWARE },
                onInitError   = { permState = CameraPermissionState.ERROR }
            )
        }
        CameraPermissionState.NEEDED -> {
            CameraErrorScreen(
                message    = stringResource(R.string.camera_permission_rationale),
                buttonText = stringResource(R.string.camera_btn_grant),
                onAction   = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                onBack     = { navController.popBackStack() }
            )
        }
        CameraPermissionState.PERMANENTLY_DENIED -> {
            CameraErrorScreen(
                message    = stringResource(R.string.camera_permission_denied),
                buttonText = stringResource(R.string.camera_btn_settings),
                onAction   = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                },
                onBack = { navController.popBackStack() }
            )
        }
        CameraPermissionState.NO_HARDWARE -> {
            CameraErrorScreen(
                message    = stringResource(R.string.camera_no_hardware),
                buttonText = stringResource(R.string.btn_back),
                onAction   = { navController.popBackStack() },
                onBack     = { navController.popBackStack() }
            )
        }
        CameraPermissionState.ERROR -> {
            CameraErrorScreen(
                message    = stringResource(R.string.camera_error_init),
                buttonText = stringResource(R.string.camera_btn_retry),
                onAction   = { permState = CameraPermissionState.READY },
                onBack     = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun CameraErrorScreen(
    message: String,
    buttonText: String,
    onAction: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onAction,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(buttonText)
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.btn_back))
        }
    }
}

@Composable
fun CameraScannerView(
    navController: NavController,
    onNoHardware: () -> Unit = {},
    onInitError:  () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    val mainColor = MaterialTheme.colorScheme.primary

    var camera by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }
    var isFlashOn by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val encodedUri = java.net.URLEncoder.encode(uri.toString(), "UTF-8")
                navController.navigate("preview_screen/$encodedUri")
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val future = ProcessCameraProvider.getInstance(ctx)
                future.addListener({
                    val provider = future.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    try {
                        provider.unbindAll()
                        val cam = provider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                        camera = cam
                    } catch (e: androidx.camera.core.CameraInfoUnavailableException) {
                        Log.e("CameraScreen", "No hay cámara trasera", e)
                        onNoHardware()
                    } catch (e: Exception) {
                        Log.e("CameraScreen", "Error al inicializar cámara", e)
                        onInitError()
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            }
        )

        ScannerOverlay(scanSize = 320.dp, cornerRadius = 32.dp)

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.camera_content_desc_close),
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.weight(0.5f))

            Text(
                text = stringResource(R.string.camera_instruction),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Spacer(Modifier.height(320.dp))
            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 64.dp, start = 32.dp, end = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .size(56.dp)
                ) {
                    Icon(
                        Icons.Default.PhotoLibrary,
                        contentDescription = stringResource(R.string.camera_content_desc_gallery),
                        tint = Color.White
                    )
                }

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
                                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                                    val savedUri = result.savedUri ?: android.net.Uri.fromFile(photoFile)
                                    val encodedUri = java.net.URLEncoder.encode(savedUri.toString(), "UTF-8")
                                    navController.navigate("preview_screen/$encodedUri")
                                }
                                override fun onError(exception: androidx.camera.core.ImageCaptureException) {
                                    android.widget.Toast.makeText(
                                        context,
                                        context.getString(R.string.camera_error_capture),
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    },
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(4.dp, Color.White, CircleShape)
                            .padding(8.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().background(mainColor, CircleShape))
                    }
                }

                IconButton(
                    onClick = {
                        isFlashOn = !isFlashOn
                        camera?.cameraControl?.enableTorch(isFlashOn)
                    },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        .size(56.dp)
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

@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    scanSize: Dp = 320.dp,
    cornerRadius: Dp = 32.dp,
    overlayColor: Color = Color.Black.copy(alpha = 0.5f),
    cornerColor: Color = MaterialTheme.colorScheme.primary,
    cornerStrokeWidth: Dp = 5.dp,
    cornerLength: Dp = 40.dp
) {
    androidx.compose.foundation.Canvas(modifier = modifier.fillMaxSize()) {
        val scanSizePx     = scanSize.toPx()
        val cornerRadiusPx = cornerRadius.toPx()
        val left   = (size.width  - scanSizePx) / 2
        val top    = (size.height - scanSizePx) / 2
        val right  = left + scanSizePx
        val bottom = top  + scanSizePx

        val holePath = androidx.compose.ui.graphics.Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    rect = androidx.compose.ui.geometry.Rect(
                        offset = androidx.compose.ui.geometry.Offset(left, top),
                        size   = androidx.compose.ui.geometry.Size(scanSizePx, scanSizePx)
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            )
        }

        clipPath(holePath, clipOp = androidx.compose.ui.graphics.ClipOp.Difference) {
            drawRect(color = overlayColor)
        }

        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = cornerStrokeWidth.toPx(),
            cap   = androidx.compose.ui.graphics.StrokeCap.Round,
            join  = androidx.compose.ui.graphics.StrokeJoin.Round
        )
        val lengthPx = cornerLength.toPx()
        val o = cornerStrokeWidth.toPx() / 2

        // Esquina Superior Izquierda
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(left - o, top + lengthPx)
                lineTo(left - o, top + cornerRadiusPx)
                quadraticBezierTo(left - o, top - o, left + cornerRadiusPx, top - o)
                lineTo(left + lengthPx, top - o)
            },
            color = cornerColor, style = stroke
        )
        // Esquina Superior Derecha
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(right - lengthPx, top - o)
                lineTo(right - cornerRadiusPx, top - o)
                quadraticBezierTo(right + o, top - o, right + o, top + cornerRadiusPx)
                lineTo(right + o, top + lengthPx)
            },
            color = cornerColor, style = stroke
        )
        // Esquina Inferior Izquierda
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(left - o, bottom - lengthPx)
                lineTo(left - o, bottom - cornerRadiusPx)
                quadraticBezierTo(left - o, bottom + o, left + cornerRadiusPx, bottom + o)
                lineTo(left + lengthPx, bottom + o)
            },
            color = cornerColor, style = stroke
        )
        // Esquina Inferior Derecha
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(right - lengthPx, bottom + o)
                lineTo(right - cornerRadiusPx, bottom + o)
                quadraticBezierTo(right + o, bottom + o, right + o, bottom - cornerRadiusPx)
                lineTo(right + o, bottom - lengthPx)
            },
            color = cornerColor, style = stroke
        )
    }
}
