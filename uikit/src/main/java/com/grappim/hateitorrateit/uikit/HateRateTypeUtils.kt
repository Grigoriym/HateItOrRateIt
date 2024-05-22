package com.grappim.hateitorrateit.uikit

import androidx.compose.ui.graphics.Color
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.uikit.theme.AtomicTangerine
import com.grappim.hateitorrateit.uikit.theme.Feijoa
import com.grappim.hateitorrateit.utils.ui.PlatoIconType

fun deactivatedColor() = Color.LightGray

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
    hateIcon()
} else {
    rateIcon()
}

fun hateIcon() = PlatoIconType.Hate.imageVector

fun rateIcon() = PlatoIconType.Rate.imageVector
