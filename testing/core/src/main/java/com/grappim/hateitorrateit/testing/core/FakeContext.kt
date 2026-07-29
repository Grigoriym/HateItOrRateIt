package com.grappim.hateitorrateit.testing.core

import android.content.ContextWrapper
import java.io.File
import java.nio.file.Files

class FakeContext(
    private val filesDirOverride: File = Files.createTempDirectory("fake-context").toFile()
) : ContextWrapper(null) {
    override fun getFilesDir(): File = filesDirOverride
}
