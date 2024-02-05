package com.grappim.hateitorrateit.utils.di

import com.grappim.hateitorrateit.utils.mappers.ImageDataMapper
import com.grappim.hateitorrateit.utils.mappers.ImageDataMapperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface ImageDataModule {
    @Binds
    fun bindImageDataMapper(imageDataMapperImpl: ImageDataMapperImpl): ImageDataMapper
}
