package com.grappim.hateitorrateit.data.backupimpl

import android.content.Context
import android.net.Uri
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class ImportRepositoryImplTest {

    private val context: Context = mockk(relaxed = true)
    private val productsRepository: ProductsRepository = mockk(relaxed = true)
    private val localDataStorage: LocalDataStorage = mockk(relaxed = true)
    private val folderPathManager: FolderPathManager = mockk(relaxed = true)
    private val json: Json = Json { ignoreUnknownKeys = true }
    private val ioDispatcher = UnconfinedTestDispatcher()

    private val repository = ImportRepositoryImpl(
        context = context,
        productsRepository = productsRepository,
        localDataStorage = localDataStorage,
        folderPathManager = folderPathManager,
        json = json,
        ioDispatcher = ioDispatcher
    )

    @Test
    fun `canImportBackup returns false when file is not accessible`() = runTest {
        // Given: Backup file URI that returns null
        val backupFileUri = mockk<Uri>()
        val contentResolver = mockk<android.content.ContentResolver>()

        every { context.contentResolver } returns contentResolver
        every { contentResolver.openInputStream(backupFileUri) } returns null

        // When: checking if backup can be imported
        val result = repository.canImportBackup(backupFileUri)

        // Then: should return false
        assertFalse(result)
    }

    @Test
    fun `canImportBackup returns false when exception occurs`() = runTest {
        // Given: ContentResolver throws exception
        val backupFileUri = mockk<Uri>()
        val contentResolver = mockk<android.content.ContentResolver>()

        every { context.contentResolver } returns contentResolver
        every {
            contentResolver.openInputStream(backupFileUri)
        } throws RuntimeException("File access error")

        // When: checking if backup can be imported
        val result = repository.canImportBackup(backupFileUri)

        // Then: should return false
        assertFalse(result)
    }
}
