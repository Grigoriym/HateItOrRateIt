package com.grappim.hateitorrateit.utils.di

import com.grappim.hateitorrateit.utils.productmanager.ProductImageManagerImpl
import com.grappim.hateitorrateit.utils.productmanager.ProductImageManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface ProductImageManagerBindsModule {
    @Binds
    fun bindProductImageManager(productImageManagerImpl: ProductImageManagerImpl): ProductImageManager
}
