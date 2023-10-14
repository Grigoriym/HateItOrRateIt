package com.grappim.hateitorrateit.utils

import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.grappim.hateitorrateit.core.HateRateType
import com.grappim.ui.theme.Cinnabar
import com.grappim.ui.theme.FruitSalad

@Composable
fun deactivatedColors() = ButtonDefaults.buttonColors(
    backgroundColor = Color.LightGray
)

@Composable
fun HateRateType.hateColors() = if (this == HateRateType.HATE) {
    ButtonDefaults.buttonColors(
        backgroundColor = Cinnabar
    )
} else {
    deactivatedColors()
}

@Composable
fun HateRateType.rateColors() = if (this == HateRateType.RATE) {
    ButtonDefaults.buttonColors(
        backgroundColor = FruitSalad
    )
} else {
    deactivatedColors()
}

@Composable
fun HateRateType.color() = if (this == HateRateType.HATE) {
    Cinnabar
} else {
    FruitSalad
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