package com.grappim.hateitorrateit.utils.productmanager

import com.grappim.hateitorrateit.utils.FileUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductImageManagerImpl @Inject constructor(
    private val fileUtils: FileUtils
) : ProductImageManager {

    /**
     * Copy the initial state of images so that we can revert them in case they are deleted from
     * the original folder
     */
    override suspend fun copyToBackupFolder(
        productFolderName: String
    ) {
        fileUtils.copySourceFilesToDestination(
            sourceFolderName = productFolderName,
            destinationFolderName = fileUtils.getBackupFolderName(productFolderName)
        )
    }

    override suspend fun moveFromTempToOriginalFolder(
        productFolderName: String
    ) {
        fileUtils.moveSourceFilesToDestinationFolder(
            sourceFolderName = fileUtils.getTempFolderName(productFolderName),
            destinationFolderName = productFolderName,
        )
    }

    override suspend fun moveFromBackupToOriginalFolder(
        productFolderName: String
    ) {
        fileUtils.moveSourceFilesToDestinationFolder(
            sourceFolderName = fileUtils.getBackupFolderName(productFolderName),
            destinationFolderName = productFolderName,
        )
    }
}
