package com.tonespace.app.ui.screens.creator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonespace.app.data.local.entity.SoundEntity
import com.tonespace.app.data.repository.SoundRepository
import com.tonespace.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreatorUiState(
    val isCreator: Boolean = false,
    val earnings: Map<String, Long> = emptyMap(),
    val mySounds: List<SoundEntity> = emptyList(),
    val totalUploads: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class CreatorViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val soundRepository: SoundRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatorUiState())
    val uiState: StateFlow<CreatorUiState> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            userRepository.getCreatorEarnings(uid)
                .onSuccess { earnings ->
                    _uiState.value = _uiState.value.copy(
                        earnings = earnings,
                        totalUploads = (earnings["totalPlays"] ?: 0).toInt(),
                        isLoading = false
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(error = it.message, isLoading = false)
                }
        }

        viewModelScope.launch {
            soundRepository.getTrendingSounds()
                .catch { }
                .collect { sounds ->
                    val mine = sounds.filter { it.creatorId == uid }
                    _uiState.value = _uiState.value.copy(mySounds = mine)
                }
        }
    }

    fun enableCreatorMode() {
        viewModelScope.launch {
            userRepository.toggleCreatorMode()
                .onSuccess { isCreator ->
                    _uiState.value = _uiState.value.copy(isCreator = isCreator)
                }
        }
    }

    fun deleteSound(soundId: String) {
        viewModelScope.launch {
            soundRepository.deleteSound(soundId)
                .onSuccess { loadData() }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadData()
    }
}