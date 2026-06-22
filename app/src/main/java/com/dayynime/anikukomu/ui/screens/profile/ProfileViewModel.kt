package com.dayynime.anikukomu.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayynime.anikukomu.data.model.Post
import com.dayynime.anikukomu.data.model.Profile
import com.dayynime.anikukomu.data.repository.AuthRepository
import com.dayynime.anikukomu.data.repository.PostRepository
import com.dayynime.anikukomu.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val posts: List<Post> = emptyList(),
    val isMyOwnProfile: Boolean = false,
    val isFollowing: Boolean = false,
    val errorMessage: String? = null,
    val showGuestDialog: Boolean = false
)

class ProfileViewModel : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun loadProfile(profileUserId: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val currentUserId = AuthRepository.getCurrentUserId()
                
                // If profileUserId is null or matches current authenticated user, it's own profile!
                val targetUserId = profileUserId ?: currentUserId
                if (targetUserId == null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Profil tidak ditemukan (tidak terautentikasi)"
                        )
                    }
                    return@launch
                }

                val isOwn = (targetUserId == currentUserId)
                val profileDetails = ProfileRepository.getProfile(targetUserId)
                val userPostList = PostRepository.fetchUserPosts(targetUserId)
                
                var following = false
                if (currentUserId != null && !isOwn) {
                    following = ProfileRepository.checkIsFollowing(currentUserId, targetUserId)
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        profile = profileDetails,
                        posts = userPostList,
                        isMyOwnProfile = isOwn,
                        isFollowing = following
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat profil: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun toggleFollow() {
        val currentUserId = AuthRepository.getCurrentUserId()
        if (currentUserId == null) {
            _state.update { it.copy(showGuestDialog = true) }
            return
        }

        val targetUserId = _state.value.profile?.id ?: return
        val isCurrentlyFollowing = _state.value.isFollowing

        viewModelScope.launch {
            _state.update { it.copy(isFollowing = !isCurrentlyFollowing) } // Optimistic
            
            val success = if (isCurrentlyFollowing) {
                ProfileRepository.unfollowUser(currentUserId, targetUserId)
            } else {
                ProfileRepository.followUser(currentUserId, targetUserId)
            }

            if (success) {
                // Refresh profile to have updated follower counts
                val profileDetails = ProfileRepository.getProfile(targetUserId)
                _state.update { it.copy(profile = profileDetails) }
            } else {
                // Revert
                _state.update { it.copy(isFollowing = isCurrentlyFollowing) }
            }
        }
    }

    fun dismissGuestDialog() {
        _state.update { it.copy(showGuestDialog = false) }
    }
}
