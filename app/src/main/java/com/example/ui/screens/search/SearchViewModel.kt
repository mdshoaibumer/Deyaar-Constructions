package com.example.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.search.GlobalSearchResult
import com.example.domain.usecase.search.GlobalSearchUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

data class SearchUiState(
    val query: String = "",
    val results: List<GlobalSearchResult> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val globalSearchUseCase: GlobalSearchUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")

    val uiState: StateFlow<SearchUiState> = _query
        .debounce(300) // Wait 300ms after user stops typing
        .flatMapLatest { query ->
            if (query.length < 2) {
                flowOf(SearchUiState(query = query))
            } else {
                globalSearchUseCase(query)
                    .map { results -> SearchUiState(query = query, results = results) }
                    .onStart { emit(SearchUiState(query = query, isLoading = true)) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchUiState()
        )

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }
}

class SearchViewModelFactory(
    private val globalSearchUseCase: GlobalSearchUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(globalSearchUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
