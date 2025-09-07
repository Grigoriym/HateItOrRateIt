package com.grappim.hateitorrateit.feature.settings.ui.screen.product

import com.grappim.hateitorrateit.data.repoapi.models.HateRateType

data class SettingsProductViewState(
    val type: HateRateType = HateRateType.HATE,
    val setNewType: () -> Unit = {}
)
