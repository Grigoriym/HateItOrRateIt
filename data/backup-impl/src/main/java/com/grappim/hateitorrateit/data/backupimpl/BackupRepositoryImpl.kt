@file:Suppress("NestedBlockDepth")

package com.grappim.hateitorrateit.data.backupimpl

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.core.async.IoDispatcher
import com.grappim.hateitorrateit.data.backupapi.BackupRepository
import com.grappim.hateitorrateit.data.backupapi.BackupVersion
import com.grappim.hateitorrateit.data.backupapi.models.BackupError
import com.grappim.hateitorrateit.data.backupapi.models.BackupPhase.COLLECTING_DATABASE_DATA
import com.grappim.hateitorrateit.data.backupapi.models.BackupPhase.COMPLETED
import com.grappim.hateitorrateit.data.backupapi.models.BackupPhase.CREATING_BACKUP_FILE
import com.grappim.hateitorrateit.data.backupapi.models.BackupPhase.INITIALIZING
import com.grappim.hateitorrateit.data.backupapi.models.BackupProgress
import com.grappim.hateitorrateit.data.backupapi.models.BackupResult
import com.grappim.hateitorrateit.data.backupapi.models.BackupState
import com.grappim.hateitorrateit.data.backupapi.models.ExportData
import com.grappim.hateitorrateit.data.backupapi.models.ExportMetadata
import com.grappim.hateitorrateit.data.backupapi.models.ProductExport
import com.grappim.hateitorrateit.data.backupapi.models.ProductImageExport
import com.grappim.hateitorrateit.data.backupapi.models.SettingsExport
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class BackupRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val productsRepository: ProductsRepository,
    private val localDataStorage: LocalDataStorage,
    private val appInfoProvider: AppInfoProvider,
    private val folderPathManager: FolderPathManager,
    private val json: Json,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val dateTimeUtils: DateTimeUtils
) : BackupRepository {

    override suspend fun createBackupWithProgress(): Flow<BackupState> = channelFlow {
        try {
            send(
                BackupState.Progress(
                    BackupProgress(
                        phase = INITIALIZING,
                        itemsProcessed = 0,
                        totalItems = 1,
                        currentItem = "Starting backup"
                    )
                )
            )

            if (!canCreateBackup()) {
                send(
                    BackupState.Completed(
                        BackupResult.Failure(
                            BackupError.STORAGE_PERMISSION_DENIED,
                            "Cannot access storage location for backup"
                        )
                    )
                )
                return@channelFlow
            }

            val (outputStream, backupInfo) = createBackupOutputStream()
            send(
                BackupState.Progress(
                    BackupProgress(
                        phase = INITIALIZING,
                        itemsProcessed = 1,
                        totalItems = 1,
                        currentItem = "Initializing"
                    )
                )
            )

            outputStream.use { stream ->
                send(
                    BackupState.Progress(
                        BackupProgress(
                            COLLECTING_DATABASE_DATA,
                            0,
                            1,
                            "Loading products"
                        )
                    )
                )
                val products = productsRepository.getProductsFlow("", null).first()

                send(
                    BackupState.Progress(
                        BackupProgress(
                            COLLECTING_DATABASE_DATA,
                            1,
                            1,
                            "Collecting settings"
                        )
                    )
                )
                val exportData = collectExportData(products.toList())

                send(
                    BackupState.Progress(
                        BackupProgress(
                            CREATING_BACKUP_FILE,
                            0,
                            2,
                            "Creating ZIP file"
                        )
                    )
                )

                ZipOutputStream(stream).use { zipOut ->
                    addDataToZip(zipOut, exportData)
                    send(
                        BackupState.Progress(
                            BackupProgress(
                                CREATING_BACKUP_FILE,
                                1,
                                2,
                                "Adding images"
                            )
                        )
                    )
                    addImagesToZip(zipOut, products.toList())
                    send(
                        BackupState.Progress(
                            BackupProgress(
                                CREATING_BACKUP_FILE,
                                2,
                                2,
                                "Finalizing"
                            )
                        )
                    )
                }
            }

            send(
                BackupState.Progress(
                    BackupProgress(
                        phase = COMPLETED,
                        itemsProcessed = 1,
                        totalItems = 1,
                        currentItem = "Backup completed"
                    )
                )
            )
            send(BackupState.Completed(BackupResult.Success(backupInfo.file)))
        } catch (e: CancellationException) {
            throw e
        } catch (e: TimeoutCancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Backup creation failed")
            send(
                BackupState.Completed(
                    BackupResult.Failure(
                        BackupError.UNKNOWN_ERROR,
                        e.message ?: "Unknown error"
                    )
                )
            )
        }
    }.flowOn(ioDispatcher)

    override suspend fun canCreateBackup(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true
        } else {
            try {
                val downloadsDir = folderPathManager.getBackupParentFolder()
                val backupChildFolder = folderPathManager.getBackupChildFolderName()
                val hiorDir = File(downloadsDir, backupChildFolder)
                (downloadsDir.exists() || downloadsDir.mkdirs()) &&
                    (hiorDir.exists() || hiorDir.mkdirs())
            } catch (e: Exception) {
                Timber.e(e, "Cannot access Downloads/hior directory")
                false
            }
        }

    override suspend fun estimateBackupSize(): Long = try {
        val products = productsRepository.getProductsFlow("", null).first()
        var totalSize = 1024L

        products.forEach { product ->
            product.images.forEach { image ->
                val imageFile =
                    File(folderPathManager.getMainFolder(product.productFolderName), image.name)
                if (imageFile.exists()) {
                    totalSize += imageFile.length()
                }
            }
        }

        totalSize
    } catch (e: Exception) {
        Timber.e(e, "Failed to estimate backup size")
        0L
    }

    private data class BackupInfo(val file: File, val uri: Uri?)

    private fun createBackupOutputStream(): Pair<OutputStream, BackupInfo> {
        val timestamp = dateTimeUtils.getBackupFolderNowTimestamp()
        val filename = "hateitorrateit_backup_$timestamp.zip"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createBackupOutputStreamApi29(filename)
        } else {
            createBackupOutputStreamLegacy(filename)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createBackupOutputStreamApi29(filename: String): Pair<OutputStream, BackupInfo> {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/zip")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/hior")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: error("Failed to create MediaStore entry")

        val outputStream = resolver.openOutputStream(uri)
            ?: error("Failed to open output stream")

        val file = File(
            File(
                folderPathManager.getBackupParentFolder(),
                folderPathManager.getBackupChildFolderName()
            ),
            filename
        )
        return outputStream to BackupInfo(file, uri)
    }

    private fun createBackupOutputStreamLegacy(filename: String): Pair<OutputStream, BackupInfo> {
        val downloadsDir = folderPathManager.getBackupParentFolder()
        val childFolder = folderPathManager.getBackupChildFolderName()
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }

        val hiorDir = File(downloadsDir, childFolder)
        if (!hiorDir.exists()) {
            hiorDir.mkdirs()
        }

        val file = File(hiorDir, filename)
        val outputStream = FileOutputStream(file)
        return outputStream to BackupInfo(file, null)
    }

    private suspend fun collectExportData(products: List<Product>): ExportData {
        val settings = SettingsExport(
            defaultType = localDataStorage.typeFlow.first(),
            darkThemeConfig = localDataStorage.darkThemeConfig.first(),
            analyticsEnabled = localDataStorage.analyticsCollectionEnabled.first(),
            crashesEnabled = localDataStorage.crashesCollectionEnabled.first()
        )

        val metadata = ExportMetadata(
            version = BackupVersion.CURRENT_VERSION,
            appVersionCode = appInfoProvider.getAppInfo().hashCode(),
            appVersionName = appInfoProvider.getAppInfo(),
            exportTimestamp = System.currentTimeMillis(),
            deviceInfo = Build.MODEL,
            totalProducts = products.size,
            totalImages = products.sumOf { it.images.size }
        )

        val productExports = products.map { product ->
            ProductExport(
                id = product.id,
                name = product.name,
                description = product.description,
                shop = product.shop,
                type = product.type,
                createdDate = product.createdDate.toString(),
                productFolderName = product.productFolderName,
                images = product.images.map { image ->
                    ProductImageExport(
                        imageId = image.imageId,
                        originalName = image.name,
                        exportFileName = "${product.productFolderName}/${image.name}",
                        mimeType = image.mimeType,
                        size = image.size,
                        md5 = image.md5
                    )
                }
            )
        }

        return ExportData(
            metadata = metadata,
            products = productExports,
            settings = settings
        )
    }

    private fun addDataToZip(zipOut: ZipOutputStream, exportData: ExportData) {
        val jsonData = json.encodeToString(exportData)
        val entry = ZipEntry("backup_data.json")
        zipOut.putNextEntry(entry)
        zipOut.write(jsonData.toByteArray())
        zipOut.closeEntry()
    }

    private fun addImagesToZip(zipOut: ZipOutputStream, products: List<Product>) {
        products.forEach { product ->
            val productFolder = folderPathManager.getMainFolder(product.productFolderName)

            product.images.forEach { image ->
                val imageFile = File(productFolder, image.name)
                if (imageFile.exists()) {
                    try {
                        val entry = ZipEntry("images/${product.productFolderName}/${image.name}")
                        zipOut.putNextEntry(entry)
                        imageFile.inputStream().use { input ->
                            input.copyTo(zipOut)
                        }
                        zipOut.closeEntry()
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to add image ${image.name} to backup")
                    }
                }
            }
        }
    }
}
