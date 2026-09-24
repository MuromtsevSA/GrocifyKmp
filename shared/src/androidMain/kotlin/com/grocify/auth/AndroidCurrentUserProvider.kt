package com.grocify.auth

import com.clerk.api.Clerk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

class AndroidCurrentUserProvider : CurrentUserProvider {
    override suspend fun getUserId(): String? {
        AndroidDevSession.bypassUserId?.let { return it }
        if (!waitForClerk()) return null
        return Clerk.userFlow.value?.id
    }

    private suspend fun waitForClerk(): Boolean =
        withTimeoutOrNull(8_000) {
            Clerk.isInitialized.first { it }
        } == true
}
