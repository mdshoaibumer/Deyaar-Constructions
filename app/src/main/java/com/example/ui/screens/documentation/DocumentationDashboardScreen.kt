package com.example.ui.screens.documentation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Dimens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.domain.model.Document
import com.example.domain.model.Photo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentationDashboardScreen(
    viewModel: DocumentationViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToPhotoDetail: (String) -> Unit,
    onNavigateToDocumentDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Photos", "Documents", "Timeline")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Project Documentation") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedTabIndex == 0) {
                        IconButton(onClick = onNavigateToCamera) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Take Photo")
                        }
                    } else if (selectedTabIndex == 1) {
                        IconButton(onClick = onNavigateToUpload) {
                            Icon(Icons.Default.Add, contentDescription = "Upload Document")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (selectedTabIndex) {
                    0 -> PhotoGallery(photos = uiState.photos, onPhotoClick = onNavigateToPhotoDetail)
                    1 -> DocumentList(documents = uiState.documents, onDocumentClick = onNavigateToDocumentDetail)
                    2 -> TimelineView(photos = uiState.photos, documents = uiState.documents)
                }
            }
        }
    }
}

@Composable
fun PhotoGallery(photos: List<Photo>, onPhotoClick: (String) -> Unit) {
    if (photos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No photos available", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(photos, key = { it.id }) { photo ->
                AsyncImage(
                    model = photo.uri,
                    contentDescription = photo.description,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clickable { onPhotoClick(photo.id) }
                )
            }
        }
    }
}

@Composable
fun DocumentList(documents: List<Document>, onDocumentClick: (String) -> Unit) {
    if (documents.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No documents available", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
            modifier = Modifier.fillMaxSize()
        ) {
            items(documents, key = { it.id }) { doc ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onDocumentClick(doc.id) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.padding(Dimens.spaceMedium), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(Dimens.spaceMedium))
                        Column {
                            Text(text = doc.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(text = "${doc.category} • ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(doc.createdAt))}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineView(photos: List<Photo>, documents: List<Document>) {
    // A simplified timeline view merging photos and documents based on date
    val combinedEvents = (photos.map { TimelineEvent(it.id, it.date, "Photo: ${it.description ?: "No description"}", it.category, it.uri, isPhoto = true) } +
            documents.map { TimelineEvent(it.id, it.createdAt, "Doc: ${it.title}", it.category, null, isPhoto = false) })
        .sortedByDescending { it.date }

    if (combinedEvents.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No timeline events", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(combinedEvents, key = { it.id }) { event ->
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.spaceMedium)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(12.dp)) {
                            Surface(shape = androidx.compose.foundation.shape.CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxSize()) {}
                        }
                        // Draw line logic would go here
                    }
                    Spacer(modifier = Modifier.width(Dimens.spaceMedium))
                    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                        Column(modifier = Modifier.padding(Dimens.spaceMedium)) {
                            Text(text = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(Date(event.date)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(Dimens.spaceMicro))
                            Text(text = event.title, style = MaterialTheme.typography.bodyMedium)
                            if (event.isPhoto && event.thumbnailUri != null) {
                                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                                AsyncImage(
                                    model = event.thumbnailUri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.height(120.dp).fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class TimelineEvent(val id: String, val date: Long, val title: String, val category: String, val thumbnailUri: String?, val isPhoto: Boolean)
