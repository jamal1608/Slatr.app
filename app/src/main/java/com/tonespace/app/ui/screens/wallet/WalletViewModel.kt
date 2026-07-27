package com.tonespace.app.ui.screens.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonespace.app.data.repository.UserRepository
import com.tonespace.app.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WalletUiState(
    val balance: Long = 0,
    val totalEarnings: Long = 0,
    val totalPlays: Long = 0,
    val totalDownloads: Long = 0,
    val withdrawalHistory: List<Map<String, Any>> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val withdrawalSuccess: Boolean = false
)

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState

    init {
        loadWallet()
    }

    private fun loadWallet() {
        val uid = userRepository.getCurrentUserId()
        if (uid == null) {
            _uiState.value = _uiState.value.copy(isLoading = false, error = "Not logged in")
            return
        }
        viewModelScope.launch {
            userRepository.getCreatorEarnings(uid)
                .onSuccess { data ->
                    _uiState.value = _uiState.value.copy(
                        balance = data["balance"] ?: 0L,
                        totalEarnings = data["totalEarnings"] ?: 0L,
                        totalPlays = data["totalPlays"] ?: 0L,
                        totalDownloads = data["totalDownloads"] ?: 0L,
                        isLoading = false
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(error = it.message, isLoading = false)
                }
        }
    }

    fun requestWithdrawal(amountCents: Long) {
        if (amountCents < Constants.MIN_WITHDRAWAL_CENTS) {
            _uiState.value = _uiState.value.copy(error = "Minimum withdrawal is $${Constants.MIN_WITHDRAWAL_CENTS / 100}")
            return
        }
        viewModelScope.launch {
            userRepository.requestWithdrawal(amountCents)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(withdrawalSuccess = true)
                    loadWallet()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(error = it.message)
                }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadWallet()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearWithdrawalSuccess() {
        _uiState.value = _uiState.value.copy(withdrawalSuccess = false)
    }
}