package com.grocify.android

import android.app.Application
import com.grocify.di.initKoin
import com.grocify.platform.initializeClerk
import org.koin.android.ext.koin.androidContext

class GrocifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeClerk(this, BuildConfig.CLERK_PUBLISHABLE_KEY)
        initKoin {
            androidContext(this@GrocifyApplication)
        }
    }
}
