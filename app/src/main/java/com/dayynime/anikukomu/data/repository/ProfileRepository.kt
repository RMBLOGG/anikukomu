package com.dayynime.anikukomu.data.repository

import com.dayynime.anikukomu.core.supabase
import com.dayynime.anikukomu.data.model.Follow
import com.dayynime.anikukomu.data.model.Profile
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ProfileRepository {

    suspend fun getProfile(userId: String): Profile? = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest["profiles"].select {
                filter {
                    eq("id", userId)
                }
            }
            response.decodeSingle<Profile>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateProfile(
        userId: String,
        displayName: String?,
        bio: String?,
        avatarUrl: String?,
        websiteUrl: String?,
        twitterUrl: String?,
        instagramUrl: String?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val updates = mutableMapOf<String, String?>()
            updates["display_name"] = displayName
            updates["bio"] = bio
            if (avatarUrl != null) {
                updates["avatar_url"] = avatarUrl
            }
            updates["website_url"] = websiteUrl
            updates["twitter_url"] = twitterUrl
            updates["instagram_url"] = instagramUrl

            supabase.postgrest["profiles"].update(updates) {
                filter {
                    eq("id", userId)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun checkIsFollowing(myId: String, targetId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = supabase.postgrest["follows"].select {
                filter {
                    eq("follower_id", myId)
                    eq("following_id", targetId)
                }
            }
            response.decodeList<Follow>().isNotEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun followUser(myId: String, targetId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val follow = Follow(followerId = myId, followingId = targetId)
            supabase.postgrest["follows"].insert(follow)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun unfollowUser(myId: String, targetId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            supabase.postgrest["follows"].delete {
                filter {
                    eq("follower_id", myId)
                    eq("following_id", targetId)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
