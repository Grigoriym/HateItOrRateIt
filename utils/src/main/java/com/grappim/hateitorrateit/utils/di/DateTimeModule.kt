package com.grappim.hateitorrateit.utils.di

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
annotation class DtfGDriveRfc3339

@[Module InstallIn(SingletonComponent::class)]
object DateTimeModule {

    private const val UI_PATTERN = "yyyy.MM.dd"
    private const val DOCUMENT_FOLDER = "yyyy-MM-dd_HH-mm-ss"

    @[Provides Singleton DtfToStore]
    fun provideDtfToStore(): DateTimeFormatter =
        DateTimeFormatter.ISO_DATE_TIME

    @[Provides Singleton DtfToDemonstrate]
    fun provideDtfToDemonstrate(): DateTimeFormatter =
        DateTimeFormatter.ofPattern(UI_PATTERN)

    @[Provides Singleton DtfGDriveRfc3339]
    fun provideDtfRfc3339(): DateTimeFormatter =
        DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @[Provides Singleton DtfDocumentFolder]
    fun provideDtfDocumentFolder(): DateTimeFormatter =
        DateTimeFormatter.ofPattern(DOCUMENT_FOLDER)
}
