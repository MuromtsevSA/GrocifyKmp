package com.grocify.di

import com.grocify.data.local.DatabaseDriverFactory
import com.grocify.data.local.GrocifyDatabase
import com.grocify.data.repository.GroceryLocalRepository
import com.grocify.domain.repository.GroceryRepository
import com.grocify.presentation.viewmodel.GroceryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val commonModule = module {
    single<GroceryRepository> { GroceryLocalRepository(get(), get()) }
    viewModel { GroceryViewModel(get(), get()) }
}

expect val platformModule: org.koin.core.module.Module

val appModules = listOf(commonModule, platformModule)
