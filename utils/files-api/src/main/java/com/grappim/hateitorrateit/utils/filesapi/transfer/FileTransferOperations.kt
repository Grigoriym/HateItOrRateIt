package com.grappim.hateitorrateit.utils.filesapi.transfer

interface FileTransferOperations {
    suspend fun moveSourceFilesToDestinationFolder(
        sourceFolderName: String,
        destinationFolderName: String
    )

    suspend fun copySourceFilesToDestination(
        sourceFolderName: String,
        destinationFolderName: String
    )
}
