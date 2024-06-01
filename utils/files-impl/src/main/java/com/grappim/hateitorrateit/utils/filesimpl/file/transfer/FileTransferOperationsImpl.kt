package com.grappim.hateitorrateit.utils.filesimpl.file.transfer

import com.grappim.hateitorrateit.core.async.IoDispatcher
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.filesapi.transfer.FileTransferOperations
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles the transfer of files between locations, such as moving or copying files and directories.
 */
@Singleton
class FileTransferOperationsImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val folderPathManager: FolderPathManager
) : FileTransferOperations {

    override suspend fun moveSourceFilesToDestinationFolder(
        sourceFolderName: String,
        destinationFolderName: String
    ) {
        Timber.d(
            "moveSourceFilesToDestinationFolder " +
                "source: $sourceFolderName, " +
                "destination: $destinationFolderName"
        )
        processFilesInFolder(sourceFolderName, destinationFolderName) { file, destPath ->
            moveFile(file.path, destPath)
            file.deleteRecursively()
        }
        withContext(ioDispatcher) {
            val mainFolder = folderPathManager.getMainFolder(sourceFolderName)
            mainFolder.deleteRecursively()
        }
    }

    override suspend fun copySourceFilesToDestination(
        sourceFolderName: String,
        destinationFolderName: String
    ) {
        Timber.d(
            "copySourceFilesToDestination " +
                "sourceFolderName: $sourceFolderName, " +
                "destinationFolderName: $destinationFolderName"
        )
        processFilesInFolder(
            sourceFolderName = sourceFolderName,
            destinationFolderName = destinationFolderName
        ) { file, destPath ->
            copyFile(file.path, destPath)
        }
    }

    @Throws(NoSuchFileException::class, FileAlreadyExistsException::class, IOException::class)
    override suspend fun writeSourceFileToTargetFile(sourceFile: File, newFile: File): Unit =
        withContext(ioDispatcher) {
            val result = sourceFile.copyTo(target = newFile, overwrite = true)
            Timber.d("writeSourceFileToTargetFile: $result")
        }

    private suspend fun moveFile(sourceFilePath: String, destinationFilePath: String) {
        copyFile(sourceFilePath, destinationFilePath)
        File(sourceFilePath).delete()
    }

    private suspend fun copyFile(sourceFilePath: String, destinationFilePath: String) =
        withContext(ioDispatcher) {
            val sourceFile = File(sourceFilePath)
            val destinationFile = File(destinationFilePath)
            sourceFile.inputStream().use { inputStream ->
                destinationFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

    /**
     * Processes each file within a specified source folder and performs a defined action on each file.
     *
     * This function iterates over all files in the source folder, applies a custom action defined by the caller,
     * and is designed to work with various file operations such as copying, moving, or custom processing.
     * It ensures that only files (not directories) are processed, and handles each file according to the
     * action specified in the `fileAction` lambda parameter.
     *
     * @param sourceFolderName The name of the source folder where files are located. This should be a relative
     * or absolute path to the folder containing files to process.
     * @param destinationFolderName The name of the destination folder where files will be processed or moved to.
     * This is used within the `fileAction` to determine the final location or processing of the file.
     * @param fileAction A high-order function or lambda that defines the action to be performed on each file.
     * This function takes two parameters: a [File] object representing the current file being processed,
     * and a [String] representing the destination path or identifier for the processed file. The action
     * can encompass any operation such as moving, copying, or other custom file manipulations.
     *
     * Usage example:
     * ```
     * processFilesInFolder("sourceFolderPath", "destinationFolderPath") { file, destinationPath ->
     *     // Custom file processing logic here
     * }
     * ```
     */
    private suspend fun processFilesInFolder(
        sourceFolderName: String,
        destinationFolderName: String,
        fileAction: suspend (File, String) -> Unit
    ) = withContext(ioDispatcher) {
        val sourceFolder = folderPathManager.getMainFolder(sourceFolderName)
        val destinationFolder = folderPathManager.getMainFolder(destinationFolderName)

        val files = sourceFolder.listFiles()
        files?.forEach { file ->
            if (file.isFile) {
                val destinationFilePath = "${destinationFolder.path}/${file.name}"
                try {
                    fileAction(file, destinationFilePath)
                } catch (e: IOException) {
                    Timber.e(e)
                }
            }
        }
    }
}
