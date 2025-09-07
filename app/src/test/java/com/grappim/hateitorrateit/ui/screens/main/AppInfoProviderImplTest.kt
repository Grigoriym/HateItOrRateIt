package com.grappim.hateitorrateit.ui.screens.main

import com.grappim.hateitorrateit.BuildConfig
import com.grappim.hateitorrateit.buildconfig.AppInfoProviderImpl
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(
    application = NonFirebaseApplication::class
)
class AppInfoProviderImplTest {

    private val sut = AppInfoProviderImpl()

    @Test
    fun `on getAppInfo usesBuildConfigValues`() {
        val expectedInfo = "${BuildConfig.VERSION_NAME} - " +
            "${BuildConfig.VERSION_CODE} - " +
            "${BuildConfig.BUILD_TYPE} " +
            BuildConfig.FLAVOR
        val actualInfo = sut.getAppInfo()

        assertEquals(expectedInfo, actualInfo)
    }
}
