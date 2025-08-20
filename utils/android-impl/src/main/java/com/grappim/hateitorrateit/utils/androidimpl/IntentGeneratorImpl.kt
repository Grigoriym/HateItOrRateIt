package com.grappim.hateitorrateit.utils.androidimpl

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ShareCompat
import com.grappim.hateitorrateit.utils.androidapi.IntentGenerator
import com.grappim.hateitorrateit.utils.filesapi.uri.UriParser
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IntentGeneratorImpl @Inject constructor(
    private val uriParser: UriParser,
    @ApplicationContext private val context: Context
) : IntentGenerator {
    override fun generateIntentToShareImage(uriString: String, mimeType: String): Intent {
        val uri = uriParser.parse(uriString)
        return ShareCompat.IntentBuilder(context)
            .setType(mimeType)
            .setStream(uri)
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    override fun generateAppSettingsIntent(): Intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )

    override fun generateOpenDownloadsFolderIntent(): Intent =
        Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
}
