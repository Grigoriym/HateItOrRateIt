package com.grappim.hateitorrateit.data.backupapi

object BackupVersion {
    const val CURRENT_VERSION = 1
    const val MIN_SUPPORTED_VERSION = 1

    fun isVersionSupported(version: Int): Boolean = version >= MIN_SUPPORTED_VERSION
}
