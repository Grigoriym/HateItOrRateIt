package com.grappim.hateitorrateit.testing.core

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.grappim.hateitorrateit.testing.domain.getRandomString
import java.io.File

fun Context.getRandomFile() = File(filesDir, getRandomString()).apply { mkdirs() }

fun Context.getUriForFile(file: File): Uri = FileProvider.getUriForFile(
    this,
    "$packageName.provider",
    file
)
