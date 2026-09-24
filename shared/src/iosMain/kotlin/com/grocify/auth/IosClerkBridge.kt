package com.grocify.auth

/**
 * Bridge between the Swift Clerk host (`iosApp`) and shared Kotlin auth.
 *
 * Swift calls [setSession] after a successful Clerk sign-in and registers
 * [setOnHostSignOut] so Kotlin [signOut] can clear the native Clerk session.
 */
object IosClerkBridge {
    private var userId: String? = null
    private var token: String? = null
    private var onHostSignOut: (() -> Unit)? = null
    private val sessionListeners = mutableListOf<() -> Unit>()

    fun setSession(userId: String, token: String?) {
        this.userId = userId
        this.token = token
        notifySessionChanged()
    }

    fun clearSession() {
        userId = null
        token = null
        notifySessionChanged()
    }

    fun getUserId(): String? = userId

    fun getToken(): String? = token

    fun isSignedIn(): Boolean = !userId.isNullOrBlank()

    fun setOnHostSignOut(handler: (() -> Unit)?) {
        onHostSignOut = handler
    }

    fun requestHostSignOut() {
        onHostSignOut?.invoke()
        clearSession()
    }

    fun addSessionListener(listener: () -> Unit) {
        sessionListeners += listener
    }

    fun removeSessionListener(listener: () -> Unit) {
        sessionListeners.remove(listener)
    }

    /** Debug-only local session when Clerk host is unavailable. */
    fun enableDevBypass(userId: String = "ios-dev-user") {
        setSession(userId, "dev-bypass-token")
    }

    private fun notifySessionChanged() {
        sessionListeners.toList().forEach { it.invoke() }
    }
}
