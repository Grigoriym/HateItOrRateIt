package com.grappim.hateitorrateit.di

import com.grappim.hateitorrateit.analyticsapi.AnalyticsController
import com.grappim.hateitorrateit.analyticsimpl.FirebaseAnalyticsControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface AnalyticsControllerModule {
    @Binds
    fun bindAnalyticsController(
        firebaseAnalyticsControllerImpl: FirebaseAnalyticsControllerImpl
    ): AnalyticsController
}
