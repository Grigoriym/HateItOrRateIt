package com.grappim.hateitorrateit.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.grappim.hateitorrateit.domain.ProductImageData
import com.grappim.hateitorrateit.utils.models.ImageData
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import javax.inject.Inject
import kotlin.test.assertEquals

private const val NAME = "test_name"
private const val URI =
    "content://media/picker/0/com.android.providers.media.photopicker/media/1001"
private const val MIME_TYPE = "image/png"
private const val FILES_DIR = "/data/user/0/com.grappim.hateitorrateit/utils/test"
private const val FOLDER_NAME = "data/products/test"

@HiltAndroidTest
class FileUtilsTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var hashUtils: HashUtils

    @Inject
    lateinit var dateTimeUtils: DateTimeUtils

    private val context = mockk<Context>(relaxed = true)
    private val contentResolver = mockk<ContentResolver>(relaxed = true)

    private lateinit var fileUtils: FileUtils

    @Before
    fun setUp() {
        hiltRule.inject()

        fileUtils = FileUtils(
            context = context,
            hashUtils = hashUtils,
            dateTimeUtils = dateTimeUtils,
        )

        every {
            context.contentResolver
        } returns contentResolver

        every {
            contentResolver.takePersistableUriPermission(
                any(),
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } just Runs
    }

    @Test
    fun toProductImageData_call_should_return_correct_ProductImageData() {
        val expected = ProductImageData(
            name = NAME,
            mimeType = "png",
            uriPath = URI,
            uriString = URI,
            size = 15,
            md5 = "test_md5",
        )
        val actual = fileUtils.toProductImageData(
            ImageData(
                uri = Uri.parse(URI),
                name = NAME,
                size = 15,
                mimeType = "png",
                md5 = "test_md5",
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getMainFolder_should_return_correct_folder() {
        every {
            context.filesDir
        } returns File(FILES_DIR)

        val expected = File(FOLDER_NAME)
        val actual = fileUtils.getMainFolder("test")
        assertEquals(expected, actual)
    }
}
