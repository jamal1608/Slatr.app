package com.tonespace.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonespace.app.data.local.entity.SoundEntity
import com.tonespace.app.data.repository.SoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<SoundEntity> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val soundRepository: SoundRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState
    private var searchJob: Job? = null

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        searchJob?.cancel()
        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isSearching = true)
                delay(300)
                soundRepository.searchSounds(query)
                    .catch { _uiState.value = _uiState.value.copy(isSearching = false) }
                    .collect { results ->
                        _uiState.value = _uiState.value.copy(
                            results = results,
                            isSearching = false,
                            hasSearched = true
                        )
                    }
            }
        } else {
            _uiState.value = _uiState.value.copy(results = emptyList(), hasSearched = false)
        }
    }

    fun clearSearch() {
        _uiState.value = SearchUiState(recentSearches = _uiState.value.recentSearches)
    }
}