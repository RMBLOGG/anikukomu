package com.dayynime.anikukomu.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayynime.anikukomu.data.model.Post
import com.dayynime.anikukomu.data.repository.AuthRepository
import com.dayynime.anikukomu.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExploreState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val likedPostIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val activeGenre: String? = null,
    val errorMessage: String? = null,
    val showGuestDialog: Boolean = false
)

class ExploreViewModel : ViewModel() {

    private val _state = MutableStateFlow(ExploreState())
    val state: StateFlow<ExploreState> = _state.asStateFlow()

    init {
        search()
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
        search()
    }

    fun onGenreSelected(genre: String?) {
        _state.update { it.copy(activeGenre = genre) }
        search()
    }

    fun search() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val currentUserId = AuthRepository.getCurrentUserId()
                val results = PostRepository.fetchExplore(
                    searchQuery = _state.value.searchQuery,
                    genreFilter = _state.value.activeGenre
                )

                // Check likes
                val likedIds = mutableSetOf<String>()
                if (currentUserId != null) {
                    results.forEach { post ->
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
                        posts = results,
                        likedPostIds = likedIds
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat explore: ${e.localizedMessage}"
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
                search()
            }
        }
    }

    fun dismissGuestDialog() {
        _state.update { it.copy(showGuestDialog = false) }
    }
}
