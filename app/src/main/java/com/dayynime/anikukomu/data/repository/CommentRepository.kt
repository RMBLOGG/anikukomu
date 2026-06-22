package com.dayynime.anikukomu.data.repository

import com.dayynime.anikukomu.core.supabase
import com.dayynime.anikukomu.data.model.Comment
import com.dayynime.anikukomu.data.model.CommentLike
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CommentRepository {

    suspend fun fetchCommentsForPost(postId: String): List<Comment> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest["comments"].select(Columns.raw("*, profiles(*)")) {
                filter {
                    eq("post_id", postId)
                }
            }
            response.decodeList<Comment>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addComment(
        postId: String,
        userId: String,
        content: String,
        parentId: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val comment = Comment(
                postId = postId,
                userId = userId,
                content = content,
                parentId = parentId
            )
            supabase.postgrest["comments"].insert(comment)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun checkIsCommentLiked(userId: String?, commentId: String): Boolean = withContext(Dispatchers.IO) {
        if (userId == null) return@withContext false
        try {
            val response = supabase.postgrest["comment_likes"].select {
                filter {
                    eq("user_id", userId)
                    eq("comment_id", commentId)
                }
            }
            response.decodeList<CommentLike>().isNotEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun likeComment(userId: String, commentId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val like = CommentLike(userId = userId, commentId = commentId)
            supabase.postgrest["comment_likes"].insert(like)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun unlikeComment(userId: String, commentId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest["comment_likes"].delete {
                filter {
                    eq("user_id", userId)
                    eq("comment_id", commentId)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
