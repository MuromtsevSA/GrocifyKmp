package com.grocify.auth

/** Debug-only bypass when Clerk cannot reach the dashboard (emulator / Native API). */
object AndroidDevSession {
    var bypassUserId: String? = null

    fun enableBypass(userId: String = "dev-local-user") {
        bypassUserId = userId
    }

    fun clear() {
        bypassUserId = null
    }
}
