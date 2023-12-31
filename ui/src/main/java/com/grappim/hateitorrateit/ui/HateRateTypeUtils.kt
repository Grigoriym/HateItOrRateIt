package com.grappim.hateitorrateit.ui

import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.ui.theme.AtomicTangerine
import com.grappim.hateitorrateit.ui.theme.Feijoa
import com.grappim.hateitorrateit.ui.utils.PlatoIconType

@Composable
fun deactivatedColors() = ButtonDefaults.buttonColors(
    backgroundColor = Color.LightGray
)

@Composable
fun HateRateType.hateColors() = if (this == HateRateType.HATE) {
    ButtonDefaults.buttonColors(
        backgroundColor = AtomicTangerine
    )
} else {
    deactivatedColors()
}

@Composable
fun HateRateType.rateColors() = if (this == HateRateType.RATE) {
    ButtonDefaults.buttonColors(
        backgroundColor = Feijoa
    )
} else {
    deactivatedColors()
}

@Composable
fun HateRateType.color() = if (this == HateRateType.HATE) {
    AtomicTangerine
} else {
    Feijoa
}

@Composable
fun HateRateType.icon() = if (this == HateRateType.HATE) {
    hateIcon()
} else {
    rateIcon()
}

@Composable
fun HateRateType.hateIcon() = PlatoIconType.ThumbDown.imageVector

@Composable
fun HateRateType.rateIcon() = PlatoIconType.ThumbUp.imageVector