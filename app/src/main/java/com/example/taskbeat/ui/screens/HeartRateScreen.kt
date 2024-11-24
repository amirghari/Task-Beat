import android.Manifest
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taskbeat.R
import com.example.taskbeat.ui.viewmodels.AppViewModelProvider
import com.example.taskbeat.ui.viewmodels.HeartRateViewModel
import java.util.concurrent.Executors
import androidx.compose.ui.layout.ContentScale.Companion as ContentScale1

@Composable
fun HeartRateScreen(
    navCtrl: NavController,
    heartrateVM: HeartRateViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val heartRate by heartrateVM.heartRate.observeAsState()
    val isMeasuring by heartrateVM.isMeasuring.observeAsState(initial = false)
    val context = LocalContext.current
    val lifecycleOwner = LocalContext.current as LifecycleOwner
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Reuse the PreviewView to avoid multiple surface requests.
    val previewView = remember { PreviewView(context) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                Log.d("HeartRateScreen", "Camera permission granted")
                startCamera(context, lifecycleOwner, cameraExecutor, previewView) { image ->
                    heartrateVM.analyzeFrame(image)
                }
            } else {
                Log.e("HeartRateScreen", "Camera permission denied.")
            }
        }
    )

    LaunchedEffect(isMeasuring) {
        if (isMeasuring) {
            // Launch permission when the measurement starts.
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Animate the heart scale when not measuring
    val heartScale by animateFloatAsState(
        targetValue = if (isMeasuring) 1f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 300),
            repeatMode = RepeatMode.Reverse
        )
    )

    // State for showing warning dialog
    var showWarningDialog by remember { mutableStateOf(false) }
    var dismissWarning by remember { mutableStateOf(false) }

  Scaffold()
    { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.heart_image),
                    contentDescription = "Heart Image",
                    modifier = Modifier
                        .size(250.dp)
                        .scale(heartScale), // Apply the scaling animation
                    contentScale = ContentScale1.Fit
                )

                if (isMeasuring) {
                    Spacer(modifier = Modifier.height(32.dp))
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(14.dp, Color(0xFF7EBD8F), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${heartRate ?: "--"} BPM",
                        fontSize = 25.sp,
                        color = Color.Gray,
                    )
                }

                // Check if heart rate exceeds 130, and if so, show the warning dialog
                if (heartRate != null && heartRate!! > 130 && !dismissWarning) {
                    showWarningDialog = true
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (!isMeasuring) {
                            Log.d("HeartRateScreen", "Starting heart rate measurement")
                            heartrateVM.startHeartRateMeasurement()
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

private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    cameraExecutor: java.util.concurrent.Executor,
    previewView: PreviewView,
    onFrameAnalyzed: (ImageProxy) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor) { image ->
                    onFrameAnalyzed(image)
                    image.close()
                }
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
            Log.d("HeartRateScreen", "Camera successfully bound to lifecycle")
        } catch (exc: Exception) {
            Log.e("HeartRateScreen", "Camera binding failed")
        }
    }, ContextCompat.getMainExecutor(context))
}
