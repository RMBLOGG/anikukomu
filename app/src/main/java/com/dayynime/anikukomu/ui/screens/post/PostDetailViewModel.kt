package com.dayynime.anikukomu.ui.screens.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayynime.anikukomu.data.model.Comment
import com.dayynime.anikukomu.data.model.Post
import com.dayynime.anikukomu.data.repository.AuthRepository
import com.dayynime.anikukomu.data.repository.CommentRepository
import com.dayynime.anikukomu.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostDetailState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val isLiked: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val likedCommentIds: Set<String> = emptySet(),
    val commentInputText: String = "",
    val replyingToComment: Comment? = null,
    val isActionLoading: Boolean = false,
    val errorMessage: String? = null,
    val showGuestDialog: Boolean = false
)

class PostDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow(PostDetailState())
    val state: StateFlow<PostDetailState> = _state.asStateFlow()

    fun loadPostDetails(postId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val currentUserId = AuthRepository.getCurrentUserId()
                val postDetail = PostRepository.fetchPostDetail(postId)
                val commentsList = CommentRepository.fetchCommentsForPost(postId)

                var postLiked = false
                val likedCommentIdsSet = mutableSetOf<String>()

                if (postDetail != null) {
                    postLiked = PostRepository.checkIsPostLiked(currentUserId, postId)
                    if (currentUserId != null) {
                        commentsList.forEach { comment ->
                            comment.id?.let { cid ->
                                if (CommentRepository.checkIsCommentLiked(currentUserId, cid)) {
                                    likedCommentIdsSet.add(cid)
                                }
                            }
                        }
                    }
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        post = postDetail,
                        isLiked = postLiked,
                        comments = commentsList,
                        likedCommentIds = likedCommentIdsSet
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat detail: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun toggleLikePost() {
        val currentUserId = AuthRepository.getCurrentUserId()
        if (currentUserId == null) {
            _state.update { it.copy(showGuestDialog = true) }
            return
        }

        val post = _state.value.post ?: return
        val postId = post.id ?: return
        val isCurrentlyLiked = _state.value.isLiked

        viewModelScope.launch {
            _state.update { s ->
                s.copy(
                    isLiked = !isCurrentlyLiked,
                    post = s.post?.copy(likesCount = s.post.likesCount + if (isCurrentlyLiked) -1 else 1)
                )
            }

            val success = if (isCurrentlyLiked) {
                PostRepository.unlikePost(currentUserId, postId)
            } else {
                PostRepository.likePost(currentUserId, postId)
            }

            if (!success) {
                // Revert
                loadPostDetails(postId)
            }
        }
    }

    fun onCommentInputChanged(text: String) {
        _state.update { it.copy(commentInputText = text) }
    }

    fun setReplyingTo(comment: Comment?) {
        _state.update { it.copy(replyingToComment = comment) }
    }

    fun submitComment() {
        val currentUserId = AuthRepository.getCurrentUserId()
        if (currentUserId == null) {
            _state.update { it.copy(showGuestDialog = true) }
            return
        }

        val text = _state.value.commentInputText
        if (text.isBlank()) return

        val post = _state.value.post ?: return
        val postId = post.id ?: return
        val parentId = _state.value.replyingToComment?.id

        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
            val success = CommentRepository.addComment(
                postId = postId,
                userId = currentUserId,
                content = text,
                parentId = parentId
            )

            if (success) {
                _state.update {
                    it.copy(
                        commentInputText = "",
                        replyingToComment = null,
                        isActionLoading = false
                    )
                }
                loadPostDetails(postId) // Refresh list and counters
            } else {
                _state.update {
                    it.copy(
                        isActionLoading = false,
                        errorMessage = "Gagal mengirim komentar"
                    )
                }
            }
        }
    }

    fun toggleLikeComment(comment: Comment) {
        val currentUserId = AuthRepository.getCurrentUserId()
        if (currentUserId == null) {
            _state.update { it.copy(showGuestDialog = true) }
            return
        }

        val commentId = comment.id ?: return
        val isCurrentlyLiked = _state.value.likedCommentIds.contains(commentId)

        viewModelScope.launch {
            _state.update { s ->
                val newLikedIds = s.likedCommentIds.toMutableSet()
                if (isCurrentlyLiked) newLikedIds.remove(commentId) else newLikedIds.add(commentId)

                val newComments = s.comments.map { c ->
                    if (c.id == commentId) {
                        c.copy(likesCount = c.likesCount + if (isCurrentlyLiked) -1 else 1)
                    } else c
                }
                s.copy(comments = newComments, likedCommentIds = newLikedIds)
            }

            val success = if (isCurrentlyLiked) {
                CommentRepository.unlikeComment(currentUserId, commentId)
            } else {
                CommentRepository.likeComment(currentUserId, commentId)
            }

            if (!success) {
                // Revert via silent reload
                val postId = _state.value.post?.id ?: return@launch
                loadPostDetails(postId)
            }
        }
    }

    fun dismissGuestDialog() {
        _state.update { it.copy(showGuestDialog = false) }
    }
}
