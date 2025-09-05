package com.grappim.hateitorrateit.data.backupimpl.version

import com.grappim.hateitorrateit.data.backupimpl.utils.ImportVersionChecker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class ImportVersionCheckerTest {

    private val sut = ImportVersionChecker()

    @Test
    fun `validateBackupVersion returns valid for supported version 1`() {
        val result = sut.validateBackupVersion(1)

        assertTrue("Version 1 should be supported", result.isValid)
        assertEquals("Error message should be empty for valid version", "", result.errorMessage)
    }

    @Test
    fun `validateBackupVersion returns invalid for version 0`() {
        val result = sut.validateBackupVersion(0)

        assertFalse("Version 0 should not be supported", result.isValid)
        assertTrue(
            "Error message should mention unsupported version",
            result.errorMessage.contains("Backup version 0 is not supported")
        )
        assertTrue(
            "Error message should mention minimum supported version",
            result.errorMessage.contains("Minimum supported version: 1")
        )
        assertTrue(
            "Error message should mention maximum supported version",
            result.errorMessage.contains("Maximum supported version: 1")
        )
    }

    @Test
    fun `validateBackupVersion returns invalid for negative version`() {
        val result = sut.validateBackupVersion(-1)

        assertFalse("Negative version should not be supported", result.isValid)
        assertTrue(
            "Error message should mention unsupported version",
            result.errorMessage.contains("Backup version -1 is not supported")
        )
    }

    @Test
    fun `validateBackupVersion returns invalid for version higher than supported`() {
        val result = sut.validateBackupVersion(2)

        assertFalse("Version 2 should not be supported yet", result.isValid)
        assertTrue(
            "Error message should mention unsupported version",
            result.errorMessage.contains("Backup version 2 is not supported")
        )
    }

    @Test
    fun `validateBackupVersion returns invalid for very high version`() {
        val result = sut.validateBackupVersion(999)

        assertFalse("Version 999 should not be supported", result.isValid)
        assertTrue(
            "Error message should mention unsupported version",
            result.errorMessage.contains("Backup version 999 is not supported")
        )
    }

    @Test
    fun `validateBackupVersion error message contains version range information`() {
        val result = sut.validateBackupVersion(5)

        assertFalse("Version 5 should not be supported", result.isValid)

        val errorMessage = result.errorMessage
        assertTrue(
            "Error message should contain 'Minimum supported version: 1'",
            errorMessage.contains("Minimum supported version: 1")
        )
        assertTrue(
            "Error message should contain 'Maximum supported version: 1'",
            errorMessage.contains("Maximum supported version: 1")
        )
    }

    @Test
    fun `validateBackupVersion with boundary values`() {
        // Test exact boundary values around the supported range

        // Just below minimum
        val belowMin = sut.validateBackupVersion(0)
        assertFalse("Version 0 should be invalid", belowMin.isValid)

        // Minimum supported
        val minVersion = sut.validateBackupVersion(1)
        assertTrue("Version 1 should be valid", minVersion.isValid)

        // Just above maximum (currently same as minimum)
        val aboveMax = sut.validateBackupVersion(2)
        assertFalse("Version 2 should be invalid", aboveMax.isValid)
    }

    @Test
    fun `validateBackupVersion handles edge cases`() {
        // Test various edge cases
        val testCases = mapOf(
            Int.MIN_VALUE to false,
            -1000 to false,
            -1 to false,
            0 to false,
            1 to true, // Only currently supported version
            2 to false,
            1000 to false,
            Int.MAX_VALUE to false
        )

        testCases.forEach { (version, expectedValid) ->
            val result = sut.validateBackupVersion(version)
            assertEquals(
                "Version $version validity should be $expectedValid",
                expectedValid,
                result.isValid
            )
        }
    }

    @Test
    fun `validateBackupVersion error message format is consistent`() {
        val invalidVersions = listOf(-1, 0, 2, 100)

        invalidVersions.forEach { version ->
            val result = sut.validateBackupVersion(version)

            assertFalse("Version $version should be invalid", result.isValid)

            val errorMessage = result.errorMessage
            assertTrue(
                "Error message should start with version info for version $version",
                errorMessage.startsWith("Backup version $version is not supported")
            )
            assertTrue(
                "Error message should contain minimum version info for version $version",
                errorMessage.contains("Minimum supported version:")
            )
            assertTrue(
                "Error message should contain maximum version info for version $version",
                errorMessage.contains("Maximum supported version:")
            )
        }
    }
}
