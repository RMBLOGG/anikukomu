package com.dayynime.anikukomu.ui.screens.home

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayynime.anikukomu.core.CloudinaryService
import com.dayynime.anikukomu.data.model.Post
import com.dayynime.anikukomu.data.model.Story
import com.dayynime.anikukomu.data.repository.AuthRepository
import com.dayynime.anikukomu.data.repository.PostRepository
import com.dayynime.anikukomu.data.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val stories: List<Story> = emptyList(),
    val likedPostIds: Set<String> = emptySet(),
    val errorMessage: String? = null,
    val showGuestDialog: Boolean = false,
    val isUploadingStory: Boolean = false
)

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadContent()
    }

    fun loadContent() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val currentUserId = AuthRepository.getCurrentUserId()
                val postsList = PostRepository.fetchFeed(currentUserId)
                val storiesList = StoryRepository.fetchActiveStories()

                // Check likes for each post
                val likedIds = mutableSetOf<String>()
                if (currentUserId != null) {
                    postsList.forEach { post ->
                        post.id?.let { pid ->
                            if (PostRepository.checkIsPostLiked(currentUserId, pid)) {
                                likedIds.add(pid)
                            }
                        }
                    }
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        posts = postsList,
                        stories = storiesList,
                        likedPostIds = likedIds
                    )
                }
            } catch (e: Throwable) {
                com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance().log("HomeViewModel.loadContent() failed: ${e.javaClass.name}: ${e.message}")
                com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance().recordException(e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat beranda: ${e.javaClass.simpleName}: ${e.message}"
                    )
                }
            }
        }
    }

    fun toggleLike(post: Post) {
        val currentUserId = AuthRepository.getCurrentUserId()
        if (currentUserId == null) {
            _state.update { it.copy(showGuestDialog = true) }
            return
        }

        val postId = post.id ?: return
        val isCurrentlyLiked = _state.value.likedPostIds.contains(postId)

        viewModelScope.launch {
            // Optimistic UI updates
            _state.update { s ->
                val newLikedIds = s.likedPostIds.toMutableSet()
                if (isCurrentlyLiked) newLikedIds.remove(postId) else newLikedIds.add(postId)
                
                val newPosts = s.posts.map { p ->
                    if (p.id == postId) {
                        p.copy(likesCount = p.likesCount + if (isCurrentlyLiked) -1 else 1)
                    } else p
                }
                s.copy(posts = newPosts, likedPostIds = newLikedIds)
            }

            val success = if (isCurrentlyLiked) {
                PostRepository.unlikePost(currentUserId, postId)
            } else {
                PostRepository.likePost(currentUserId, postId)
            }

            if (!success) {
                // Revert state if API call failed
                loadContent()
            }
        }
    }

    fun dismissGuestDialog() {
        _state.update { it.copy(showGuestDialog = false) }
    }

    fun uploadStory(context: Context, uri: Uri) {
        val currentUserId = AuthRepository.getCurrentUserId()
        if (currentUserId == null) {
            _state.update { it.copy(showGuestDialog = true) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isUploadingStory = true, errorMessage = null) }
            try {
                // 1. Upload to Cloudinary
                val uploadResult = CloudinaryService.uploadImage(context, uri)
                
                // 2. Insert to Supabase stories table
                val success = StoryRepository.createStory(
                    userId = currentUserId,
                    imageUrl = uploadResult.secureUrl,
                    imagePublicId = uploadResult.publicId
                )

                if (success) {
                    loadContent() // Refresh stories
                } else {
                    _state.update { it.copy(errorMessage = "Gagal mendaftarkan story") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Gagal mengupload story: ${e.localizedMessage}") }
            } finally {
                _state.update { it.copy(isUploadingStory = false) }
            }
        }
    }
}
