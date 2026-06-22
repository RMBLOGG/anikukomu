package com.dayynime.anikukomu.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayynime.anikukomu.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChanged(value: String) {
        _state.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _state.update { it.copy(password = value, errorMessage = null) }
    }

    fun login() {
        val emailVal = _state.value.email.trim()
        val passVal = _state.value.password

        if (emailVal.isBlank() || passVal.isBlank()) {
            _state.update { it.copy(errorMessage = "Email dan password tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val success = AuthRepository.login(emailVal, passVal)
            if (success) {
                _state.update { it.copy(isSuccess = true, isLoading = false) }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Login gagal. Cek kembali email & password kamu weeb!"
                    )
                }
            }
        }
    }
}
