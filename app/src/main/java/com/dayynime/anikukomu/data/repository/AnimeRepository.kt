package com.dayynime.anikukomu.data.repository

import com.dayynime.anikukomu.core.supabase
import com.dayynime.anikukomu.data.model.Anime
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AnimeRepository {

    suspend fun searchAnimes(query: String): List<Anime> = withContext(Dispatchers.IO) {
        try {
            if (query.isBlank()) {
                val response = supabase.postgrest["animes"].select {
                    limit(20)
                }
                return@withContext response.decodeList<Anime>()
            }
            val response = supabase.postgrest["animes"].select {
                filter {
                    ilike("title", "%$query%")
                }
                limit(20)
            }
            response.decodeList<Anime>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addAnime(title: String, genreList: List<String>): Anime? = withContext(Dispatchers.IO) {
        try {
            // Generate a random high-quality placeholder cover from unsplash anime collection if not provided
            val defaultCover = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600"
            val anime = Anime(
                title = title,
                genre = genreList,
                coverUrl = defaultCover
            )
            val insertResponse = supabase.postgrest["animes"].insert(anime) {
                select()
            }
            insertResponse.decodeSingle<Anime>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
