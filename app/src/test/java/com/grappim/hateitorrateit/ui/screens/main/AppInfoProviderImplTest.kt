package com.grappim.hateitorrateit.ui.screens.main

import com.grappim.hateitorrateit.BuildConfig
import com.grappim.hateitorrateit.buildconfig.AppInfoProviderImpl
import org.junit.Test
import kotlin.test.assertEquals

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
