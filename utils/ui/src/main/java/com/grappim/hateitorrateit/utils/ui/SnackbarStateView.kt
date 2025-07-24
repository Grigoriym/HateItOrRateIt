package com.grappim.hateitorrateit.utils.ui

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

interface SnackbarStateViewModel {
    val snackBarMessage: Flow<NativeText>
    suspend fun showSnackbarSuspend(message: NativeText)
}

class SnackbarStateViewModelImpl : SnackbarStateViewModel {
    private val _snackBarMessage = Channel<NativeText>()
    override val snackBarMessage: Flow<NativeText> = _snackBarMessage.receiveAsFlow()

    override suspend fun showSnackbarSuspend(message: NativeText) {
        _snackBarMessage.send(message)
    }
}
