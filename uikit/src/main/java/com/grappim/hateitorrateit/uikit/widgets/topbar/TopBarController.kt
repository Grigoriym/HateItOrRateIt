package com.grappim.hateitorrateit.uikit.widgets.topbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Controller to set up the global top bar
 */
val LocalTopBarConfig = compositionLocalOf<TopBarController> {
    error("TopBarController not provided")
}

class TopBarController {
    var config by mutableStateOf(TopBarConfig())
        private set

    fun update(config: TopBarConfig) {
        this.config = config
    }

    fun onScreenDispose(screenConfig: TopBarConfig) {
        if (this.config == screenConfig) {
            reset()
        }
    }

    fun reset() {
        config = TopBarConfig()
    }
}

@Composable
fun TopBarEffect(
    config: TopBarConfig,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    topBarController: TopBarController = LocalTopBarConfig.current
) {
    val currentConfig by rememberUpdatedState(config)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                topBarController.update(currentConfig)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
