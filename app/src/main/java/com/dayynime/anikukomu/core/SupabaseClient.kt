package com.dayynime.anikukomu.core

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

val supabase = createSupabaseClient(
    supabaseUrl = Constants.SUPABASE_URL,
    supabaseKey = Constants.SUPABASE_KEY
) {
    install(Auth) {
        autoRefreshToken = true
        alwaysAutoRefresh = true
    }
    install(Postgrest)
    install(Storage)
    install(Realtime)
}
