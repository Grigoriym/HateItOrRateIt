package com.grappim.hateitorrateit.data.db.di

import android.content.Context
import androidx.room.Room
import com.grappim.hateitorrateit.data.db.BuildConfig
import com.grappim.hateitorrateit.data.db.HateItOrRateItDatabase
import com.grappim.hateitorrateit.data.db.converters.DateTimeConverter
import com.grappim.hateitorrateit.data.db.dao.DatabaseDao
import com.grappim.hateitorrateit.data.db.dao.ProductsDao
import com.grappim.hateitorrateit.data.db.utils.TransactionController
import com.grappim.hateitorrateit.data.db.utils.TransactionControllerImpl
import com.grappim.hateitorrateit.data.db.wrapper.DatabaseWrapper
import com.grappim.hateitorrateit.data.db.wrapper.DatabaseWrapperImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class DatabaseModule {

    @[Provides Singleton]
    fun provideRoomDatabase(
        @ApplicationContext context: Context,
        dateTimeConverter: DateTimeConverter
    ): HateItOrRateItDatabase = Room.databaseBuilder(
        context,
        HateItOrRateItDatabase::class.java,
        "hateitorrateit_${BuildConfig.BUILD_TYPE}.db"
    )
        .addTypeConverter(dateTimeConverter)
        .build()

    @[Provides Singleton]
    fun provideProductsDao(
        databaseWrapper: DatabaseWrapper
    ): ProductsDao = databaseWrapper.productsDao

    @[Provides Singleton]
    fun provideDatabaseDao(
        databaseWrapper: DatabaseWrapper
    ): DatabaseDao = databaseWrapper.databaseDao
}

@[Module InstallIn(SingletonComponent::class)]
interface DatabaseBindsModule {
    @Binds
    fun bindDatabaseWrapper(databaseWrapper: DatabaseWrapperImpl): DatabaseWrapper

    @Binds
    fun bindTransactionController(transactionControllerImpl: TransactionControllerImpl): TransactionController
}
