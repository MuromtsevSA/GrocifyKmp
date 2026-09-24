package com.grocify.platform

actual object PlatformConfig {
    // Local SQLite — no remote API required.
    actual val apiBaseUrl: String = ""
}
