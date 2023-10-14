package com.grappim.hateitorrateit.data.storage.local

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface StorageModule {

    @[Binds]
    fun bindLocalDataStorage(localDataStorageImpl: LocalDataStorageImpl): LocalDataStorage
}