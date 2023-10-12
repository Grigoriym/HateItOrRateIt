package com.grappim.hateitorrateit.core

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface SnackbarStateViewModel {
    val snackBarMessage: SharedFlow<LaunchedEffectResult<NativeText>>
    suspend fun setSnackbarMessageSuspend(message: NativeText)
    fun setSnackbarMessage(message: NativeText)
}

class SnackbarStateViewModelImpl : SnackbarStateViewModel {
    private val _snackBarMessage = MutableSharedFlow<LaunchedEffectResult<NativeText>>()
    override val snackBarMessage: SharedFlow<LaunchedEffectResult<NativeText>>
        get() = _snackBarMessage.asSharedFlow()

    override suspend fun setSnackbarMessageSuspend(message: NativeText) {
        _snackBarMessage.emit(
            LaunchedEffectResult(
                data = message
            )
        )
    }

    override fun setSnackbarMessage(message: NativeText) {
        _snackBarMessage.tryEmit(
            LaunchedEffectResult(
                data = message
            )
        )
    }
}