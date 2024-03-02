package com.grappim.hateitorrateit.ui.screens.main

import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.workerapi.WorkerController
import com.grappim.hateitorrateit.domain.DarkThemeConfig
import com.grappim.hateitorrateit.testing.MainDispatcherRule
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

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup() {
        every { workerController.startCleaning() } just Runs
        every { localDataStorage.darkThemeConfig } returns flowOf(DarkThemeConfig.default())

        mainViewModel = MainViewModel(workerController, localDataStorage)
    }

    @Test
    fun `on initializing should call startCleaning`() {
        verify { workerController.startCleaning() }
    }
}
