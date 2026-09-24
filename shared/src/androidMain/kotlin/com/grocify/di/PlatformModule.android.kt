package com.grocify.di

import com.grocify.auth.AndroidAuthTokenProvider
import com.grocify.auth.AndroidCurrentUserProvider
import com.grocify.auth.AuthTokenProvider
import com.grocify.auth.CurrentUserProvider
import com.grocify.data.local.DatabaseDriverFactory
import com.grocify.data.local.GrocifyDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { GrocifyDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single<AuthTokenProvider> { AndroidAuthTokenProvider() }
    single<CurrentUserProvider> { AndroidCurrentUserProvider() }
}
