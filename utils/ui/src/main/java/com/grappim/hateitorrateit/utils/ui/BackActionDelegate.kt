package com.grappim.hateitorrateit.utils.ui

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

interface BackActionDelegate {
    val onBackAction: Flow<Unit>
    suspend fun triggerBackAction()
}

class BackActionDelegateImpl : BackActionDelegate {
    private val _onBackAction = Channel<Unit>()
    override val onBackAction: Flow<Unit> = _onBackAction.receiveAsFlow()

    override suspend fun triggerBackAction() {
        _onBackAction.send(Unit)
    }
}
