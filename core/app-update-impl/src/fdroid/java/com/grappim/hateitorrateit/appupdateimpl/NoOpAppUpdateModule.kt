package com.grappim.hateitorrateit.appupdateimpl

import com.grappim.hateitorrateit.appupdateapi.AppUpdateChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface NoOpAppUpdateModule {
    @Binds
    fun bindAppUpdateChecker(impl: NoOpAppUpdateCheckerImpl): AppUpdateChecker
}
