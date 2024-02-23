package com.grappim.hateitorrateit.ui

import androidx.compose.ui.graphics.Color
import com.grappim.hateitorrateit.domain.HateRateType
import com.grappim.hateitorrateit.ui.theme.AtomicTangerine
import com.grappim.hateitorrateit.ui.theme.Feijoa
import com.grappim.hateitorrateit.ui.utils.PlatoIconType
import org.junit.Test
import kotlin.test.assertEquals

class HateRateTypeUtilsKtTest {

    @Test
    fun `hateColors returns correct colors for HATE type`() {
        assertEquals(
            HateRateType.HATE.hateColors(),
            AtomicTangerine
        )
    }

    @Test
    fun `hateColors returns correct colors for RATE type`() {
        assertEquals(
            HateRateType.RATE.hateColors(),
            Color.LightGray
        )
    }

    @Test
    fun `rateColors returns correct colors for RATE type`() {
        assertEquals(
            HateRateType.RATE.rateColors(),
            Feijoa
        )
    }

    @Test
    fun `rateColors returns correct colors for HATE type`() {
        assertEquals(
            HateRateType.HATE.rateColors(),
            Color.LightGray
        )
    }

    @Test
    fun `color returns AtomicTangerine for HATE type`() {
        assertEquals(
            HateRateType.HATE.color(),
            AtomicTangerine
        )
    }

    @Test
    fun `color returns Feijoa for RATE type`() {
        assertEquals(
            HateRateType.RATE.color(),
            Feijoa
        )
    }

    @Test
    fun `icon returns correct icon for HATE type`() {
        assertEquals(
            HateRateType.HATE.icon(),
            PlatoIconType.ThumbDown.imageVector
        )
    }

    @Test
    fun `icon returns correct icon for RATE type`() {
        assertEquals(
            HateRateType.RATE.icon(),
            PlatoIconType.ThumbUp.imageVector
        )
    }
}
