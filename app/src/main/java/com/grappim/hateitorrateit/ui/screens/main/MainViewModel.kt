package com.grappim.hateitorrateit.ui.screens.main

import androidx.lifecycle.ViewModel
import com.grappim.hateitorrateit.data.workerapi.WorkerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    workerController: WorkerController
) : ViewModel() {

    init {
        workerController.startCleaning()
    }
}
