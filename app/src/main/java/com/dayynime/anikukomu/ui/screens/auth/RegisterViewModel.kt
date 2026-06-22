package com.dayynime.anikukomu.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayynime.anikukomu.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class RegisterViewModel : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onUsernameChanged(value: String) {
        // Clean username: lowercase, alphanumeric
        val filtered = value.lowercase().filter { it.isLetterOrDigit() }
        _state.update { it.copy(username = filtered, errorMessage = null) }
    }

    fun onEmailChanged(value: String) {
        _state.update { it.copy(email = value.trim(), errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _state.update { it.copy(password = value, errorMessage = null) }
    }

    fun register() {
        val userVal = _state.value.username.trim()
        val emailVal = _state.value.email.trim()
        val passVal = _state.value.password

        if (userVal.length < 3) {
            _state.update { it.copy(errorMessage = "Username minimal 3 karakter weeb!") }
            return
        }
        if (emailVal.isBlank()) {
            _state.update { it.copy(errorMessage = "Email tidak boleh kosong!") }
            return
        }
        if (passVal.length < 6) {
            _state.update { it.copy(errorMessage = "Password minimal 6 karakter!") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val success = AuthRepository.register(userVal, emailVal, passVal)
            if (success) {
                _state.update { it.copy(isSuccess = true, isLoading = false) }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Pendaftaran gagal. Username/Email mungkin sudah terdaftar"
                    )
                }
            }
        }
    }
}
