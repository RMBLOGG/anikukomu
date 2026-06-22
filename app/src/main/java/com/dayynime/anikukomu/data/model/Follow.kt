package com.dayynime.anikukomu.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Follow(
    @SerialName("follower_id") val followerId: String,
    @SerialName("following_id") val followingId: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Like(
    @SerialName("user_id") val userId: String,
    @SerialName("post_id") val postId: String,
    @SerialName("created_at") val createdAt: String? = null
)
