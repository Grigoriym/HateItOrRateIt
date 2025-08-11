package com.grappim.hateitorrateit.uikit.widgets.topbar

import com.grappim.hateitorrateit.utils.ui.NativeText

data class TopBarConfig(
    val state: TopBarState = TopBarState.Hidden
)

sealed interface TopBarState {
    data object Hidden : TopBarState
    data class Visible(
        val title: NativeText = NativeText.Empty,
        val topBarBackButtonState: TopBarBackButtonState = TopBarBackButtonState.Hidden
    ) : TopBarState
}

sealed interface TopBarBackButtonState {
    data object Hidden : TopBarBackButtonState
    data class Visible(
        val overrideBackHandlerAction: (() -> Unit)? = null
    ) : TopBarBackButtonState
}
