package com.grappim.hateitorrateit.datetime

import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.format.DateTimeFormatter
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class DtfToStore

@Qualifier
annotation class DtfToDemonstrate

@Qualifier
annotation class DtfDocumentFolder

@Qualifier
annotation class DtfBackupFolderTimestamp

@[Module InstallIn(SingletonComponent::class)]
interface DateTimeModule {
    @Binds
    fun bindDateTimeUtils(dateTimeUtilsImpl: DateTimeUtilsImpl): DateTimeUtils

    companion object {

        private const val UI_PATTERN = "yyyy.MM.dd"
        private const val DOCUMENT_FOLDER = "yyyy-MM-dd_HH-mm-ss"

        /**
         * 2023-11-23T14:16:37.502Z
         */
        @[Provides Singleton DtfToStore]
        fun provideDtfToStore(): DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

        @[Provides Singleton DtfToDemonstrate]
        fun provideDtfToDemonstrate(): DateTimeFormatter = DateTimeFormatter.ofPattern(UI_PATTERN)

        @[Provides Singleton DtfDocumentFolder]
        fun provideDtfDocumentFolder(): DateTimeFormatter =
            DateTimeFormatter.ofPattern(DOCUMENT_FOLDER)

        @[Provides Singleton DtfBackupFolderTimestamp]
        fun provideDtfBackupFolderTimestamp(): DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
    }
}
