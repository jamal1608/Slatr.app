package com.tonespace.app.ui.screens.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonespace.app.data.local.entity.SoundEntity
import com.tonespace.app.data.model.SoundCategory
import com.tonespace.app.data.repository.SoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BrowseUiState(
    val selectedCategory: SoundCategory? = null,
    val sounds: List<SoundEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val soundRepository: SoundRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BrowseUiState())
    val uiState: StateFlow<BrowseUiState> = _uiState

    init {
        loadSounds(null)
    }

    fun selectCategory(category: SoundCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category, isLoading = true)
        loadSounds(category)
    }

    private fun loadSounds(category: SoundCategory?) {
        viewModelScope.launch {
            val flow = if (category != null) {
                soundRepository.getSoundsByCategory(category)
            } else {
                soundRepository.getTrendingSounds()
            }
            flow.catch { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }.collect { sounds ->
                _uiState.value = _uiState.value.copy(sounds = sounds, isLoading = false)
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadSounds(_uiState.value.selectedCategory)
    }
}