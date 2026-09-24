package com.grocify.platform

import android.content.Context
import com.clerk.api.Clerk

fun initializeClerk(context: Context, publishableKey: String) {
    if (publishableKey.isNotBlank()) {
        Clerk.initialize(context, publishableKey = publishableKey)
    }
}
