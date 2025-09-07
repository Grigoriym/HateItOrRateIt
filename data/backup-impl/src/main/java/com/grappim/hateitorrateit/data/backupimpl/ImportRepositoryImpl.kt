@file:Suppress("NestedBlockDepth")

package com.grappim.hateitorrateit.data.backupimpl

import android.content.Context
import android.net.Uri
import com.grappim.hateitorrateit.core.async.IoDispatcher
import com.grappim.hateitorrateit.data.backupapi.ImportRepository
import com.grappim.hateitorrateit.data.backupapi.models.ExportData
import com.grappim.hateitorrateit.data.backupapi.models.ImportError
import com.grappim.hateitorrateit.data.backupapi.models.ImportMode
import com.grappim.hateitorrateit.data.backupapi.models.ImportPhase
import com.grappim.hateitorrateit.data.backupapi.models.ImportProgress
import com.grappim.hateitorrateit.data.backupapi.models.ImportResult
import com.grappim.hateitorrateit.data.backupapi.models.ImportState
import com.grappim.hateitorrateit.data.backupapi.models.ProductExport
import com.grappim.hateitorrateit.data.backupimpl.models.BackupContent
import com.grappim.hateitorrateit.data.backupimpl.models.ImageImportResult
import com.grappim.hateitorrateit.data.backupimpl.models.ProductImportResult
import com.grappim.hateitorrateit.data.backupimpl.models.SingleProductImportResult
import com.grappim.hateitorrateit.data.backupimpl.models.VersionCheckResult
import com.grappim.hateitorrateit.data.backupimpl.utils.Constants.BACKUP_DATA_JSON
import com.grappim.hateitorrateit.data.backupimpl.utils.Constants.IMAGES_ZIP_FOLDER
import com.grappim.hateitorrateit.data.backupimpl.utils.ImportVersionChecker
import com.grappim.hateitorrateit.data.backupimpl.utils.ProductConflictDetector
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.CreateProduct
import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.data.repoapi.models.ProductImage
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.filesapi.urimanager.FileUriManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
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
    private val fileUriManager: FileUriManager,
    private val versionChecker: ImportVersionChecker,
    private val conflictDetector: ProductConflictDetector,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ImportRepository {

    override suspend fun importBackupWithProgress(
        backupFileUri: Uri,
        importMode: ImportMode
    ): Flow<ImportState> = channelFlow {
        try {
            val result = performImportWithProgress(backupFileUri, importMode) { progress ->
                send(ImportState.Progress(progress))
            }

            send(
                ImportState.Progress(
                    ImportProgress(phase = ImportPhase.COMPLETED)
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

    private suspend fun performImportWithProgress(
        backupFileUri: Uri,
        importMode: ImportMode,
        progressCallback: suspend (ImportProgress) -> Unit
    ): ImportResult {
        val warnings = mutableListOf<String>()

        try {
            progressCallback(
                ImportProgress(phase = ImportPhase.EXTRACTING_DATA)
            )

            val backupContent = extractBackupContent(backupFileUri)

            progressCallback(
                ImportProgress(phase = ImportPhase.VALIDATING_BACKUP)
            )

            val versionCheckResult = validateBackupVersion(
                backupVersion = backupContent.exportData.metadata.version
            )
            if (!versionCheckResult.isValid) {
                return ImportResult.Failure(
                    error = ImportError.UNSUPPORTED_VERSION,
                    message = versionCheckResult.errorMessage
                )
            }

            progressCallback(ImportProgress(phase = ImportPhase.IMPORTING_SETTINGS))

            val importedSettings = importSettingsFromExportData(
                exportData = backupContent.exportData,
                warnings = warnings
            )

            val productImportResult = importProductsBasedOnVersion(
                backupContent,
                importMode,
                progressCallback
            )

            return createImportResult(
                importedProducts = productImportResult.importedProducts,
                importedImages = productImportResult.importedImages,
                importedSettings = importedSettings,
                failedImages = productImportResult.failedImages,
                warnings = warnings + productImportResult.warnings,
                skippedProducts = productImportResult.skippedProducts
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
                while (entry != null && currentCoroutineContext().isActive) {
                    when {
                        entry.name == BACKUP_DATA_JSON -> {
                            val jsonData = zipIn.readBytes().toString(Charsets.UTF_8)
                            exportData = json.decodeFromString<ExportData>(jsonData)
                        }

                        entry.name.startsWith("$IMAGES_ZIP_FOLDER/") && !entry.isDirectory -> {
                            images[entry.name] = zipIn.readBytes()
                        }
                    }
                    entry = zipIn.nextEntry
                }

                if (exportData == null) {
                    error("No backup_data.json found in backup file")
                }

                BackupContent(exportData, images)
            }
        } ?: error("Could not open backup file")

    private suspend fun importSettingsFromExportData(
        exportData: ExportData,
        warnings: MutableList<String>
    ): Boolean = try {
        exportData.settings.let { settings ->
            localDataStorage.changeTypeTo(settings.defaultType)
            localDataStorage.setDarkThemeConfig(settings.darkThemeConfig)
            localDataStorage.setAnalyticsCollectionEnabled(settings.analyticsEnabled)
            localDataStorage.setCrashesCollectionEnabled(settings.crashesEnabled)
        }
        true
    } catch (e: Exception) {
        Timber.w(e, "Failed to import settings")
        warnings.add("Failed to import settings: ${e.message}")
        false
    }

    private fun validateBackupVersion(backupVersion: Int): VersionCheckResult =
        versionChecker.validateBackupVersion(backupVersion)

    private suspend fun importProductsBasedOnVersion(
        backupContent: BackupContent,
        importMode: ImportMode,
        progressCallback: suspend (ImportProgress) -> Unit
    ): ProductImportResult {
        val version = backupContent.exportData.metadata.version

        return when (version) {
            1 -> importProductsFromBackupV1(
                backupContent,
                importMode,
                progressCallback
            )

            else -> error("Unsupported version: $version")
        }
    }

    private suspend fun importProductsFromBackupV1(
        backupContent: BackupContent,
        importMode: ImportMode,
        progressCallback: suspend (ImportProgress) -> Unit
    ): ProductImportResult {
        progressCallback(ImportProgress(phase = ImportPhase.DETECTING_CONFLICTS))

        var importedProducts = 0
        var importedImages = 0
        val failedImages = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val skippedProducts = mutableListOf<String>()

        // Detect conflicts based on import mode
        val conflictResults = when (importMode) {
            ImportMode.CREATE_NEW -> {
                emptyMap()
            }

            ImportMode.REPLACE_EXISTING, ImportMode.SKIP_CONFLICTS -> {
                conflictDetector.detectConflicts(backupContent.exportData.products)
            }
        }

        progressCallback(ImportProgress(phase = ImportPhase.IMPORTING_PRODUCTS))

        backupContent.exportData.products.forEach { productExport ->
            if (!currentCoroutineContext().isActive) {
                return@forEach
            }

            val existingProduct = conflictResults[productExport]
            val shouldImport = when (importMode) {
                ImportMode.CREATE_NEW, ImportMode.REPLACE_EXISTING -> true
                ImportMode.SKIP_CONFLICTS -> {
                    if (existingProduct != null) {
                        skippedProducts.add(productExport.name)
                        false
                    } else {
                        true
                    }
                }
            }

            if (shouldImport) {
                val productResult = importSingleProduct(
                    productExport = productExport,
                    imageMap = backupContent.images,
                    existingProduct = existingProduct,
                    importMode = importMode
                )
                if (productResult.success) {
                    importedProducts++
                    importedImages += productResult.imageCount
                } else {
                    warnings.add("Failed to import product: ${productExport.name}")
                }
                failedImages.addAll(productResult.failedImages)
            }
        }

        return ProductImportResult(
            importedProducts,
            importedImages,
            failedImages,
            warnings,
            skippedProducts
        )
    }

    private suspend fun importSingleProduct(
        productExport: ProductExport,
        imageMap: Map<String, ByteArray>,
        existingProduct: Product? = null,
        importMode: ImportMode
    ): SingleProductImportResult = try {
        // Use existing folder for REPLACE_EXISTING with conflicts, otherwise original folder
        val folderName = if (importMode == ImportMode.REPLACE_EXISTING && existingProduct != null) {
            existingProduct.productFolderName
        } else {
            productExport.productFolderName
        }

        val productFolder = folderPathManager.getMainFolder(folderName)

        val (productImages, importedImageCount, failedImages) = importProductImages(
            productExport = productExport,
            imageMap = imageMap,
            productFolder = productFolder
        )

        val createProduct = when (importMode) {
            ImportMode.CREATE_NEW, ImportMode.SKIP_CONFLICTS -> {
                // Both modes create new products when they reach this point
                CreateProduct.newProduct(
                    name = productExport.name,
                    description = productExport.description,
                    shop = productExport.shop,
                    type = productExport.type,
                    createdDate = OffsetDateTime.parse(productExport.createdDate),
                    productFolderName = folderName,
                    images = productImages
                )
            }

            ImportMode.REPLACE_EXISTING -> {
                if (existingProduct != null) {
                    // Replace existing product by reusing its ID
                    CreateProduct(
                        id = existingProduct.id,
                        name = productExport.name,
                        description = productExport.description,
                        shop = productExport.shop,
                        type = productExport.type,
                        createdDate = OffsetDateTime.parse(productExport.createdDate),
                        productFolderName = folderName,
                        images = productImages
                    )
                } else {
                    // No conflict found, create as new product
                    CreateProduct.newProduct(
                        name = productExport.name,
                        description = productExport.description,
                        shop = productExport.shop,
                        type = productExport.type,
                        createdDate = OffsetDateTime.parse(productExport.createdDate),
                        productFolderName = folderName,
                        images = productImages
                    )
                }
            }
        }

        productsRepository.importProduct(createProduct)
        SingleProductImportResult(true, importedImageCount, failedImages)
    } catch (e: Exception) {
        Timber.w(e, "Failed to import product ${productExport.name}")
        SingleProductImportResult(false, 0, emptyList())
    }

    private fun importProductImages(
        productExport: ProductExport,
        imageMap: Map<String, ByteArray>,
        productFolder: File
    ): ImageImportResult {
        val productImages = mutableListOf<ProductImage>()
        val failedImages = mutableListOf<String>()
        var importedCount = 0

        productExport.images.forEach { imageExport ->
            val imagePath = "$IMAGES_ZIP_FOLDER/${imageExport.exportFileName}"
            val imageData = imageMap[imagePath]

            if (imageData != null) {
                try {
                    val imageFile = File(productFolder, imageExport.originalName)
                    imageFile.writeBytes(imageData)

                    val contentUri = fileUriManager.getFileUri(imageFile)

                    productImages.add(
                        ProductImage(
                            imageId = imageExport.imageId,
                            name = imageExport.originalName,
                            mimeType = imageExport.mimeType,
                            uriPath = contentUri.path ?: "",
                            uriString = contentUri.toString(),
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
        warnings: List<String>,
        skippedProducts: List<String>
    ): ImportResult {
        // In SKIP_CONFLICTS mode, skipped products are expected behavior, not failures
        val hasRealWarnings = warnings.isNotEmpty()
        val hasFailures = failedImages.isNotEmpty()

        return when {
            // Complete success: no warnings, no failures
            !hasRealWarnings && !hasFailures -> {
                ImportResult.Success(
                    importedProducts = importedProducts,
                    importedImages = importedImages,
                    importedSettings = importedSettings
                )
            }

            // Partial success: has warnings or failures, but some imports succeeded
            else -> {
                ImportResult.PartialSuccess(
                    importedProducts = importedProducts,
                    importedImages = importedImages,
                    importedSettings = importedSettings,
                    skippedProducts = skippedProducts,
                    failedImages = failedImages,
                    warnings = warnings
                )
            }
        }
    }
}
