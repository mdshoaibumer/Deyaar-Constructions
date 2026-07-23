package com.example.ui.screens.documentation

import com.example.ui.theme.Dimens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.domain.model.Photo
import com.example.domain.repository.DocumentationRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailsScreen(
    photoId: String,
    repository: DocumentationRepository,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var photo by remember { mutableStateOf<Photo?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(photoId) {
        photo = repository.getPhotoById(photoId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photo Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            repository.deletePhoto(photoId)
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Photo")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            com.example.ui.components.layout.ShimmerCardList(modifier = Modifier.padding(paddingValues))
        } else if (photo != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AsyncImage(
                    model = photo?.uri,
                    contentDescription = photo?.description,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.Black)
                )
                Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                    Text(text = photo?.description ?: "No Description", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                    Text(text = "Category: ${photo?.category}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Captured By: ${photo?.capturedBy}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Date: ${SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(Date(photo?.date ?: 0))}", style = MaterialTheme.typography.bodyMedium)
                    if (photo?.tags?.isNotEmpty() == true) {
                        Text(text = "Tags: ${photo?.tags?.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Photo not found")
            }
        }
    }
}
