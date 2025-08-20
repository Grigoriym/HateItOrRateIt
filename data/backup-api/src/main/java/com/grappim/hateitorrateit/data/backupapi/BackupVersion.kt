package com.grappim.hateitorrateit.data.backupapi

object BackupVersion {
    const val CURRENT_VERSION = "1.0.0"
    const val MIN_SUPPORTED_VERSION = "1.0.0"

    val SUPPORTED_VERSIONS = setOf(
        "1.0.0"
    )

    fun isVersionSupported(version: String): Boolean = version in SUPPORTED_VERSIONS

    fun isVersionCompatible(version: String): Boolean =
        compareVersions(version, MIN_SUPPORTED_VERSION) >= 0

    private fun compareVersions(version1: String, version2: String): Int {
        val parts1 = version1.split(".").map { it.toIntOrNull() ?: 0 }
        val parts2 = version2.split(".").map { it.toIntOrNull() ?: 0 }

        val maxLength = maxOf(parts1.size, parts2.size)
        val paddedParts1 = parts1 + List(maxLength - parts1.size) { 0 }
        val paddedParts2 = parts2 + List(maxLength - parts2.size) { 0 }

        for (i in 0 until maxLength) {
            val comparison = paddedParts1[i].compareTo(paddedParts2[i])
            if (comparison != 0) return comparison
        }
        return 0
    }
}
