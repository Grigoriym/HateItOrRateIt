package com.grappim.hateitorrateit.data.backupimpl

import android.content.Context
import app.cash.turbine.test
import com.grappim.hateitorrateit.core.appinfoapi.AppInfoProvider
import com.grappim.hateitorrateit.data.backupapi.BackupRepository
import com.grappim.hateitorrateit.data.backupapi.models.BackupError
import com.grappim.hateitorrateit.data.backupapi.models.BackupPhase
import com.grappim.hateitorrateit.data.backupapi.models.BackupProgress
import com.grappim.hateitorrateit.data.backupapi.models.BackupResult
import com.grappim.hateitorrateit.data.backupapi.models.BackupState
import com.grappim.hateitorrateit.data.backupimpl.utils.BackupEligibilityChecker
import com.grappim.hateitorrateit.data.localdatastorageapi.LocalDataStorage
import com.grappim.hateitorrateit.data.repoapi.ProductsRepository
import com.grappim.hateitorrateit.utils.datetimeapi.DateTimeUtils
import com.grappim.hateitorrateit.utils.filesapi.pathmanager.FolderPathManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
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

    private val backupEligibilityChecker: BackupEligibilityChecker = mockk()

    private lateinit var sut: BackupRepository

    @Before
    fun setup() {
        sut = BackupRepositoryImpl(
            context = context,
            productsRepository = productsRepository,
            localDataStorage = localDataStorage,
            appInfoProvider = appInfoProvider,
            folderPathManager = folderPathManager,
            json = json,
            ioDispatcher = ioDispatcher,
            dateTimeUtils = dateTimeUtils,
            backupEligibilityChecker = backupEligibilityChecker
        )
    }

    @Test
    fun `on createBackupWithProgress with canCreateBackup error, sends failure`() = runTest {
        every { backupEligibilityChecker.canCreateBackup() } returns false

        sut.createBackupWithProgress().test {
            assertEquals(
                expected = awaitItem(),
                actual = BackupState.Progress(
                    BackupProgress(phase = BackupPhase.INITIALIZING)
                )
            )

            assertEquals(
                expected = awaitItem(),
                actual = BackupState.Completed(
                    BackupResult.Failure(
                        BackupError.STORAGE_PERMISSION_DENIED,
                        "Cannot access storage location for backup"
                    )
                )
            )

            awaitComplete()
        }
    }
}
