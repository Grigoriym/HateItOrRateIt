package com.grappim.hateitorrateit.testing.core

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import java.io.File

@Suppress("UtilityClassWithPublicConstructor")
@Implements(FileProvider::class)
class ShadowFileProvider {

    companion object {

        @Suppress("UnusedParameter", "UnusedPrivateMember")
        @JvmStatic
        @Implementation
        fun getUriForFile(context: Context, authority: String, file: File): Uri {
            return Uri.parse("content://$authority/${file.name}")
        }
    }
}
