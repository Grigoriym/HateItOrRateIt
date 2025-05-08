package com.grappim.hateitorrateit.data.cleanerimpl

import com.grappim.hateitorrateit.data.cleanerapi.DataCleaner
import com.grappim.hateitorrateit.data.cleanerapi.EmptyFilesCleaner
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface DataCleanerModule {

    @Binds
    fun bindDataCleaner(impl: DataCleanerImpl): DataCleaner

    @Binds
    fun bindEmptyFilesCleaner(impl: EmptyFilesCleanerImpl): EmptyFilesCleaner
}
