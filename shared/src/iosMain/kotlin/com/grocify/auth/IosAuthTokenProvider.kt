package com.grocify.auth

/**
 * Reads the session published by Swift Clerk via [IosClerkBridge].
 */
class IosAuthTokenProvider : AuthTokenProvider {
    override suspend fun getToken(): String? = IosClerkBridge.getToken()

    override suspend fun isSignedIn(): Boolean = IosClerkBridge.isSignedIn()

    override suspend fun signOut() {
        IosClerkBridge.requestHostSignOut()
    }
}
