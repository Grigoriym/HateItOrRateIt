@file:Suppress("NestedBlockDepth")

package com.grappim.hateitorrateit.data.backupimpl

import android.content.Context
import android.net.Uri
import com.grappim.hateitorrateit.core.async.IoDispatcher
import com.grappim.hateitorrateit.data.backupapi.BackupVersion
import com.grappim.hateitorrateit.data.backupapi.ImportRepository
import com.grappim.hateitorrateit.data.backupapi.models.ExportData
import com.grappim.hateitorrateit.data.backupapi.models.ImportError
import com.grappim.hateitorrateit.data.backupapi.models.ImportPhase
import com.grappim.hateitorrateit.data.backupapi.models.ImportProgress
import com.grappim.hateitorrateit.data.backupapi.models.ImportResult
import com.grappim.hateitorrateit.data.backupapi.models.ImportState
import com.grappim.hateitorrateit.data.backupapi.models.ProductExport
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.CreateProduct
import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
import java.time.OffsetDateTime
import java.util.zip.ZipInputStream
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class ImportRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val productsRepository: ProductsRepository,
    private val localDataStorage: LocalDataStorage,
    private val folderPathManager: FolderPathManager,
    private val json: Json,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ImportRepository {

    override suspend fun importBackupWithProgress(backupFileUri: Uri): Flow<ImportState> =
        channelFlow {
            try {
                send(
                    ImportState.Progress(
                        ImportProgress(ImportPhase.INITIALIZING, 0, 1, "Starting import")
                    )
                )

                send(
                    ImportState.Progress(
                        ImportProgress(
                            ImportPhase.VALIDATING_BACKUP,
                            0,
                            1,
                            "Validating backup file"
                        )
                    )
                )

                val result = performImportWithProgress(backupFileUri) { progress ->
                    send(ImportState.Progress(progress))
                }

                send(
                    ImportState.Progress(
                        ImportProgress(
                            phase = ImportPhase.COMPLETED,
                            itemsProcessed = 1,
                            totalItems = 1,
                            currentItem = "Import completed"
                        )
                    )
                )
                send(ImportState.Completed(result))
            } catch (e: CancellationException) {
                throw e
            } catch (e: TimeoutCancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Import failed")
                send(
                    ImportState.Completed(
                        ImportResult.Failure(
                            error = ImportError.UNKNOWN_ERROR,
                            message = e.message ?: "Unknown error"
                        )
                    )
                )
            }
        }.flowOn(ioDispatcher)

    override suspend fun canImportBackup(backupFileUri: Uri): Boolean = try {
        context.contentResolver.openInputStream(backupFileUri)?.use { inputStream ->
            ZipInputStream(inputStream).use { zipIn ->
                var hasDataFile = false
                var entry = zipIn.nextEntry
                while (entry != null) {
                    if (entry.name == "backup_data.json") {
                        hasDataFile = true
                        break
                    }
                    entry = zipIn.nextEntry
                }
                hasDataFile
            }
        } ?: false
    } catch (e: Exception) {
        Timber.e(e, "Failed to validate backup file")
        false
    }

    private data class BackupContent(val exportData: ExportData, val images: Map<String, ByteArray>)

    private suspend fun performImportWithProgress(
        backupFileUri: Uri,
        progressCallback: suspend (ImportProgress) -> Unit
    ): ImportResult {
        val warnings = mutableListOf<String>()

        try {
            progressCallback(
                ImportProgress(
                    phase = ImportPhase.VALIDATING_BACKUP,
                    itemsProcessed = 0,
                    totalItems = 1,
                    currentItem = "Reading backup file"
                )
            )

            val backupContent = extractBackupContent(backupFileUri)

            progressCallback(
                ImportProgress(
                    phase = ImportPhase.VALIDATING_BACKUP,
                    itemsProcessed = 0,
                    totalItems = 1,
                    currentItem = "Validating backup version"
                )
            )

            val versionCheckResult =
                validateBackupVersion(backupContent.exportData.metadata.version)
            if (!versionCheckResult.isValid) {
                return ImportResult.Failure(
                    ImportError.UNSUPPORTED_VERSION,
                    versionCheckResult.errorMessage
                )
            }

            val totalItems = backupContent.exportData.products.size + 1
            var processedItems = 0

            val importedSettings = importSettingsFromExportData(
                backupContent.exportData,
                progressCallback,
                warnings,
                totalItems,
                processedItems
            )
            processedItems++

            val productImportResult = importProductsBasedOnVersion(
                backupContent,
                progressCallback,
                totalItems,
                processedItems
            )

            return createImportResult(
                importedProducts = productImportResult.importedProducts,
                importedImages = productImportResult.importedImages,
                importedSettings = importedSettings,
                failedImages = productImportResult.failedImages,
                warnings = warnings + productImportResult.warnings
            )
        } catch (e: Exception) {
            Timber.e(e, "Import failed")
            return ImportResult.Failure(
                ImportError.UNKNOWN_ERROR,
                e.message ?: "Unknown error during import"
            )
        }
    }

    private suspend fun extractBackupContent(backupFileUri: Uri): BackupContent =
        context.contentResolver.openInputStream(backupFileUri)?.use { inputStream ->
            ZipInputStream(inputStream).use { zipIn ->
                var exportData: ExportData? = null
                val images = mutableMapOf<String, ByteArray>()

                var entry = zipIn.nextEntry
                while (entry != null) {
                    when {
                        entry.name == "backup_data.json" -> {
                            val jsonData = zipIn.readBytes().toString(Charsets.UTF_8)
                            exportData = json.decodeFromString<ExportData>(jsonData)
                        }

                        entry.name.startsWith("images/") && !entry.isDirectory -> {
                            images[entry.name] = zipIn.readBytes()
                        }
                    }
                    entry = zipIn.nextEntry
                }

                BackupContent(
                    exportData
                        ?: throw IllegalArgumentException(
                            "No backup_data.json found in backup file"
                        ),
                    images
                )
            }
        } ?: throw IllegalArgumentException("Could not open backup file")

    private suspend fun importSettingsFromExportData(
        exportData: ExportData,
        progressCallback: suspend (ImportProgress) -> Unit,
        warnings: MutableList<String>,
        totalItems: Int,
        processedItems: Int
    ): Boolean {
        progressCallback(
            ImportProgress(
                phase = ImportPhase.IMPORTING_SETTINGS,
                itemsProcessed = processedItems,
                totalItems = totalItems,
                currentItem = "Importing settings"
            )
        )

        return try {
            exportData.settings.let { settings ->
                localDataStorage.changeTypeTo(settings.defaultType)
                localDataStorage.setDarkThemeConfig(settings.darkThemeConfig)
                localDataStorage.setAnalyticsCollectionEnabled(settings.analyticsEnabled)
                localDataStorage.setCrashesCollectionEnabled(settings.crashesEnabled)
            }
            progressCallback(
                ImportProgress(
                    phase = ImportPhase.IMPORTING_SETTINGS,
                    itemsProcessed = processedItems + 1,
                    totalItems = totalItems,
                    currentItem = "Settings imported"
                )
            )
            true
        } catch (e: Exception) {
            Timber.w(e, "Failed to import settings")
            warnings.add("Failed to import settings: ${e.message}")
            false
        }
    }

    private data class ProductImportResult(
        val importedProducts: Int,
        val importedImages: Int,
        val failedImages: List<String>,
        val warnings: List<String>
    )

    private data class VersionCheckResult(val isValid: Boolean, val errorMessage: String = "")

    private fun validateBackupVersion(backupVersion: String): VersionCheckResult = when {
        !BackupVersion.isVersionSupported(backupVersion) -> {
            VersionCheckResult(
                isValid = false,
                errorMessage = "Backup version $backupVersion is not supported. " +
                    "Supported versions: ${BackupVersion.SUPPORTED_VERSIONS.joinToString(", ")}"
            )
        }

        !BackupVersion.isVersionCompatible(backupVersion) -> {
            VersionCheckResult(
                isValid = false,
                errorMessage = "Backup version $backupVersion is too old and not compatible. " +
                    "Minimum supported version: ${BackupVersion.MIN_SUPPORTED_VERSION}"
            )
        }

        else -> VersionCheckResult(isValid = true)
    }

    private suspend fun importProductsBasedOnVersion(
        backupContent: BackupContent,
        progressCallback: suspend (ImportProgress) -> Unit,
        totalItems: Int,
        processedItems: Int
    ): ProductImportResult {
        val version = backupContent.exportData.metadata.version

        return when {
            version.startsWith("1.0") -> importProductsFromBackupV1(
                backupContent,
                progressCallback,
                totalItems,
                processedItems
            )

            else -> error("Unsupported version: $version")
        }
    }

    private suspend fun importProductsFromBackupV1(
        backupContent: BackupContent,
        progressCallback: suspend (ImportProgress) -> Unit,
        totalItems: Int,
        processedItems: Int
    ): ProductImportResult {
        var importedProducts = 0
        var importedImages = 0
        val failedImages = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        progressCallback(
            ImportProgress(
                phase = ImportPhase.IMPORTING_PRODUCTS,
                itemsProcessed = processedItems,
                totalItems = totalItems,
                currentItem = "Starting product import"
            )
        )

        backupContent.exportData.products.forEachIndexed { _, productExport ->
            val productResult = importSingleProduct(
                productExport,
                backupContent.images,
                progressCallback,
                processedItems + importedProducts,
                totalItems
            )
            if (productResult.success) {
                importedProducts++
                importedImages += productResult.imageCount
            } else {
                warnings.add("Failed to import product: ${productExport.name}")
            }
            failedImages.addAll(productResult.failedImages)
        }

        return ProductImportResult(importedProducts, importedImages, failedImages, warnings)
    }

    private data class SingleProductImportResult(
        val success: Boolean,
        val imageCount: Int,
        val failedImages: List<String>
    )

    private suspend fun importSingleProduct(
        productExport: ProductExport,
        imageMap: Map<String, ByteArray>,
        progressCallback: suspend (ImportProgress) -> Unit,
        currentItemIndex: Int,
        totalItems: Int
    ): SingleProductImportResult = try {
        progressCallback(
            ImportProgress(
                phase = ImportPhase.IMPORTING_PRODUCTS,
                itemsProcessed = currentItemIndex,
                totalItems = totalItems,
                currentItem = "Importing ${productExport.name}"
            )
        )

        val productFolder = folderPathManager.getMainFolder(productExport.productFolderName)
        if (!productFolder.exists()) {
            productFolder.mkdirs()
        }

        val (productImages, importedImageCount, failedImages) = importProductImages(
            productExport,
            imageMap,
            productFolder
        )

        val createProduct = CreateProduct(
            id = productExport.id,
            name = productExport.name,
            description = productExport.description,
            shop = productExport.shop,
            type = productExport.type,
            createdDate = OffsetDateTime.parse(productExport.createdDate),
            productFolderName = productExport.productFolderName,
            images = productImages
        )

        productsRepository.importProduct(createProduct)
        SingleProductImportResult(true, importedImageCount, failedImages)
    } catch (e: Exception) {
        Timber.w(e, "Failed to import product ${productExport.name}")
        SingleProductImportResult(false, 0, emptyList())
    }

    private data class ImageImportResult(
        val productImages: List<ProductImage>,
        val importedCount: Int,
        val failedImages: List<String>
    )

    private fun importProductImages(
        productExport: ProductExport,
        imageMap: Map<String, ByteArray>,
        productFolder: File
    ): ImageImportResult {
        val productImages = mutableListOf<ProductImage>()
        val failedImages = mutableListOf<String>()
        var importedCount = 0

        productExport.images.forEach { imageExport ->
            val imagePath = "images/${imageExport.exportFileName}"
            val imageData = imageMap[imagePath]

            if (imageData != null) {
                try {
                    val imageFile = File(productFolder, imageExport.originalName)
                    imageFile.writeBytes(imageData)

                    productImages.add(
                        ProductImage(
                            imageId = imageExport.imageId,
                            name = imageExport.originalName,
                            mimeType = imageExport.mimeType,
                            uriPath = imageFile.absolutePath,
                            uriString = imageFile.toURI().toString(),
                            size = imageData.size.toLong(),
                            md5 = imageExport.md5
                        )
                    )
                    importedCount++
                } catch (e: Exception) {
                    Timber.w(e, "Failed to import image ${imageExport.originalName}")
                    failedImages.add(imageExport.originalName)
                }
            } else {
                failedImages.add(imageExport.originalName)
            }
        }

        return ImageImportResult(productImages, importedCount, failedImages)
    }

    private fun createImportResult(
        importedProducts: Int,
        importedImages: Int,
        importedSettings: Boolean,
        failedImages: List<String>,
        warnings: List<String>
    ): ImportResult = if (warnings.isEmpty() && failedImages.isEmpty()) {
        ImportResult.Success(
            importedProducts = importedProducts,
            importedImages = importedImages,
            importedSettings = importedSettings
        )
    } else {
        ImportResult.PartialSuccess(
            importedProducts = importedProducts,
            importedImages = importedImages,
            importedSettings = importedSettings,
            skippedProducts = emptyList(),
            failedImages = failedImages,
            warnings = warnings
        )
    }
}
