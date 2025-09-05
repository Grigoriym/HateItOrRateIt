@file:Suppress("NestedBlockDepth")

package com.grappim.hateitorrateit.data.backupimpl

import android.content.Context
import android.net.Uri
import android.os.Build
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.core.async.IoDispatcher
import com.grappim.hateitorrateit.data.backupapi.BackupRepository
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
import com.grappim.hateitorrateit.data.backupimpl.utils.Constants.BACKUP_DATA_JSON
import com.grappim.hateitorrateit.data.backupimpl.utils.Constants.IMAGES_ZIP_FOLDER
import com.grappim.hateitorrateit.data.backupimpl.utils.ImportVersionChecker
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.data.repoapi.models.Product
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
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

    override suspend fun createBackupWithProgress(backupFileUri: Uri): Flow<BackupState> =
        channelFlow {
            try {
                send(
                    BackupState.Progress(
                        BackupProgress(phase = INITIALIZING)
                    )
                )

                send(
                    BackupState.Progress(
                        BackupProgress(phase = COLLECTING_DATABASE_DATA)
                    )
                )
                val products = productsRepository.getAllProducts()

                send(
                    BackupState.Progress(
                        BackupProgress(phase = COLLECTING_DATABASE_DATA)
                    )
                )
                val exportData = collectExportData(products)

                send(
                    BackupState.Progress(
                        BackupProgress(phase = CREATING_BACKUP_FILE)
                    )
                )

                writeBackupData(exportData, products, backupFileUri) { progress ->
                    send(BackupState.Progress(progress))
                }

                send(
                    BackupState.Progress(
                        BackupProgress(phase = COMPLETED)
                    )
                )
                send(BackupState.Completed(BackupResult.Success))
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

    private suspend fun writeBackupData(
        exportData: ExportData,
        products: ImmutableList<Product>,
        backupFileUri: Uri,
        progressCallback: suspend (BackupProgress) -> Unit
    ) {
        val outputStream = context.contentResolver.openOutputStream(backupFileUri)
            ?: error("Failed to open output stream for user-selected location")

        outputStream.use { stream ->
            ZipOutputStream(stream).use { zipOut ->
                addDataToZip(zipOut, exportData)
                progressCallback(BackupProgress(phase = CREATING_BACKUP_FILE))
                addImagesToZip(zipOut, products)
            }
        }
    }

    private suspend fun collectExportData(products: ImmutableList<Product>): ExportData {
        val settings = SettingsExport(
            defaultType = localDataStorage.typeFlow.first(),
            darkThemeConfig = localDataStorage.darkThemeConfig.first(),
            analyticsEnabled = localDataStorage.analyticsCollectionEnabled.first(),
            crashesEnabled = localDataStorage.crashesCollectionEnabled.first()
        )

        val metadata = ExportMetadata(
            version = ImportVersionChecker.CURRENT_VERSION,
            appVersionName = appInfoProvider.getAppInfo(),
            exportTimestamp = dateTimeUtils.getInstantNow().toEpochMilli(),
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
        val entry = ZipEntry(BACKUP_DATA_JSON)
        zipOut.putNextEntry(entry)
        zipOut.write(jsonData.toByteArray(Charsets.UTF_8))
        zipOut.closeEntry()
    }

    private suspend fun addImagesToZip(zipOut: ZipOutputStream, products: ImmutableList<Product>) {
        products.forEach { product ->
            if (!currentCoroutineContext().isActive) {
                return@forEach
            }
            val productFolder = folderPathManager.getMainFolder(product.productFolderName)

            product.images.forEach { image ->
                val imageFile = File(productFolder, image.name)
                if (imageFile.exists()) {
                    try {
                        val entry =
                            ZipEntry(
                                "$IMAGES_ZIP_FOLDER/${product.productFolderName}/${image.name}"
                            )
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
