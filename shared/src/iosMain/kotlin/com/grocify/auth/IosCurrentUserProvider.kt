package com.grocify.auth

class IosCurrentUserProvider : CurrentUserProvider {
    override suspend fun getUserId(): String? = IosClerkBridge.getUserId()
}
