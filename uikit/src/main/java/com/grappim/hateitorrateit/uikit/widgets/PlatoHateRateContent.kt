package com.grappim.hateitorrateit.uikit.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.uikit.color
import com.grappim.hateitorrateit.uikit.hateColors
import com.grappim.hateitorrateit.uikit.rateColors
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.ThemePreviews
import com.grappim.hateitorrateit.utils.ui.PlatoIconType

const val PLATO_HATE_RATE_CONTENT_TAG = "plato_hate_rate_content_tag"

@Composable
fun PlatoHateRateContent(
    modifier: Modifier = Modifier,
    currentType: HateRateType,
    onTypeClicked: (HateRateType) -> Unit
) {
    val buttonsSize = 80.dp
    val hateColors = ButtonDefaults.buttonColors(
        backgroundColor = currentType.hateColors()
    )
    val rateColors = ButtonDefaults.buttonColors(
        backgroundColor = currentType.rateColors()
    )
    PlatoCard(
        modifier = modifier
            .padding(top = 12.dp, bottom = 12.dp)
            .padding(horizontal = 32.dp)
            .fillMaxWidth()
            .testTag(PLATO_HATE_RATE_CONTENT_TAG),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(8.dp, currentType.color())
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .size(buttonsSize),
                onClick = {
                    onTypeClicked(HateRateType.HATE)
                },
                shape = CircleShape,
                colors = hateColors
            ) {
                PlatoIcon(imageVector = PlatoIconType.Hate.imageVector)
            }

            Text(
                text = when (currentType) {
                    HateRateType.RATE -> "I Like it"
                    HateRateType.HATE -> "I Hate it"
                }
            )

            Button(
                modifier = Modifier
                    .size(buttonsSize),
                onClick = {
                    onTypeClicked(HateRateType.RATE)
                },
                shape = CircleShape,
                colors = rateColors
            ) {
                PlatoIcon(imageVector = PlatoIconType.Rate.imageVector)
            }
        }
    }
}

@[Composable ThemePreviews]
private fun PlatoHateRateContentHatePreview() {
    HateItOrRateItTheme {
        PlatoHateRateContent(
            currentType = HateRateType.HATE,
            onTypeClicked = {}
        )
    }
}

@[Composable ThemePreviews]
private fun PlatoHateRateContentRatePreview() {
    HateItOrRateItTheme {
        PlatoHateRateContent(
            currentType = HateRateType.RATE,
            onTypeClicked = {}
        )
    }
}
