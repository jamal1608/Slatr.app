package com.tonespace.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonespace.app.data.local.entity.SoundEntity
import com.tonespace.app.data.repository.SoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val trendingSounds: List<SoundEntity> = emptyList(),
    val newSounds: List<SoundEntity> = emptyList(),
    val featuredSounds: List<SoundEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val soundRepository: SoundRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadAllSounds()
    }

    private fun loadAllSounds() {
        viewModelScope.launch {
            soundRepository.fetchTrendingSounds()
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message, isLoading = false) }
        }
        viewModelScope.launch {
            soundRepository.fetchNewSounds()
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message, isLoading = false) }
        }
        viewModelScope.launch {
            soundRepository.fetchFeaturedSounds()
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message, isLoading = false) }
        }

        viewModelScope.launch {
            soundRepository.getTrendingSounds()
                .catch { e -> _uiState.value = _uiState.value.copy(error = e.message) }
                .collect { sounds ->
                    _uiState.value = _uiState.value.copy(
                        trendingSounds = sounds,
                        isLoading = false
                    )
                }
        }

        viewModelScope.launch {
            soundRepository.getSoundsByCategory(com.tonespace.app.data.model.SoundCategory.CUSTOM)
                .catch { }
                .collect { sounds -> _uiState.value = _uiState.value.copy(newSounds = sounds.take(10)) }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadAllSounds()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}