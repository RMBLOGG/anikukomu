package com.dayynime.anikukomu.data.repository

import android.util.Log
import com.dayynime.anikukomu.core.supabase
import com.dayynime.anikukomu.data.model.Profile
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "AuthRepository"

object AuthRepository {

    suspend fun login(emailInput: String, passwordInput: String): Boolean = withContext(Dispatchers.IO) {
        try {
            supabase.auth.signInWith(Email) {
                email = emailInput
                password = passwordInput
            }
            supabase.auth.currentSessionOrNull() != null
        } catch (e: Throwable) {
            Log.e(TAG, "login() failed: ${e.javaClass.name}: ${e.message}", e)
            FirebaseCrashlytics.getInstance().log("login() failed: ${e.javaClass.name}: ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
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
        } catch (e: Throwable) {
            Log.e(TAG, "register() failed: ${e.javaClass.name}: ${e.message}", e)
            FirebaseCrashlytics.getInstance().log("register() failed: ${e.javaClass.name}: ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }
    }

    fun isSessionActive(): Boolean {
        return try {
            supabase.auth.currentSessionOrNull() != null
        } catch (e: Throwable) {
            Log.e(TAG, "isSessionActive() failed: ${e.javaClass.name}: ${e.message}", e)
            FirebaseCrashlytics.getInstance().log("isSessionActive() failed: ${e.javaClass.name}: ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }
    }

    fun getCurrentUserId(): String? {
        return try {
            supabase.auth.currentUserOrNull()?.id
        } catch (e: Throwable) {
            Log.e(TAG, "getCurrentUserId() failed: ${e.javaClass.name}: ${e.message}", e)
            FirebaseCrashlytics.getInstance().log("getCurrentUserId() failed: ${e.javaClass.name}: ${e.message}")
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        try {
            supabase.auth.signOut()
        } catch (e: Throwable) {
            Log.e(TAG, "logout() failed: ${e.javaClass.name}: ${e.message}", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}
