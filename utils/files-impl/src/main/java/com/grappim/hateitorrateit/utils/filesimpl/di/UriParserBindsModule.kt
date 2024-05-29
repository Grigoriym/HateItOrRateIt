package com.grappim.hateitorrateit.utils.filesimpl.di

import com.grappim.hateitorrateit.utils.filesapi.uri.UriParser
import com.grappim.hateitorrateit.utils.filesimpl.uri.UriParserImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface UriParserBindsModule {
    @Binds
    fun bindUriParser(uriParserImpl: UriParserImpl): UriParser
}
