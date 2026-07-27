package com.tonespace.app.ui.screens.upload

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonespace.app.data.model.SoundCategory
import com.tonespace.app.data.repository.SoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UploadUiState(
    val title: String = "",
    val description: String = "",
    val category: SoundCategory = SoundCategory.CUSTOM,
    val tags: List<String> = emptyList(),
    val tagInput: String = "",
    val audioUri: Uri? = null,
    val audioDuration: Int = 0,
    val audioBytes: ByteArray? = null,
    val coverImageUri: Uri? = null,
    val coverImageBytes: ByteArray? = null,
    val isPremium: Boolean = false,
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val uploadSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class UploadViewModel @Inject constructor(
    application: Application,
    private val soundRepository: SoundRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState

    private val context = application

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateCategory(category: SoundCategory) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateTagInput(input: String) {
        _uiState.value = _uiState.value.copy(tagInput = input)
    }

    fun addTag() {
        val tag = _uiState.value.tagInput.trim()
        if (tag.isNotEmpty() && !_uiState.value.tags.contains(tag)) {
            _uiState.value = _uiState.value.copy(
                tags = _uiState.value.tags + tag,
                tagInput = ""
            )
        }
    }

    fun removeTag(tag: String) {
        _uiState.value = _uiState.value.copy(tags = _uiState.value.tags - tag)
    }

    fun setAudioFile(uri: Uri, duration: Int, bytes: ByteArray) {
        _uiState.value = _uiState.value.copy(
            audioUri = uri,
            audioDuration = duration,
            audioBytes = bytes
        )
    }

    fun setCoverImage(uri: Uri, bytes: ByteArray) {
        _uiState.value = _uiState.value.copy(
            coverImageUri = uri,
            coverImageBytes = bytes
        )
    }

    fun togglePremium() {
        _uiState.value = _uiState.value.copy(isPremium = !_uiState.value.isPremium)
    }

    fun upload() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.value = state.copy(error = "Title is required")
            return
        }
        if (state.audioBytes == null) {
            _uiState.value = state.copy(error = "Please select an audio file")
            return
        }
        if (state.audioDuration < 5) {
            _uiState.value = state.copy(error = "Audio must be at least 5 seconds")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true, error = null, uploadProgress = 0.3f)
            soundRepository.uploadSound(
                title = state.title,
                description = state.description,
                category = state.category,
                tags = state.tags,
                audioBytes = state.audioBytes!!,
                duration = state.audioDuration,
                coverImageBytes = state.coverImageBytes,
                isPremium = state.isPremium
            ).onSuccess {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    uploadSuccess = true,
                    uploadProgress = 1f
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    error = e.message,
                    uploadProgress = 0f
                )
            }
        }
    }

    fun reset() {
        _uiState.value = UploadUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}