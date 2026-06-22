package com.dayynime.anikukomu.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Story(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("image_public_id") val imagePublicId: String,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val profiles: Profile? = null
)
