package com.grappim.hateitorrateit.di

import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.cleanerimpl.DataCleanerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface DataCleanerModule {

    @Binds
    fun bindDataCleaner(impl: DataCleanerImpl): DataCleaner
}
