package com.grappim.hateitorrateit.data.db.di

import com.grappim.hateitorrateit.data.db.dao.DatabaseDao
import com.grappim.hateitorrateit.data.db.dao.ProductsDao
import com.grappim.hateitorrateit.data.db.utils.TransactionController
import com.grappim.hateitorrateit.data.db.wrapper.DatabaseWrapper
import com.grappim.hateitorrateit.data.db.wrapper.DatabaseWrapperImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class DatabaseModule {

    @[Provides Singleton]
    fun provideProductsDao(
        databaseWrapper: DatabaseWrapper
    ): ProductsDao = databaseWrapper.productsDao

    @[Provides Singleton]
    fun provideDatabaseDao(
        databaseWrapper: DatabaseWrapper
    ): DatabaseDao = databaseWrapper.databaseDao

    @[Provides Singleton]
    fun provideTransactionController(
        databaseWrapper: DatabaseWrapper
    ): TransactionController = databaseWrapper.transactionController
}

@[Module InstallIn(SingletonComponent::class)]
interface DatabaseBindsModule {
    @Binds
    fun bindDatabaseWrapper(databaseWrapper: DatabaseWrapperImpl): DatabaseWrapper
}
