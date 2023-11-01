package com.grappim.hateitorrateit.data.db

import android.content.Context
import androidx.room.Room
import com.grappim.hateitorrateit.BuildConfig
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
        dateTimeConverter: DateTimeConverter,
    ): HateItOrRateItDatabase =
        Room.databaseBuilder(
            context,
            HateItOrRateItDatabase::class.java,
            "hateitorrateit_${BuildConfig.BUILD_TYPE}.db"
        )
            .fallbackToDestructiveMigration()
            .addTypeConverter(dateTimeConverter)
            .build()

    @[Provides Singleton]
    fun provideProductsDao(
        hateItOrRateItDatabase: HateItOrRateItDatabase
    ): ProductsDao = hateItOrRateItDatabase.productsDao()
}