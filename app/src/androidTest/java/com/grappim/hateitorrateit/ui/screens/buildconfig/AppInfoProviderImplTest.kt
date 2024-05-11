package com.grappim.hateitorrateit.ui.screens.buildconfig

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.grappim.hateitorrateit.BuildConfig
import com.grappim.hateitorrateit.buildconfig.AppInfoProviderImpl
import com.grappim.hateitorrateit.ui.screens.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class AppInfoProviderImplTest {

    private val sut = AppInfoProviderImpl()

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testGetAppInfo_usesBuildConfigValues() {
        val expectedInfo = "${BuildConfig.VERSION_NAME} - ${BuildConfig.VERSION_CODE} - ${BuildConfig.BUILD_TYPE}"
        val actualInfo = sut.getAppInfo()
        assertEquals(expectedInfo, actualInfo)
    }
}
