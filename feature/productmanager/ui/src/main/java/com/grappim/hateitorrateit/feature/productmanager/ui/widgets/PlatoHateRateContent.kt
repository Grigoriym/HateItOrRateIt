package com.grappim.hateitorrateit.feature.productmanager.ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.feature.productmanager.ui.R
import com.grappim.hateitorrateit.uikit.icons.PlatoIconType
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight
import com.grappim.hateitorrateit.uikit.utils.color
import com.grappim.hateitorrateit.uikit.utils.hateColors
import com.grappim.hateitorrateit.uikit.utils.rateColors
import com.grappim.hateitorrateit.uikit.widgets.PlatoCard
import com.grappim.hateitorrateit.uikit.widgets.PlatoIcon

const val PLATO_HATE_RATE_CONTENT_TAG = "plato_hate_rate_content_tag"

@Composable
fun PlatoHateRateContent(
    currentType: HateRateType,
    onTypeClick: (HateRateType) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonsSize = 80.dp
    val hateColors = ButtonDefaults.buttonColors(
        containerColor = currentType.hateColors()
    )
    val rateColors = ButtonDefaults.buttonColors(
        containerColor = currentType.rateColors()
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .size(buttonsSize),
                onClick = {
                    onTypeClick(HateRateType.HATE)
                },
                shape = CircleShape,
                colors = hateColors
            ) {
                PlatoIcon(imageVector = PlatoIconType.Hate.imageVector)
            }

            Text(
                text = when (currentType) {
                    HateRateType.RATE -> stringResource(id = R.string.i_rate_it)
                    HateRateType.HATE -> stringResource(id = R.string.i_hate_it)
                }
            )

            Button(
                modifier = Modifier
                    .size(buttonsSize),
                onClick = {
                    onTypeClick(HateRateType.RATE)
                },
                shape = CircleShape,
                colors = rateColors
            ) {
                PlatoIcon(imageVector = PlatoIconType.Rate.imageVector)
            }
        }
    }
}

@[Composable PreviewDarkLight]
private fun PlatoHateRateContentHatePreview() {
    HateItOrRateItTheme {
        PlatoHateRateContent(
            currentType = HateRateType.HATE,
            onTypeClick = {}
        )
    }
}

@[Composable PreviewDarkLight]
private fun PlatoHateRateContentRatePreview() {
    HateItOrRateItTheme {
        PlatoHateRateContent(
            currentType = HateRateType.RATE,
            onTypeClick = {}
        )
    }
}
