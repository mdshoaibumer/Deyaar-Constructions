package com.example.ui.screens.documentation

import com.example.ui.theme.Dimens
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.domain.model.Document
import com.example.domain.repository.DocumentationRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentDetailsScreen(
    documentId: String,
    repository: DocumentationRepository,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var document by remember { mutableStateOf<Document?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(documentId) {
        document = repository.getDocumentById(documentId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Document Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            repository.deleteDocument(documentId)
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Document")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (document != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(Dimens.spaceMedium)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp).align(Alignment.CenterHorizontally),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                Text(text = document?.title ?: "", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                Text(text = "Category: ${document?.category}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                Text(text = "Created: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(document?.createdAt ?: 0))}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                Text(text = document?.description ?: "No description provided.", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { /* Open File Intent */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Open Document")
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Document not found")
            }
        }
    }
}
