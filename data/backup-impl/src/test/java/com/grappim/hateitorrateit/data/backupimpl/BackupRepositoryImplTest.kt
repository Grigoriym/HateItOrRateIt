package com.grappim.hateitorrateit.data.backupimpl

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.data.backupapi.models.BackupError
import com.grappim.hateitorrateit.data.backupapi.models.BackupPhase
import com.grappim.hateitorrateit.data.backupapi.models.BackupResult
import com.grappim.hateitorrateit.data.backupapi.models.BackupState
import com.grappim.hateitorrateit.data.backupapi.models.ExportData
import com.grappim.hateitorrateit.data.backupimpl.utils.Constants.BACKUP_DATA_JSON
import com.grappim.hateitorrateit.data.backupimpl.utils.Constants.IMAGES_ZIP_FOLDER
import com.grappim.hateitorrateit.data.backupimpl.utils.ImportVersionChecker
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import com.grappim.hateitorrateit.testing.core.FakeFolderPathManager
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.Instant
import java.time.OffsetDateTime
import java.util.zip.ZipInputStream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class BackupRepositoryImplTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val contentResolver: ContentResolver = mockk()
    private val context: Context = mockk {
        every { contentResolver } returns this@BackupRepositoryImplTest.contentResolver
    }
    private val productsRepository: ProductsRepository = mockk()
    private val localDataStorage: LocalDataStorage = mockk {
        every { typeFlow } returns flowOf(HateRateType.RATE)
        every { darkThemeConfig } returns flowOf(DarkThemeConfig.DARK)
        every { analyticsCollectionEnabled } returns flowOf(true)
        every { crashesCollectionEnabled } returns flowOf(false)
    }
    private val appInfoProvider: AppInfoProvider = mockk {
        every { getAppInfo() } returns APP_INFO
    }
    private val dateTimeUtils: DateTimeUtils = mockk {
        every { getInstantNow() } returns Instant.ofEpochMilli(TIMESTAMP)
    }
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    private val backupUri: Uri = mockk()

    private lateinit var productFolder: File
    private lateinit var output: ByteArrayOutputStream

    @Before
    fun setup() {
        productFolder = tempFolder.newFolder(FOLDER_NAME)
        output = ByteArrayOutputStream()
        every { contentResolver.openOutputStream(backupUri) } returns output
    }

    private fun createSut(dispatcher: TestDispatcher) = BackupRepositoryImpl(
        context = context,
        productsRepository = productsRepository,
        localDataStorage = localDataStorage,
        appInfoProvider = appInfoProvider,
        folderPathManager = FakeFolderPathManager(listOf(productFolder)),
        json = json,
        ioDispatcher = dispatcher,
        dateTimeUtils = dateTimeUtils
    )

    @Test
    fun `backup emits all phases and completes with success`() = runTest {
        coEvery { productsRepository.getAllProducts() } returns persistentListOf(product())

        val states = createSut(StandardTestDispatcher(testScheduler))
            .createBackupWithProgress(backupUri)
            .toList()

        val phases = states.filterIsInstance<BackupState.Progress>().map { it.progress.phase }
        assertEquals(BackupPhase.INITIALIZING, phases.first())
        assertEquals(BackupPhase.COMPLETED, phases.last())
        assertTrue(BackupPhase.CREATING_BACKUP_FILE in phases)
        assertEquals(BackupState.Completed(BackupResult.Success), states.last())
    }

    @Test
    fun `backup zip contains json with metadata, settings and products`() = runTest {
        coEvery { productsRepository.getAllProducts() } returns persistentListOf(product())

        createSut(StandardTestDispatcher(testScheduler))
            .createBackupWithProgress(backupUri)
            .toList()

        val entries = readZip(output.toByteArray())
        val exportData = json.decodeFromString<ExportData>(
            entries.getValue(BACKUP_DATA_JSON).toString(Charsets.UTF_8)
        )

        assertEquals(ImportVersionChecker.CURRENT_VERSION, exportData.metadata.version)
        assertEquals(APP_INFO, exportData.metadata.appVersionName)
        assertEquals(TIMESTAMP, exportData.metadata.exportTimestamp)
        assertEquals(1, exportData.metadata.totalProducts)
        assertEquals(2, exportData.metadata.totalImages)

        assertEquals(HateRateType.RATE, exportData.settings.defaultType)
        assertEquals(DarkThemeConfig.DARK, exportData.settings.darkThemeConfig)
        assertTrue(exportData.settings.analyticsEnabled)
        assertEquals(false, exportData.settings.crashesEnabled)

        val exportedProduct = exportData.products.single()
        assertEquals(PRODUCT_NAME, exportedProduct.name)
        assertEquals(CREATED_DATE.toString(), exportedProduct.createdDate)
        assertEquals(
            listOf("$FOLDER_NAME/$IMAGE_1", "$FOLDER_NAME/$IMAGE_2"),
            exportedProduct.images.map { it.exportFileName }
        )
    }

    @Test
    fun `backup zip includes only image files that exist on disk`() = runTest {
        File(productFolder, IMAGE_1).writeBytes(IMAGE_BYTES)
        coEvery { productsRepository.getAllProducts() } returns persistentListOf(product())

        createSut(StandardTestDispatcher(testScheduler))
            .createBackupWithProgress(backupUri)
            .toList()

        val entries = readZip(output.toByteArray())
        val imageEntries = entries.keys.filter { it.startsWith("$IMAGES_ZIP_FOLDER/") }

        assertEquals(listOf("$IMAGES_ZIP_FOLDER/$FOLDER_NAME/$IMAGE_1"), imageEntries)
        assertTrue(IMAGE_BYTES.contentEquals(entries.getValue(imageEntries.single())))
    }

    @Test
    fun `backup completes with failure when output stream cannot be opened`() = runTest {
        coEvery { productsRepository.getAllProducts() } returns persistentListOf(product())
        every { contentResolver.openOutputStream(backupUri) } returns null

        val last = createSut(StandardTestDispatcher(testScheduler))
            .createBackupWithProgress(backupUri)
            .toList()
            .last()

        val result = assertIs<BackupResult.Failure>(assertIs<BackupState.Completed>(last).result)
        assertEquals(BackupError.UNKNOWN_ERROR, result.error)
    }

    @Test
    fun `backup completes with failure when repository throws`() = runTest {
        coEvery { productsRepository.getAllProducts() } throws IllegalStateException(ERROR)

        val last = createSut(StandardTestDispatcher(testScheduler))
            .createBackupWithProgress(backupUri)
            .toList()
            .last()

        val result = assertIs<BackupResult.Failure>(assertIs<BackupState.Completed>(last).result)
        assertEquals(ERROR, result.message)
    }

    private fun readZip(bytes: ByteArray): Map<String, ByteArray> {
        val entries = mutableMapOf<String, ByteArray>()
        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                entries[entry.name] = zip.readBytes()
                entry = zip.nextEntry
            }
        }
        return entries
    }

    private fun product() = Product(
        id = 1L,
        name = PRODUCT_NAME,
        images = listOf(image(IMAGE_1), image(IMAGE_2)),
        createdDate = CREATED_DATE,
        productFolderName = FOLDER_NAME,
        description = "desc",
        shop = "shop",
        type = HateRateType.HATE
    )

    private fun image(name: String) = ProductImage(
        imageId = name.hashCode().toLong(),
        name = name,
        mimeType = "image/jpeg",
        uriPath = "/path/$name",
        uriString = "content://$name",
        size = 10L,
        md5 = "md5-$name"
    )

    private companion object {
        const val APP_INFO = "1.0.0 (1)"
        const val TIMESTAMP = 1_700_000_000_000L
        const val FOLDER_NAME = "product_folder"
        const val PRODUCT_NAME = "Coffee"
        const val IMAGE_1 = "one.jpg"
        const val IMAGE_2 = "two.jpg"
        const val ERROR = "db is gone"
        val IMAGE_BYTES = byteArrayOf(1, 2, 3, 4)
        val CREATED_DATE: OffsetDateTime = OffsetDateTime.parse("2026-01-02T10:00:00Z")
    }
}
