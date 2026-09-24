package com.grocify.auth

interface CurrentUserProvider {
    suspend fun getUserId(): String?
}
