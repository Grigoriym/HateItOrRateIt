package com.grappim.hateitorrateit.utils.androidimpl.di

import com.grappim.hateitorrateit.utils.androidapi.GalleryInteractions
import com.grappim.hateitorrateit.utils.androidapi.IntentGenerator
import com.grappim.hateitorrateit.utils.androidimpl.GalleryInteractionsImpl
import com.grappim.hateitorrateit.utils.androidimpl.IntentGeneratorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface AndroidUtilsModule {
    @Binds
    fun bindIntentGenerator(intentGeneratorImpl: IntentGeneratorImpl): IntentGenerator

    @Binds
    fun bindGalleryInteractions(
        galleryInteractionsImpl: GalleryInteractionsImpl
    ): GalleryInteractions
}
