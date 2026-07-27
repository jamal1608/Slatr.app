package com.tonespace.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonespace.app.data.model.User
import com.tonespace.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val success: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(isLoggedIn = userRepository.isUserLoggedIn()))
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        if (userRepository.isUserLoggedIn()) {
            loadUser()
        }
    }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Email and password are required")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.signInWithEmail(email, password)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, isLoggedIn = true, user = user
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, error = it.message
                    )
                }
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "All fields are required")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.signUpWithEmail(email, password, displayName)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, isLoggedIn = true, user = user,
                        success = "Account created successfully!"
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, error = it.message
                    )
                }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            userRepository.signInWithGoogle(idToken)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, isLoggedIn = true, user = user
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, error = it.message
                    )
                }
        }
    }

    fun signOut() {
        userRepository.signOut()
        _uiState.value = AuthUiState()
    }

    private fun loadUser() {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            userRepository.getUser(uid)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(user = user, isLoggedIn = true)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = null)
    }
}