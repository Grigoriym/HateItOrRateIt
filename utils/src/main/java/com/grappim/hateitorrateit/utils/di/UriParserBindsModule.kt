package com.grappim.hateitorrateit.utils.di

import com.grappim.hateitorrateit.utils.UriParser
import com.grappim.hateitorrateit.utils.UriParserImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface UriParserBindsModule {
    @Binds
    fun bindUriParser(uriParserImpl: UriParserImpl): UriParser
}
