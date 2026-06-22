package com.dayynime.anikukomu.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Anime(
    val id: Long? = null,
    @SerialName("mal_id") val malId: Int? = null,
    val title: String,
    @SerialName("cover_url") val coverUrl: String? = null,
    val genre: List<String> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null
)
