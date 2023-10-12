package com.grappim.hateitorrateit.utils

import timber.log.Timber
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HashUtils @Inject constructor(

) {

    fun md5(file: File): String {
        Timber.d("get md5 from $file")
        val md = MessageDigest.getInstance("MD5")
        return file.inputStream().use { fis ->
            val buffer = ByteArray(8192)
            generateSequence {
                when (val bytesRead = fis.read(buffer)) {
                    -1 -> null
                    else -> bytesRead
                }
            }.forEach { bytesRead -> md.update(buffer, 0, bytesRead) }
            md.digest().joinToString("") { "%02x".format(it) }
        }
    }

}