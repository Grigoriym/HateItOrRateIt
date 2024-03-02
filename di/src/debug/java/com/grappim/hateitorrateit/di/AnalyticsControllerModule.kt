package com.grappim.hateitorrateit.di

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsimpl.DebugAnalyticsControllerImpl
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
