package com.grappim.hateitorrateit.data.backupimpl.utils

import com.grappim.hateitorrateit.data.backupimpl.models.VersionCheckResult
import javax.inject.Inject

/**
 * Handles validation of backup versions during import operations
 */
class ImportVersionChecker @Inject constructor() {

    /**
     * Validates if the given backup version is supported for import
     * @param backupVersion The version number from the backup file
     * @return VersionCheckResult indicating if the version is valid and any error message
     */
    fun validateBackupVersion(backupVersion: Int): VersionCheckResult = when {
        !isVersionSupported(backupVersion) -> {
            VersionCheckResult(
                isValid = false,
                errorMessage = "Backup version $backupVersion is not supported. " +
                    "Minimum supported version: $MIN_SUPPORTED_VERSION, " +
                    "Maximum supported version: $CURRENT_VERSION"
            )
        }

        else -> VersionCheckResult(isValid = true)
    }

    /**
     * Checks if the given version is within the supported range
     * @param version The version to check
     * @return true if supported, false otherwise
     */
    private fun isVersionSupported(version: Int): Boolean =
        version in MIN_SUPPORTED_VERSION..CURRENT_VERSION

    companion object {
        const val MIN_SUPPORTED_VERSION = 1
        const val CURRENT_VERSION = 1
    }
}
