package com.grappim.hateitorrateit.uikit.widgets.topbar

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Controller to set up the global top bar
 */
val LocalTopBarConfig = compositionLocalOf<TopBarController> {
    error("TopBarController not provided")
}

class TopBarController {
    val scope = MainScope()
    var config by mutableStateOf(TopBarConfig())
        private set

    fun update(config: TopBarConfig) {
        scope.launch(Dispatchers.Main.immediate) {
            this@TopBarController.config = config
        }
    }

    fun reset() {
        config = TopBarConfig()
    }
}
