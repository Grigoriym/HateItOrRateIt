package com.grappim.hateitorrateit.feature.home.ui.di

import com.grappim.hateitorrateit.feature.home.ui.utils.HomeUIModelsMapper
import com.grappim.hateitorrateit.feature.home.ui.utils.HomeUIModelsMapperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface HomeModule {

    @Binds
    fun bindHomeUIModelsMapper(impl: HomeUIModelsMapperImpl): HomeUIModelsMapper
}
