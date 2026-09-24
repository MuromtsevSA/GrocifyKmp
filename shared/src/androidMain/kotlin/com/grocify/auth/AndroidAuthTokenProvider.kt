package com.grocify.auth

import com.clerk.api.Clerk
import com.clerk.api.network.serialization.onSuccess
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

class AndroidAuthTokenProvider : AuthTokenProvider {
    override suspend fun getToken(): String? {
        AndroidDevSession.bypassUserId?.let { return "dev-bypass-token" }
        if (!waitForClerk()) return null
        var token: String? = null
        Clerk.auth.getToken().onSuccess { token = it }
        return token
    }

    override suspend fun isSignedIn(): Boolean {
        if (AndroidDevSession.bypassUserId != null) return true
        if (!waitForClerk()) return false
        return Clerk.userFlow.value != null
    }

    override suspend fun signOut() {
        AndroidDevSession.clear()
        if (!waitForClerk()) return
        Clerk.auth.signOut()
    }

    private suspend fun waitForClerk(): Boolean =
        withTimeoutOrNull(CLERK_INIT_TIMEOUT_MS) {
            Clerk.isInitialized.first { it }
        } == true

    private companion object {
        const val CLERK_INIT_TIMEOUT_MS = 8_000L
    }
}
