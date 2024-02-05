package com.grappim.hateitorrateit.ui.screens.productmanager

sealed interface QuitStatus {
    data object Initial : QuitStatus
    data object InProgress : QuitStatus
    data object Finish : QuitStatus
}
