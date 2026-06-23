package com.dayynime.anikukomu.core

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

private const val TAG = "SupabaseClient"

val supabase by lazy {
    try {
        createSupabaseClient(
            supabaseUrl = Constants.SUPABASE_URL,
            supabaseKey = Constants.SUPABASE_KEY
        ) {
            install(Auth) {
                alwaysAutoRefresh = true
            }
            install(Postgrest)
            install(Storage)
            install(Realtime)
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Supabase client", e)
        FirebaseCrashlytics.getInstance().recordException(e)
        throw e
    }
}
