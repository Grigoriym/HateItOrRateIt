package com.grappim.hateitorrateit.feature.productmanager.ui

sealed interface QuitStatus {
    data object Initial : QuitStatus
    data object InProgress : QuitStatus
    data object Finish : QuitStatus
}
