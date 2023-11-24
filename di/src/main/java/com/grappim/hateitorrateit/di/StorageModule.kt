package com.grappim.hateitorrateit.di

import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.localdatastorageimpl.LocalDataStorageImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface StorageModule {

    @[Binds]
    fun bindLocalDataStorage(localDataStorageImpl: LocalDataStorageImpl): LocalDataStorage
}
