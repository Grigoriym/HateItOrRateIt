package com.grappim.hateitorrateit.utils.filesimpl.di

import com.grappim.hateitorrateit.utils.filesapi.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.filesimpl.mappers.ImageDataMapperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface ImageDataModule {
    @Binds
    fun bindImageDataMapper(imageDataMapperImpl: ImageDataMapperImpl): ImageDataMapper
}
