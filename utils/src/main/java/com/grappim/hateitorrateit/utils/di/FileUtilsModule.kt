package com.grappim.hateitorrateit.utils.di

import com.grappim.hateitorrateit.utils.file.creation.FileCreationUtils
import com.grappim.hateitorrateit.utils.file.creation.FileCreationUtilsImpl
import com.grappim.hateitorrateit.utils.file.deletion.FileDeletionUtils
import com.grappim.hateitorrateit.utils.file.deletion.FileDeletionUtilsImpl
import com.grappim.hateitorrateit.utils.file.images.ImagePersistenceManager
import com.grappim.hateitorrateit.utils.file.images.ImagePersistenceManagerImpl
import com.grappim.hateitorrateit.utils.file.inforetriever.FileInfoRetriever
import com.grappim.hateitorrateit.utils.file.inforetriever.FileInfoRetrieverImpl
import com.grappim.hateitorrateit.utils.file.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.file.pathmanager.FolderPathManagerImpl
import com.grappim.hateitorrateit.utils.file.transfer.FileTransferOperations
import com.grappim.hateitorrateit.utils.file.transfer.FileTransferOperationsImpl
import com.grappim.hateitorrateit.utils.file.urimanager.FileUriManager
import com.grappim.hateitorrateit.utils.file.urimanager.FileUriManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@[Module InstallIn(SingletonComponent::class)]
interface FileUtilsModule {

    @Binds
    fun bindFileDeletionUtils(fileDeletionUtilsImpl: FileDeletionUtilsImpl): FileDeletionUtils

    @Binds
    fun bindFileTransferOperations(
        fileTransferOperationsImpl: FileTransferOperationsImpl
    ): FileTransferOperations

    @Binds
    fun bindFolderPathManager(folderPathManagerImpl: FolderPathManagerImpl): FolderPathManager

    @Binds
    fun bindFileInfoRetriever(fileInfoRetrieverImpl: FileInfoRetrieverImpl): FileInfoRetriever

    @Binds
    fun bindFileCreationUtils(fileCreationUtilsImpl: FileCreationUtilsImpl): FileCreationUtils

    @Binds
    fun bindFileUriManager(fileUriManagerImpl: FileUriManagerImpl): FileUriManager

    @Binds
    fun bindImagePersistenceManager(
        imagePersistenceManagerImpl: ImagePersistenceManagerImpl
    ): ImagePersistenceManager
}
