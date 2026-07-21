package com.example.ui.screens.documentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Document
import com.example.domain.model.Photo
import com.example.domain.repository.DocumentationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocumentationViewModel(
    private val projectId: String,
    private val repository: DocumentationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentationUiState(isLoading = true))
    val uiState: StateFlow<DocumentationUiState> = _uiState.asStateFlow()

    init {
        loadDocumentation()
    }

    private fun loadDocumentation() {
        viewModelScope.launch {
            repository.getPhotosForProject(projectId).collect { photos ->
                _uiState.update { it.copy(photos = photos, isLoading = false) }
            }
        }
        viewModelScope.launch {
            repository.getDocumentsForProject(projectId).collect { documents ->
                _uiState.update { it.copy(documents = documents) }
            }
        }
    }
}

data class DocumentationUiState(
    val photos: List<Photo> = emptyList(),
    val documents: List<Document> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class DocumentationViewModelFactory(
    private val projectId: String,
    private val repository: DocumentationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DocumentationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DocumentationViewModel(projectId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
