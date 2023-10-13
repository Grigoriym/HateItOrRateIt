package com.grappim.hateitorrateit.ui.screens.home

import com.grappim.hateitorrateit.model.DocumentListUI

data class HomeViewState(
    val query: String = "",
    val onSearchQueryChanged: (query: String) -> Unit = {},

    val onClearQueryClicked: () -> Unit = {},

    val docs: List<DocumentListUI>,
)