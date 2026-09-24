package com.grocify.auth

interface AuthTokenProvider {
    suspend fun getToken(): String?
    suspend fun isSignedIn(): Boolean
    suspend fun signOut()
}
