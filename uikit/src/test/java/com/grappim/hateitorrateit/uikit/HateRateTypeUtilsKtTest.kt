package com.grappim.hateitorrateit.uikit

import androidx.compose.ui.graphics.Color
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.uikit.theme.AtomicTangerine
import com.grappim.hateitorrateit.uikit.theme.Feijoa
import com.grappim.hateitorrateit.utils.ui.PlatoIconType
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
            PlatoIconType.Hate.imageVector
        )
    }

    @Test
    fun `icon returns correct icon for RATE type`() {
        assertEquals(
            HateRateType.RATE.icon(),
            PlatoIconType.Rate.imageVector
        )
    }
}
