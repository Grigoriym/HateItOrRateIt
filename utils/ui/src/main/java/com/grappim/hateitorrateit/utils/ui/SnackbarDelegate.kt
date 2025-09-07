package com.grappim.hateitorrateit.utils.ui

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

interface SnackbarDelegate {
    val snackBarMessage: Flow<NativeText>
    suspend fun showSnackbar(message: NativeText)
}

class SnackbarDelegateImpl : SnackbarDelegate {
    private val _snackBarMessage = Channel<NativeText>()
    override val snackBarMessage: Flow<NativeText> = _snackBarMessage.receiveAsFlow()

    override suspend fun showSnackbar(message: NativeText) {
        _snackBarMessage.send(message)
    }
}
