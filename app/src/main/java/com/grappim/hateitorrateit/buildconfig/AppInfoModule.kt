package com.grappim.hateitorrateit.buildconfig

import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface AppInfoModule {
    @Binds
    fun bindAppInfoProvider(appInfoProviderImpl: AppInfoProviderImpl): AppInfoProvider
}
