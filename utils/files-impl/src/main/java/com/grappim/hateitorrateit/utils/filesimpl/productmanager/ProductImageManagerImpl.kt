package com.grappim.hateitorrateit.utils.filesimpl.productmanager

import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.filesapi.productmanager.ProductImageManager
import com.grappim.hateitorrateit.utils.filesapi.transfer.FileTransferOperations
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductImageManagerImpl @Inject constructor(
    private val folderPathManager: FolderPathManager,
    private val fileTransferOperations: FileTransferOperations
) : ProductImageManager {

    /**
     * Copy the initial state of images so that we can revert them in case they are deleted from
     * the original folder
     */
    override suspend fun copyToBackupFolder(productFolderName: String) {
        fileTransferOperations.copySourceFilesToDestination(
            sourceFolderName = productFolderName,
            destinationFolderName = folderPathManager.getBackupFolderName(productFolderName)
        )
    }

    override suspend fun moveFromTempToOriginalFolder(productFolderName: String) {
        fileTransferOperations.moveSourceFilesToDestinationFolder(
            sourceFolderName = folderPathManager.getTempFolderName(productFolderName),
            destinationFolderName = productFolderName
        )
    }

    override suspend fun moveFromBackupToOriginalFolder(productFolderName: String) {
        fileTransferOperations.moveSourceFilesToDestinationFolder(
            sourceFolderName = folderPathManager.getBackupFolderName(productFolderName),
            destinationFolderName = productFolderName
        )
    }
}
