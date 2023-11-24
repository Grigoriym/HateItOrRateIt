package com.grappim.hateitorrateit.di

import com.grappim.hateitorrateit.data.workerapi.WorkerController
import com.grappim.hateitorrateit.data.workerimpl.WorkerControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface WorkerModule {

    @Binds
    fun bindWorkerController(
        workerControllerImpl: WorkerControllerImpl
    ): WorkerController
}
