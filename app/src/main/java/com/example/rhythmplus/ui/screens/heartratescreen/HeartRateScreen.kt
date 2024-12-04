package com.example.rhythmplus.ui.screens.heartratescreen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.Surface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rhythmplus.R
import com.example.rhythmplus.ui.viewmodels.AppViewModelProvider
import com.example.rhythmplus.ui.viewmodels.HeartRateViewModel
import java.util.concurrent.Executors

@Composable
fun HeartRateScreen(
    navCtrl: NavController,
    heartrateVM: HeartRateViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Load the user data when the composable is first composed
    LaunchedEffect(Unit) {
        heartrateVM.loadUserFromLocalDatabase()
    }

    val heartRate by heartrateVM.heartRate.observeAsState()
    val isMeasuring by heartrateVM.isMeasuring.observeAsState(initial = false)
    val averageHeartRate by heartrateVM.averageHeartRate.observeAsState()
    val healthData by heartrateVM.healthData.observeAsState()
    val heartRateReadings = healthData?.heartRateReadings ?: emptyList()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Reuse the PreviewView to avoid multiple surface requests.
    val previewView = remember { PreviewView(context) }

    // State to track if the camera is started
    var cameraStarted by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                Log.d("HeartRateScreen", "Camera permission granted")
                // Start measurement and camera
                heartrateVM.startHeartRateMeasurement()
                startCamera(context, lifecycleOwner, cameraExecutor, previewView) { image ->
                    heartrateVM.analyzeFrame(image)
                }
                cameraStarted = true
            } else {
                Log.e("HeartRateScreen", "Camera permission denied.")
            }
        }
    )
    // Circle Rotation while measuring
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400),
            repeatMode = RepeatMode.Restart
        )
    )

    // Observe isMeasuring to stop the camera when measurement ends
    LaunchedEffect(isMeasuring) {
        if (!isMeasuring && cameraStarted) {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            cameraProvider.unbindAll()
            Log.d("HeartRateScreen", "Camera unbound")
            cameraStarted = false
        }
    }

    // Animate the heart scale when not measuring
    val heartScale by animateFloatAsState(
        targetValue = if (isMeasuring) 1f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    // State for showing warning dialog
    var showWarningDialog by remember { mutableStateOf(false) }
    var dismissWarning by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(13.dp))

                Image(
                    painter = painterResource(id = R.drawable.heart_image),
                    contentDescription = "Heart Image",
                    modifier = Modifier
                        .size(200.dp)
                        .scale(heartScale) // Apply the scaling animation
                )
                if (isMeasuring) {
                    Spacer(modifier = Modifier.height(4.dp))
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier
                            .size(20.dp) // Reduced size since we're not displaying the preview
                            .clip(CircleShape)
                    )
                }

                HeartRateChart(heartrateVM = heartrateVM)



                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CustomSpinner(isMeasuring = isMeasuring)
                    // Static Text (BPM value)
                    Text(
                        text = "${heartRate ?: "--"} BPM",
                        fontSize = 25.sp,
                        color = Color.Gray,
                    )
                }

                // Check if heart rate exceeds 130, and if so, show the warning dialog
                if (heartRate != null && (heartRate!! > 130 || heartRate!! < 50) && !dismissWarning) {
                    showWarningDialog = true
                }

                Spacer(modifier = Modifier.height(13.dp))

                Button(
                    onClick = {
                        if (!isMeasuring) {
                            Log.d("HeartRateScreen", "Starting heart rate measurement")
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                heartrateVM.startHeartRateMeasurement()
                                startCamera(context, lifecycleOwner, cameraExecutor, previewView) { image ->
                                    heartrateVM.analyzeFrame(image)
                                }
                                cameraStarted = true
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                    modifier = Modifier.size(160.dp, 50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7EBD8F)),
                    enabled = !isMeasuring
                ) {
                    Text(
                        text = if (isMeasuring) "..." else "Measure",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }

            }

            // Warning dialog
            if (showWarningDialog) {
                AlertDialog(
                    onDismissRequest = { showWarningDialog = false },
                    title = { Text("Warning") },
                    text = { Text("Please hold your finger on the camera properly.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showWarningDialog = false
                                dismissWarning = true // Prevent dialog from immediately reopening
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

// Helper function to start the camera
private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    cameraExecutor: java.util.concurrent.Executor,
    previewView: PreviewView,
    onFrameAnalyzed: (ImageProxy) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        onFrameAnalyzed(image)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
            Log.d("HeartRateScreen", "Camera successfully bound to lifecycle")
        } catch (exc: Exception) {
            Log.e("HeartRateScreen", "Camera binding failed: ${exc.message}")
        }
    }, ContextCompat.getMainExecutor(context))
}