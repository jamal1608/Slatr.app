package com.tonespace.app.ui.screens.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonespace.app.data.model.Sound
import com.tonespace.app.data.repository.SoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val currentSound: Sound? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Int = 0,
    val duration: Int = 0,
    val isLiked: Boolean = false,
    val isPremium: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    application: Application,
    private val soundRepository: SoundRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState

    private var playQueue: List<Sound> = emptyList()
    private var currentIndex: Int = 0

    fun loadSound(soundId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            soundRepository.getSoundById(soundId)
                .onSuccess { sound ->
                    if (sound != null) {
                        _uiState.value = _uiState.value.copy(
                            currentSound = sound,
                            isLiked = sound.isLiked,
                            isPremium = sound.isPremium,
                            isLoading = false
                        )
                        soundRepository.recordPlay(soundId)
                    }
                }
        }
    }

    fun setQueue(sounds: List<Sound>, startIndex: Int) {
        playQueue = sounds
        currentIndex = startIndex
        if (sounds.isNotEmpty()) {
            loadSound(sounds[startIndex].id)
        }
    }

    fun togglePlayPause() {
        _uiState.value = _uiState.value.copy(isPlaying = !_uiState.value.isPlaying)
    }

    fun seekTo(position: Int) {
        _uiState.value = _uiState.value.copy(currentPosition = position)
    }

    fun nextTrack() {
        if (currentIndex < playQueue.size - 1) {
            currentIndex++
            loadSound(playQueue[currentIndex].id)
        }
    }

    fun previousTrack() {
        if (currentIndex > 0) {
            currentIndex--
            loadSound(playQueue[currentIndex].id)
        }
    }

    fun toggleLike() {
        val sound = _uiState.value.currentSound ?: return
        viewModelScope.launch {
            soundRepository.toggleLike(sound.id)
                .onSuccess { liked ->
                    _uiState.value = _uiState.value.copy(isLiked = liked)
                }
        }
    }

    fun downloadSound() {
        val sound = _uiState.value.currentSound ?: return
        viewModelScope.launch {
            soundRepository.recordDownload(sound.id)
        }
    }

    fun updateProgress(position: Int, total: Int) {
        _uiState.value = _uiState.value.copy(currentPosition = position, duration = total)
    }
}