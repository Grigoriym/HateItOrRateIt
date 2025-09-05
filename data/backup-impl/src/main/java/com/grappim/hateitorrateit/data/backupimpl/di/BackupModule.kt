package com.grappim.hateitorrateit.data.backupimpl.di

import com.grappim.hateitorrateit.data.backupapi.BackupRepository
import com.grappim.hateitorrateit.data.backupapi.ImportRepository
import com.grappim.hateitorrateit.data.backupimpl.BackupRepositoryImpl
import com.grappim.hateitorrateit.data.backupimpl.ImportRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BackupModule {

    @Binds
    abstract fun bindBackupRepository(impl: BackupRepositoryImpl): BackupRepository

    @Binds
    abstract fun bindImportRepository(impl: ImportRepositoryImpl): ImportRepository

    companion object {
        @Provides
        @Singleton
        fun provideJson(): Json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
    }
}
