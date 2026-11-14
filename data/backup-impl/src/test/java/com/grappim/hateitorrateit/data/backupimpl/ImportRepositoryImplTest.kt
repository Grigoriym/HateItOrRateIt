package com.grappim.hateitorrateit.data.backupimpl

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.grappim.hateitorrateit.data.backupapi.models.ExportData
import com.grappim.hateitorrateit.data.backupapi.models.ExportMetadata
import com.grappim.hateitorrateit.data.backupapi.models.ImportError
import com.grappim.hateitorrateit.data.backupapi.models.ImportMode
import com.grappim.hateitorrateit.data.backupapi.models.ImportPhase
import com.grappim.hateitorrateit.data.backupapi.models.ImportResult
import com.grappim.hateitorrateit.data.backupapi.models.ImportState
import com.grappim.hateitorrateit.data.backupapi.models.ProductExport
import com.grappim.hateitorrateit.data.backupapi.models.ProductImageExport
import com.grappim.hateitorrateit.data.backupapi.models.SettingsExport
import com.grappim.hateitorrateit.data.backupimpl.utils.Constants.BACKUP_DATA_JSON
import com.grappim.hateitorrateit.data.backupimpl.utils.Constants.IMAGES_ZIP_FOLDER
import com.grappim.hateitorrateit.data.backupimpl.utils.ImportVersionChecker
import com.grappim.hateitorrateit.data.backupimpl.utils.ProductConflictDetector
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.CreateProduct
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.testing.core.FakeFolderPathManager
import com.grappim.hateitorrateit.utils.filesapi.urimanager.FileUriManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.OffsetDateTime
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ImportRepositoryImplTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val contentResolver: ContentResolver = mockk()
    private val context: Context = mockk {
        every { contentResolver } returns this@ImportRepositoryImplTest.contentResolver
    }
    private val productsRepository: ProductsRepository = mockk()
    private val localDataStorage: LocalDataStorage = mockk(relaxed = true)
    private val fileUriManager: FileUriManager = mockk()
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    private val backupUri: Uri = mockk()
    private val importedProducts = mutableListOf<CreateProduct>()

    private lateinit var productFolder: File
    private lateinit var folderPathManager: FakeFolderPathManager

    @Before
    fun setup() {
        productFolder = tempFolder.newFolder("target")
        folderPathManager = FakeFolderPathManager(listOf(productFolder))
        coEvery { productsRepository.getAllProducts() } returns persistentListOf()
        val productSlot = slot<CreateProduct>()
        coEvery { productsRepository.importProduct(capture(productSlot)) } answers {
            importedProducts += productSlot.captured
            importedProducts.size.toLong()
        }
        val fileSlot = slot<File>()
        every { fileUriManager.getFileUri(capture(fileSlot)) } answers {
            val file = fileSlot.captured
            mockk {
                every { path } returns file.path
                every { this@mockk.toString() } returns "content://${file.name}"
            }
        }
    }

    private suspend fun TestScope.import(
        mode: ImportMode = ImportMode.CREATE_NEW
    ): List<ImportState> = ImportRepositoryImpl(
        context = context,
        productsRepository = productsRepository,
        localDataStorage = localDataStorage,
        folderPathManager = folderPathManager,
        json = json,
        fileUriManager = fileUriManager,
        versionChecker = ImportVersionChecker(),
        conflictDetector = ProductConflictDetector(productsRepository),
        ioDispatcher = StandardTestDispatcher(testScheduler)
    ).importBackupWithProgress(backupUri, mode).toList()

    private fun givenBackup(
        exportData: ExportData? = exportData(),
        images: Map<String, ByteArray> = mapOf(IMAGE_ENTRY to IMAGE_BYTES)
    ) {
        val bytes = ByteArrayOutputStream().also { out ->
            ZipOutputStream(out).use { zip ->
                exportData?.let {
                    zip.putNextEntry(ZipEntry(BACKUP_DATA_JSON))
                    zip.write(json.encodeToString(it).toByteArray())
                    zip.closeEntry()
                }
                images.forEach { (name, data) ->
                    zip.putNextEntry(ZipEntry(name))
                    zip.write(data)
                    zip.closeEntry()
                }
            }
        }.toByteArray()
        every { contentResolver.openInputStream(backupUri) } answers { ByteArrayInputStream(bytes) }
    }

    @Test
    fun `create new imports settings, product and image`() = runTest {
        givenBackup()

        val states = import()

        val result = assertIs<ImportResult.Success>(completedResult(states))
        assertEquals(1, result.importedProducts)
        assertEquals(1, result.importedImages)
        assertTrue(result.importedSettings)

        coVerify { localDataStorage.changeTypeTo(HateRateType.RATE) }
        coVerify { localDataStorage.setDarkThemeConfig(DarkThemeConfig.LIGHT) }
        coVerify { localDataStorage.setAnalyticsCollectionEnabled(false) }
        coVerify { localDataStorage.setCrashesCollectionEnabled(true) }

        val imported = importedProducts.single()
        assertEquals(0L, imported.id)
        assertEquals(PRODUCT_NAME, imported.name)
        assertEquals(FOLDER_NAME, imported.productFolderName)
        assertEquals(OffsetDateTime.parse(CREATED_DATE), imported.createdDate)
        val image = imported.images.single()
        assertEquals(IMAGE_NAME, image.name)
        assertEquals("content://$IMAGE_NAME", image.uriString)
        assertEquals(IMAGE_BYTES.size.toLong(), image.size)
        assertTrue(IMAGE_BYTES.contentEquals(File(productFolder, IMAGE_NAME).readBytes()))
    }

    @Test
    fun `progress goes through extracting, validating and ends with completed`() = runTest {
        givenBackup()

        val phases = import()
            .filterIsInstance<ImportState.Progress>()
            .map { it.progress.phase }

        assertEquals(
            listOf(
                ImportPhase.EXTRACTING_DATA,
                ImportPhase.VALIDATING_BACKUP,
                ImportPhase.IMPORTING_SETTINGS,
                ImportPhase.DETECTING_CONFLICTS,
                ImportPhase.IMPORTING_PRODUCTS,
                ImportPhase.COMPLETED
            ),
            phases
        )
    }

    @Test
    fun `missing image in archive yields partial success`() = runTest {
        givenBackup(images = emptyMap())

        val result = assertIs<ImportResult.PartialSuccess>(completedResult(import()))

        assertEquals(1, result.importedProducts)
        assertEquals(0, result.importedImages)
        assertEquals(listOf(IMAGE_NAME), result.failedImages)
    }

    @Test
    fun `skip conflicts does not import matching product`() = runTest {
        givenBackup()
        coEvery { productsRepository.getAllProducts() } returns persistentListOf(existingProduct())

        val result = assertIs<ImportResult.Success>(
            completedResult(import(ImportMode.SKIP_CONFLICTS))
        )

        assertEquals(0, result.importedProducts)
        assertTrue(importedProducts.isEmpty())
    }

    @Test
    fun `skip conflicts imports product without a match`() = runTest {
        givenBackup()
        coEvery { productsRepository.getAllProducts() } returns persistentListOf(
            existingProduct().copy(name = "Something else")
        )

        val result = assertIs<ImportResult.Success>(
            completedResult(import(ImportMode.SKIP_CONFLICTS))
        )

        assertEquals(1, result.importedProducts)
        assertEquals(0L, importedProducts.single().id)
    }

    @Test
    fun `replace existing reuses id and folder of the matching product`() = runTest {
        givenBackup()
        coEvery { productsRepository.getAllProducts() } returns persistentListOf(existingProduct())

        assertIs<ImportResult.Success>(completedResult(import(ImportMode.REPLACE_EXISTING)))

        val imported = importedProducts.single()
        assertEquals(EXISTING_ID, imported.id)
        assertEquals(EXISTING_FOLDER, imported.productFolderName)
        assertEquals(listOf(EXISTING_FOLDER), folderPathManager.getMainFolderCalls)
    }

    @Test
    fun `replace existing without a match creates a new product`() = runTest {
        givenBackup()

        assertIs<ImportResult.Success>(completedResult(import(ImportMode.REPLACE_EXISTING)))

        val imported = importedProducts.single()
        assertEquals(0L, imported.id)
        assertEquals(FOLDER_NAME, imported.productFolderName)
    }

    @Test
    fun `unsupported backup version fails`() = runTest {
        givenBackup(exportData = exportData(version = 99))

        val result = assertIs<ImportResult.Failure>(completedResult(import()))

        assertEquals(ImportError.UNSUPPORTED_VERSION, result.error)
        assertTrue(importedProducts.isEmpty())
    }

    @Test
    fun `archive without backup json fails`() = runTest {
        givenBackup(exportData = null)

        val result = assertIs<ImportResult.Failure>(completedResult(import()))

        assertEquals(ImportError.UNKNOWN_ERROR, result.error)
    }

    @Test
    fun `unreadable backup file fails`() = runTest {
        every { contentResolver.openInputStream(backupUri) } returns null

        val result = assertIs<ImportResult.Failure>(completedResult(import()))

        assertEquals(ImportError.UNKNOWN_ERROR, result.error)
    }

    @Test
    fun `settings failure is reported as a warning`() = runTest {
        givenBackup()
        coEvery { localDataStorage.changeTypeTo(any()) } throws IllegalStateException("boom")

        val result = assertIs<ImportResult.PartialSuccess>(completedResult(import()))

        assertFalse(result.importedSettings)
        assertEquals(1, result.importedProducts)
        assertTrue(result.warnings.single().contains("settings"))
    }

    @Test
    fun `product failure is reported as a warning`() = runTest {
        givenBackup()
        coEvery { productsRepository.importProduct(any()) } throws IllegalStateException("boom")

        val result = assertIs<ImportResult.PartialSuccess>(completedResult(import()))

        assertEquals(0, result.importedProducts)
        assertEquals(listOf("Failed to import product: $PRODUCT_NAME"), result.warnings)
    }

    private fun completedResult(states: List<ImportState>): ImportResult =
        assertIs<ImportState.Completed>(states.last()).result

    private fun exportData(version: Int = ImportVersionChecker.CURRENT_VERSION) = ExportData(
        metadata = ExportMetadata(
            version = version,
            appVersionName = "1.0.0",
            exportTimestamp = 0L,
            deviceInfo = "device",
            totalProducts = 1,
            totalImages = 1
        ),
        products = listOf(
            ProductExport(
                id = 7L,
                name = PRODUCT_NAME,
                description = DESCRIPTION,
                shop = SHOP,
                type = HateRateType.HATE,
                createdDate = CREATED_DATE,
                productFolderName = FOLDER_NAME,
                images = listOf(
                    ProductImageExport(
                        imageId = 3L,
                        originalName = IMAGE_NAME,
                        exportFileName = "$FOLDER_NAME/$IMAGE_NAME",
                        mimeType = "image/jpeg",
                        size = 999L,
                        md5 = "md5"
                    )
                )
            )
        ),
        settings = SettingsExport(
            defaultType = HateRateType.RATE,
            darkThemeConfig = DarkThemeConfig.LIGHT,
            analyticsEnabled = false,
            crashesEnabled = true
        )
    )

    private fun existingProduct() = Product(
        id = EXISTING_ID,
        name = PRODUCT_NAME.uppercase(),
        images = emptyList(),
        createdDate = OffsetDateTime.parse(CREATED_DATE),
        productFolderName = EXISTING_FOLDER,
        description = DESCRIPTION,
        shop = SHOP,
        type = HateRateType.HATE
    )

    private companion object {
        const val PRODUCT_NAME = "Coffee"
        const val DESCRIPTION = "desc"
        const val SHOP = "shop"
        const val FOLDER_NAME = "folder_from_backup"
        const val EXISTING_FOLDER = "existing_folder"
        const val EXISTING_ID = 42L
        const val IMAGE_NAME = "img.jpg"
        const val IMAGE_ENTRY = "$IMAGES_ZIP_FOLDER/$FOLDER_NAME/$IMAGE_NAME"
        const val CREATED_DATE = "2026-01-02T10:00Z"
        val IMAGE_BYTES = byteArrayOf(9, 8, 7)
    }
}
