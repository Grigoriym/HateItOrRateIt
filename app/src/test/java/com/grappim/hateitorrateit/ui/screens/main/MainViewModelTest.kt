package com.grappim.hateitorrateit.ui.screens.main

import com.grappim.hateitorrateit.data.workerapi.WorkerController
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class MainViewModelTest {
    private val workerController: WorkerController = mockk()

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup() {
        every { workerController.startCleaning() } just Runs

        mainViewModel = MainViewModel(workerController)
    }

    @Test
    fun `on initializing should call startCleaning`() {
        verify { workerController.startCleaning() }
    }
}
