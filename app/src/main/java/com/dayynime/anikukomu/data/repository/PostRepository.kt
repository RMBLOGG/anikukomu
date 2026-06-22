package com.dayynime.anikukomu.data.repository

import com.dayynime.anikukomu.core.supabase
import com.dayynime.anikukomu.data.model.Follow
import com.dayynime.anikukomu.data.model.InsertPostAnimeTag
import com.dayynime.anikukomu.data.model.Like
import com.dayynime.anikukomu.data.model.Post
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PostRepository {

    suspend fun fetchFeed(currentUserId: String? = null): List<Post> = withContext(Dispatchers.IO) {
        try {
            if (currentUserId == null) {
                // Return all posts for guests
                val response = supabase.postgrest["posts"].select(Columns.raw("*, profiles(*), post_anime_tags(*, animes(*))")) {
                    order("created_at", Order.DESCENDING)
                }
                return@withContext response.decodeList<Post>()
            } else {
                // Fetch following IDs
                val followingResponse = supabase.postgrest["follows"].select {
                    filter {
                        eq("follower_id", currentUserId)
                    }
                }
                val followingIds = followingResponse.decodeList<Follow>().map { it.followingId }
                val targetUserIds = followingIds + currentUserId

                val response = supabase.postgrest["posts"].select(Columns.raw("*, profiles(*), post_anime_tags(*, animes(*))")) {
                    filter {
                        isIn("user_id", targetUserIds)
                    }
                    order("created_at", Order.DESCENDING)
                }
                val posts = response.decodeList<Post>()
                if (posts.isEmpty()) {
                    // Fallback to general posts if following feed is empty, to provide active content
                    val fallbackResponse = supabase.postgrest["posts"].select(Columns.raw("*, profiles(*), post_anime_tags(*, animes(*))")) {
                        order("created_at", Order.DESCENDING)
                    }
                    return@withContext fallbackResponse.decodeList<Post>()
                }
                return@withContext posts
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Try fetching all posts as resilient fallback
            try {
                val response = supabase.postgrest["posts"].select(Columns.raw("*, profiles(*), post_anime_tags(*, animes(*))")) {
                    order("created_at", Order.DESCENDING)
                }
                return@withContext response.decodeList<Post>()
            } catch (inner: Exception) {
                inner.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun fetchExplore(searchQuery: String? = null, genreFilter: String? = null): List<Post> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest["posts"].select(Columns.raw("*, profiles(*), post_anime_tags(*, animes(*))")) {
                order("created_at", Order.DESCENDING)
            }
            val allPosts = response.decodeList<Post>()

            // Filter locally to avoid Postgrest join search complexity
            var filtered = allPosts
            if (!searchQuery.isNullOrBlank()) {
                filtered = filtered.filter { post ->
                    val userMatch = post.profiles?.username?.contains(searchQuery, ignoreCase = true) == true ||
                            post.profiles?.displayName?.contains(searchQuery, ignoreCase = true) == true
                    val animeMatch = post.postAnimeTags?.any { tag ->
                        tag.animes?.title?.contains(searchQuery, ignoreCase = true) == true
                    } == true
                    val captionMatch = post.caption?.contains(searchQuery, ignoreCase = true) == true
                    userMatch || animeMatch || captionMatch
                }
            }

            if (!genreFilter.isNullOrBlank()) {
                filtered = filtered.filter { post ->
                    post.postAnimeTags?.any { tag ->
                        tag.animes?.genre?.any { genre ->
                            genre.contains(genreFilter, ignoreCase = true)
                        } == true
                    } == true
                }
            }
            filtered
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun fetchUserPosts(profileUserId: String): List<Post> = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest["posts"].select(Columns.raw("*, profiles(*), post_anime_tags(*, animes(*))")) {
                filter {
                    eq("user_id", profileUserId)
                }
                order("created_at", Order.DESCENDING)
            }
            response.decodeList<Post>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun fetchPostDetail(postId: String): Post? = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest["posts"].select(Columns.raw("*, profiles(*), post_anime_tags(*, animes(*))")) {
                filter {
                    eq("id", postId)
                }
            }
            response.decodeSingle<Post>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun createPost(
        userId: String,
        caption: String,
        imageUrl: String,
        imagePublicId: String,
        animeIds: List<Long>
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val post = Post(
                userId = userId,
                caption = caption,
                imageUrl = imageUrl,
                imagePublicId = imagePublicId
            )
            val insertResponse = supabase.postgrest["posts"].insert(post) {
                select()
            }
            val insertedPost = insertResponse.decodeSingle<Post>()
            val postId = insertedPost.id ?: return@withContext false

            if (animeIds.isNotEmpty()) {
                val tags = animeIds.map { InsertPostAnimeTag(postId = postId, animeId = it) }
                supabase.postgrest["post_anime_tags"].insert(tags)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun checkIsPostLiked(userId: String?, postId: String): Boolean = withContext(Dispatchers.IO) {
        if (userId == null) return@withContext false
        try {
            val response = supabase.postgrest["likes"].select {
                filter {
                    eq("user_id", userId)
                    eq("post_id", postId)
                }
            }
            response.decodeList<Like>().isNotEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun likePost(userId: String, postId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val like = Like(userId = userId, postId = postId)
            supabase.postgrest["likes"].insert(like)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun unlikePost(userId: String, postId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest["likes"].delete {
                filter {
                    eq("user_id", userId)
                    eq("post_id", postId)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
