package com.grappim.ui

import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.grappim.domain.HateRateType
import com.grappim.ui.theme.AtomicTangerine
import com.grappim.ui.theme.Feijoa

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
fun HateRateType.hateIcon() = Icons.Filled.ThumbDown

@Composable
fun HateRateType.rateIcon() = Icons.Filled.ThumbUp