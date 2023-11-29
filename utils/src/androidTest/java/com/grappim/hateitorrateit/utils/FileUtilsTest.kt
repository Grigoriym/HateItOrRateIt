package com.grappim.hateitorrateit.utils

import android.content.Context
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

//@RunWith(AndroidJUnit4::class)
//@HiltAndroidTest
//class FileUtilsTest {
//
//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)
//
//    @Inject
//    lateinit var context: Context
//
//    @Before
//    fun setUp() {
//        hiltRule.inject()
//    }
//
//    @Test
//    fun testFileUtils() {
//        val fileUtils = FileUtils(context, HashUtils(), DateTimeUtils())
//        assertNotNull(fileUtils)
//    }
//}