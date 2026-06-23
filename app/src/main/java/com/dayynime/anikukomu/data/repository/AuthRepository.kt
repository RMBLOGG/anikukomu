package com.dayynime.anikukomu.data.repository

import com.dayynime.anikukomu.core.supabase
import com.dayynime.anikukomu.data.model.Profile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AuthRepository {

    suspend fun login(emailInput: String, passwordInput: String): Boolean = withContext(Dispatchers.IO) {
        try {
            supabase.auth.signInWith(Email) {
                email = emailInput
                password = passwordInput
            }
            supabase.auth.currentSessionOrNull() != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun register(usernameInput: String, emailInput: String, passwordInput: String): Boolean = withContext(Dispatchers.IO) {
        try {
            supabase.auth.signUpWith(Email) {
                email = emailInput
                password = passwordInput
            }
            val user = supabase.auth.currentUserOrNull()
            if (user != null) {
                val profile = Profile(
                    id = user.id,
                    username = usernameInput,
                    displayName = usernameInput,
                    followersCount = 0,
                    followingCount = 0,
                    postsCount = 0
                )
                supabase.postgrest["profiles"].insert(profile)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isSessionActive(): Boolean {
        return supabase.auth.currentSessionOrNull() != null
    }

    fun getCurrentUserId(): String? {
        return supabase.auth.currentUserOrNull()?.id
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        try {
            supabase.auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
