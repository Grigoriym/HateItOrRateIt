package com.grappim.hateitorrateit.appupdateimpl

import com.grappim.hateitorrateit.appupdateapi.AppUpdateChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@[Module InstallIn(ActivityComponent::class)]
interface PlayStoreAppUpdateModule {
    @Binds
    fun bindAppUpdateChecker(impl: PlayStoreAppUpdateCheckerImpl): AppUpdateChecker
}
