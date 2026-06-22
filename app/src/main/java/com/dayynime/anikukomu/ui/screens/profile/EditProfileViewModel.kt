package com.dayynime.anikukomu.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayynime.anikukomu.core.CloudinaryService
import com.dayynime.anikukomu.data.repository.AuthRepository
import com.dayynime.anikukomu.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileState(
    val isLoading: Boolean = false,
    val displayName: String = "",
    val bio: String = "",
    val websiteUrl: String = "",
    val twitterUrl: String = "",
    val instagramUrl: String = "",
    val avatarUrl: String? = null,
    val selectedLocalAvatarUri: Uri? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class EditProfileViewModel : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    fun loadInitialProfile() {
        val currentUserId = AuthRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val profile = ProfileRepository.getProfile(currentUserId)
                if (profile != null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            displayName = profile.displayName ?: "",
                            bio = profile.bio ?: "",
                            avatarUrl = profile.avatarUrl,
                            websiteUrl = profile.websiteUrl ?: "",
                            twitterUrl = profile.twitterUrl ?: "",
                            instagramUrl = profile.instagramUrl ?: ""
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Profile not found") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Error: ${e.localizedMessage}") }
            }
        }
    }

    fun onDisplayNameChanged(value: String) {
        _state.update { it.copy(displayName = value) }
    }

    fun onBioChanged(value: String) {
        if (value.length <= 150) {
            _state.update { it.copy(bio = value) }
        }
    }

    fun onWebsiteChanged(value: String) {
        _state.update { it.copy(websiteUrl = value) }
    }

    fun onTwitterChanged(value: String) {
        _state.update { it.copy(twitterUrl = value) }
    }

    fun onInstagramChanged(value: String) {
        _state.update { it.copy(instagramUrl = value) }
    }

    fun onAvatarLocalSelected(uri: Uri?) {
        _state.update { it.copy(selectedLocalAvatarUri = uri) }
    }

    fun saveProfile(context: Context) {
        val currentUserId = AuthRepository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                var finalAvatarUrl = _state.value.avatarUrl
                val localUri = _state.value.selectedLocalAvatarUri

                // Upload custom avatar if chosen
                if (localUri != null) {
                    val uploadResult = CloudinaryService.uploadImage(context, localUri)
                    finalAvatarUrl = uploadResult.secureUrl
                }

                val saved = ProfileRepository.updateProfile(
                    userId = currentUserId,
                    displayName = _state.value.displayName,
                    bio = _state.value.bio,
                    avatarUrl = finalAvatarUrl,
                    websiteUrl = _state.value.websiteUrl,
                    twitterUrl = _state.value.twitterUrl,
                    instagramUrl = _state.value.instagramUrl
                )

                if (saved) {
                    _state.update { it.copy(isSuccess = true, isSaving = false) }
                } else {
                    _state.update { it.copy(isSaving = false, errorMessage = "Gagal memperbarui database") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, errorMessage = "Error: ${e.localizedMessage}") }
            }
        }
    }
}
