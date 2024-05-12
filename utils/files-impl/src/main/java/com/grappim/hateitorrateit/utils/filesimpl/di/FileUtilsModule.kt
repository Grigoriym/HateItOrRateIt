package com.grappim.hateitorrateit.utils.filesimpl.di

import com.grappim.hateitorrateit.utils.filesapi.creation.FileCreationUtils
import com.grappim.hateitorrateit.utils.filesapi.deletion.FileDeletionUtils
import com.grappim.hateitorrateit.utils.filesapi.images.ImagePersistenceManager
import com.grappim.hateitorrateit.utils.filesapi.inforetriever.FileInfoRetriever
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import com.grappim.hateitorrateit.utils.filesapi.transfer.FileTransferOperations
import com.grappim.hateitorrateit.utils.filesapi.urimanager.FileUriManager
import com.grappim.hateitorrateit.utils.filesimpl.file.creation.FileCreationUtilsImpl
import com.grappim.hateitorrateit.utils.filesimpl.file.deletion.FileDeletionUtilsImpl
import com.grappim.hateitorrateit.utils.filesimpl.file.images.ImagePersistenceManagerImpl
import com.grappim.hateitorrateit.utils.filesimpl.file.inforetriever.FileInfoRetrieverImpl
import com.grappim.hateitorrateit.utils.filesimpl.file.pathmanager.FolderPathManagerImpl
import com.grappim.hateitorrateit.utils.filesimpl.file.transfer.FileTransferOperationsImpl
import com.grappim.hateitorrateit.utils.filesimpl.file.urimanager.FileUriManagerImpl
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
