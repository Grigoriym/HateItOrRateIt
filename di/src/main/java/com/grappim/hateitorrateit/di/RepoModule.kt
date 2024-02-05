package com.grappim.hateitorrateit.di

import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.BackupImagesRepository
import com.grappim.hateitorrateit.data.repoimpl.ProductsRepositoryImpl
import com.grappim.hateitorrateit.data.repoimpl.BackupImagesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface RepoModule {
    @Binds
    fun bindProductsRepository(productsRepositoryImpl: ProductsRepositoryImpl): ProductsRepository

    @Binds
    fun bindTempImagesRepository(tempImagesRepositoryImpl: BackupImagesRepositoryImpl): BackupImagesRepository
}
