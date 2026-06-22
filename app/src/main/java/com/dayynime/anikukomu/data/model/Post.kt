package com.dayynime.anikukomu.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Post(
    val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    val caption: String? = null,
    @SerialName("image_url") val imageUrl: String = "",
    @SerialName("image_public_id") val imagePublicId: String = "",
    @SerialName("likes_count") val likesCount: Int = 0,
    @SerialName("comments_count") val commentsCount: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
    // Relationships loaded in queries
    val profiles: Profile? = null,
    @SerialName("post_anime_tags") val postAnimeTags: List<PostAnimeTagJoin>? = null
)

@Serializable
data class PostAnimeTagJoin(
    @SerialName("post_id") val postId: String,
    @SerialName("anime_id") val animeId: Long,
    val animes: Anime? = null
)

@Serializable
data class InsertPostAnimeTag(
    @SerialName("post_id") val postId: String,
    @SerialName("anime_id") val animeId: Long
)
