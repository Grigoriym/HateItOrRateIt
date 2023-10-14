package com.grappim.hateitorrateit.utils

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
annotation class DtfGDriveDocumentFolder

@Qualifier
annotation class DtfGDriveRfc3339

@[Module InstallIn(SingletonComponent::class)]
object DateTimeModule {

    private const val PATTERN_TO_DEMONSTRATE = "yyyy.MM.dd HH:mm:ss"
    private const val GOOGLE_DRIVE_DOCUMENT_FOLDER = "yyyy-MM-dd_HH-mm-ss"

    @[Provides Singleton DtfToStore]
    fun provideDtfToStore(): DateTimeFormatter =
        DateTimeFormatter.ISO_DATE_TIME

    @[Provides Singleton DtfToDemonstrate]
    fun provideDtfToDemonstrate(): DateTimeFormatter =
        DateTimeFormatter.ofPattern(PATTERN_TO_DEMONSTRATE)

    @[Provides Singleton DtfGDriveRfc3339]
    fun provideDtfRfc3339(): DateTimeFormatter =
        DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @[Provides Singleton DtfGDriveDocumentFolder]
    fun provideDtfGDriveDocumentFolder(): DateTimeFormatter =
        DateTimeFormatter.ofPattern(GOOGLE_DRIVE_DOCUMENT_FOLDER)
}
