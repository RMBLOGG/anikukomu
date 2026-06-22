package com.dayynime.anikukomu.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Comment(
    val id: String? = null,
    @SerialName("post_id") val postId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("parent_id") val parentId: String? = null,
    val content: String,
    @SerialName("likes_count") val likesCount: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
    val profiles: Profile? = null
)

@Serializable
data class CommentLike(
    @SerialName("user_id") val userId: String,
    @SerialName("comment_id") val commentId: String
)
