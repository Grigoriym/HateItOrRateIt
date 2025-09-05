package com.grappim.hateitorrateit.data.backupimpl

import android.content.Context
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BackupRepositoryImplTest {

    private val context: Context = mockk(relaxed = true)
    private val productsRepository: ProductsRepository = mockk()
    private val localDataStorage: LocalDataStorage = mockk()
    private val appInfoProvider: AppInfoProvider = mockk()
    private val folderPathManager: FolderPathManager = mockk()
    private val json: Json = Json { ignoreUnknownKeys = true }
    private val ioDispatcher = UnconfinedTestDispatcher()
    private val dateTimeUtils: DateTimeUtils = mockk()

    private val repository = BackupRepositoryImpl(
        context = context,
        productsRepository = productsRepository,
        localDataStorage = localDataStorage,
        appInfoProvider = appInfoProvider,
        folderPathManager = folderPathManager,
        json = json,
        ioDispatcher = ioDispatcher,
        dateTimeUtils = dateTimeUtils
    )
}
