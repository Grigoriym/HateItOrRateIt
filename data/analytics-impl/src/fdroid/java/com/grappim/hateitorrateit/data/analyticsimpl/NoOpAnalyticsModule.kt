package com.grappim.hateitorrateit.data.analyticsimpl

import com.grappim.hateitorrateit.data.analyticsapi.AnalyticsController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface NoOpAnalyticsModule {
    @Binds
    fun bindAnalyticsController(
        debugAnalyticsControllerImpl: NoOpAnalyticsModuleImpl
    ): AnalyticsController
}
