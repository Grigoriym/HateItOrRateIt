package com.grappim.hateitorrateit.utils.file

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
