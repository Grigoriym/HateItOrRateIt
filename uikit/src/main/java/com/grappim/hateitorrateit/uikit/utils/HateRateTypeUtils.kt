package com.grappim.hateitorrateit.uikit.utils

import androidx.compose.ui.graphics.Color
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.uikit.icons.PlatoIconType
import com.grappim.hateitorrateit.uikit.theme.AtomicTangerine
import com.grappim.hateitorrateit.uikit.theme.Feijoa

fun deactivatedColor() = Color.LightGray

fun getColor(isEnabled: Boolean): Color = if (isEnabled) {
    Feijoa
} else {
    AtomicTangerine
}

fun HateRateType.hateColors() = if (this == HateRateType.HATE) {
    AtomicTangerine
} else {
    deactivatedColor()
}

fun HateRateType.rateColors() = if (this == HateRateType.RATE) {
    Feijoa
} else {
    deactivatedColor()
}

fun HateRateType.color() = if (this == HateRateType.HATE) {
    AtomicTangerine
} else {
    Feijoa
}

fun HateRateType.icon() = if (this == HateRateType.HATE) {
    PlatoIconType.Hate.imageVector
} else {
    PlatoIconType.Rate.imageVector
}
