package com.dayynime.anikukomu.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Profile(
    val id: String, // UUID from auth
    val username: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val bio: String? = null,
    @SerialName("website_url") val websiteUrl: String? = null,
    @SerialName("twitter_url") val twitterUrl: String? = null,
    @SerialName("instagram_url") val instagramUrl: String? = null,
    @SerialName("followers_count") val followersCount: Int = 0,
    @SerialName("following_count") val followingCount: Int = 0,
    @SerialName("posts_count") val postsCount: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)
