package com.example.ui.screens.documentation

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import com.example.ui.theme.Dimens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.domain.model.Photo
import com.example.domain.repository.DocumentationRepository
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    projectId: String,
    repository: DocumentationRepository,
    onNavigateBack: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        CameraPreviewContent(projectId = projectId, repository = repository, onNavigateBack = onNavigateBack)
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Camera permission is required.")
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@Composable
private fun CameraPreviewContent(
    projectId: String,
    repository: DocumentationRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    
    val imageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(previewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.align(Alignment.TopStart).padding(Dimens.spaceMedium).background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        
        FloatingActionButton(
            onClick = {
                takePhoto(
                    context = context,
                    imageCapture = imageCapture,
                    executor = ContextCompat.getMainExecutor(context),
                    onImageCaptured = { uri ->
                        coroutineScope.launch {
                            val photo = Photo(
                                id = UUID.randomUUID().toString(),
                                projectId = projectId,
                                uri = uri.toString(),
                                description = "Captured on ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())}",
                                tags = listOf("Site Progress"),
                                category = "Site Progress",
                                date = System.currentTimeMillis(),
                                capturedBy = "Current User",
                                location = null,
                                linkedSiteDiaryId = null,
                                linkedMilestoneId = null
                            )
                            repository.savePhoto(photo)
                            onNavigateBack()
                        }
                    },
                    onError = { }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
                .size(72.dp),
            shape = CircleShape
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Take Photo", modifier = Modifier.size(32.dp))
        }
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFile = File(
        context.getExternalFilesDir(null),
        "Deyaar_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())}.jpg"
    )
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    
    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                onImageCaptured(savedUri)
            }
            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}
