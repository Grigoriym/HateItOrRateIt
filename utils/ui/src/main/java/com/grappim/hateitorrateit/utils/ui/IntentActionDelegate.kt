package com.grappim.hateitorrateit.utils.ui

import android.content.Intent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

interface IntentActionDelegate {
    val intentAction: Flow<Intent>
    suspend fun useIntentAction(intent: Intent)
}

class IntentActionDelegateImpl : IntentActionDelegate {
    private val _intentAction = Channel<Intent>()
    override val intentAction: Flow<Intent> = _intentAction.receiveAsFlow()

    override suspend fun useIntentAction(intent: Intent) {
        _intentAction.send(intent)
    }
}
