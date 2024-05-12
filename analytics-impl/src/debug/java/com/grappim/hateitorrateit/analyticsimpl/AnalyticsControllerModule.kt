package com.grappim.hateitorrateit.analyticsimpl

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface AnalyticsControllerModule {
    @Binds
    fun bindAnalyticsController(
        debugAnalyticsControllerImpl: DebugAnalyticsControllerImpl
    ): AnalyticsController
}
