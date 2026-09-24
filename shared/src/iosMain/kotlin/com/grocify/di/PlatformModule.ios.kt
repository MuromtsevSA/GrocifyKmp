package com.grocify.di

import com.grocify.auth.AuthTokenProvider
import com.grocify.auth.CurrentUserProvider
import com.grocify.auth.IosAuthTokenProvider
import com.grocify.auth.IosCurrentUserProvider
import com.grocify.data.local.DatabaseDriverFactory
import com.grocify.data.local.GrocifyDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseDriverFactory() }
    single { GrocifyDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single<AuthTokenProvider> { IosAuthTokenProvider() }
    single<CurrentUserProvider> { IosCurrentUserProvider() }
}
