package com.dayynime.anikukomu.data.repository

import com.dayynime.anikukomu.core.supabase
import com.dayynime.anikukomu.data.model.Story
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

object StoryRepository {

    suspend fun fetchActiveStories(): List<Story> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest["stories"].select(Columns.raw("*, profiles(*)"))
            val list = response.decodeList<Story>()
            val now = Instant.now()
            
            // Filter where expires_at is in the future
            list.filter { story ->
                try {
                    val expiresAtStr = story.expiresAt ?: return@filter true
                    val expiresInstant = Instant.parse(expiresAtStr)
                    expiresInstant.isAfter(now)
                } catch (e: Exception) {
                    true // fall back to keeping it if we fail to parse
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun createStory(userId: String, imageUrl: String, imagePublicId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val story = Story(
                userId = userId,
                imageUrl = imageUrl,
                imagePublicId = imagePublicId
            )
            supabase.postgrest["stories"].insert(story)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
