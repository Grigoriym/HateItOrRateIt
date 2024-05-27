package com.grappim.hateitorrateit.utils.filesapi.transfer

import java.io.File

interface FileTransferOperations {
    suspend fun moveSourceFilesToDestinationFolder(
        sourceFolderName: String,
        destinationFolderName: String
    )

    suspend fun copySourceFilesToDestination(
        sourceFolderName: String,
        destinationFolderName: String
    )
    suspend fun writeSourceFileToTargetFile(sourceFile: File, newFile: File)
}
