package com.grappim.hateitorrateit.utils.filesimpl.di

import com.grappim.hateitorrateit.utils.filesapi.productmanager.ProductImageManager
import com.grappim.hateitorrateit.utils.filesimpl.productmanager.ProductImageManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface ProductImageManagerBindsModule {
    @Binds
    fun bindProductImageManager(
        productImageManagerImpl: ProductImageManagerImpl
    ): ProductImageManager
}
