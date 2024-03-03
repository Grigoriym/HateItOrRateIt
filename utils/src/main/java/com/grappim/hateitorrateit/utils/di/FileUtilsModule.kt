package com.grappim.hateitorrateit.utils.di

import com.grappim.hateitorrateit.utils.file.FileCreationUtils
import com.grappim.hateitorrateit.utils.file.FileCreationUtilsImpl
import com.grappim.hateitorrateit.utils.file.FileDeletionUtils
import com.grappim.hateitorrateit.utils.file.FileDeletionUtilsImpl
import com.grappim.hateitorrateit.utils.file.FileInfoRetriever
import com.grappim.hateitorrateit.utils.file.FileInfoRetrieverImpl
import com.grappim.hateitorrateit.utils.file.FileTransferOperations
import com.grappim.hateitorrateit.utils.file.FileTransferOperationsImpl
import com.grappim.hateitorrateit.utils.file.FileUriManager
import com.grappim.hateitorrateit.utils.file.FileUriManagerImpl
import com.grappim.hateitorrateit.utils.file.FolderPathManager
import com.grappim.hateitorrateit.utils.file.FolderPathManagerImpl
import com.grappim.hateitorrateit.utils.file.ImagePersistenceManager
import com.grappim.hateitorrateit.utils.file.ImagePersistenceManagerImpl
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
