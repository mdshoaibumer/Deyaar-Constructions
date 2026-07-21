package com.example.core.permissions

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

object PermissionManager {
    
    val cameraPermissions = listOf(Manifest.permission.CAMERA)

    val storagePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions(
    permissions: List<String>,
    onGranted: () -> Unit,
    onDenied: (Boolean) -> Unit
) {
    val permissionsState: MultiplePermissionsState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(permissionsState) {
        if (permissionsState.allPermissionsGranted) {
            onGranted()
        } else {
            onDenied(permissionsState.shouldShowRationale)
        }
    }
    
    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }
}
