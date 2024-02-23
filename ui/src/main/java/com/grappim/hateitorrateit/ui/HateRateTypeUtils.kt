package com.grappim.hateitorrateit.ui

import androidx.compose.ui.graphics.Color
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.ui.theme.AtomicTangerine
import com.grappim.hateitorrateit.ui.theme.Feijoa
import com.grappim.hateitorrateit.ui.utils.PlatoIconType

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

fun hateIcon() = PlatoIconType.ThumbDown.imageVector

fun rateIcon() = PlatoIconType.ThumbUp.imageVector
