package com.grappim.hateitorrateit.ui.screens.main

import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.data.remoteconfigapi.RemoteConfigsListener
import com.grappim.hateitorrateit.data.workerapi.WorkerController
import com.grappim.hateitorrateit.testing.core.MainDispatcherRule
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val workerController: WorkerController = mockk()
    private val localDataStorage: LocalDataStorage = mockk()
    private val remoteConfigsListener: RemoteConfigsListener = mockk()

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup() {
        every { workerController.startCleaning() } just Runs
        every { localDataStorage.darkThemeConfig } returns flowOf(DarkThemeConfig.default())
        every { remoteConfigsListener.inAppUpdateEnabled } returns flowOf(true)

        mainViewModel = MainViewModel(
            workerController = workerController,
            localDataStorage = localDataStorage,
            remoteConfigsListener = remoteConfigsListener
        )
    }

    @Test
    fun `on initializing should call startCleaning`() {
        verify { workerController.startCleaning() }
    }
}
