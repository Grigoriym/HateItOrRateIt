package com.grappim.hateitorrateit.utils.androidimpl

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.grappim.hateitorrateit.testing.core.getRandomUri
import com.grappim.hateitorrateit.utils.androidapi.IntentGenerator
import com.grappim.hateitorrateit.utils.filesapi.uri.UriParser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class IntentGeneratorImplTest {

    private val uriParser: UriParser = mockk()
    private val context = RuntimeEnvironment.getApplication()

    private lateinit var sut: IntentGenerator

    @Before
    fun setUp() {
        sut = IntentGeneratorImpl(
            uriParser = uriParser,
            context = context
        )
    }

    @Test
    fun `test generateIntentToShareImage`() {
        val uriString = "content://some_uri"
        val mimeType = "image/jpeg"
        val uri = getRandomUri()

        every { uriParser.parse(uriString) } returns uri

        val intent = sut.generateIntentToShareImage(uriString, mimeType)

        assert(intent.action == Intent.ACTION_CHOOSER)
        assert(intent.hasExtra(Intent.EXTRA_INTENT))
        val sharingIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT, Intent::class.java)
        assert(sharingIntent?.action == Intent.ACTION_SEND)
        assert(sharingIntent?.type == mimeType)
        assert(sharingIntent?.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java) == uri)
        assert(sharingIntent?.flags?.and(Intent.FLAG_GRANT_READ_URI_PERMISSION) != 0)

        verify { uriParser.parse(uriString) }
    }

    @Test
    fun `test generateAppSettingsIntent`() {
        val intent = sut.generateAppSettingsIntent()

        assert(intent.action == Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        assert(intent.data == Uri.fromParts("package", context.packageName, null))
    }
}
