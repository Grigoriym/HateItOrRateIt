package com.grappim.hateitorrateit.ui.screens.rateorhate

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.domain.HateRateType
import com.grappim.hateitorrateit.core.DataCleaner
import com.grappim.hateitorrateit.core.NativeText
import com.grappim.hateitorrateit.core.SnackbarStateViewModel
import com.grappim.hateitorrateit.core.SnackbarStateViewModelImpl
import com.grappim.hateitorrateit.data.DocsRepository
import com.grappim.hateitorrateit.data.storage.local.LocalDataStorage
import com.grappim.hateitorrateit.model.CreateDocument
import com.grappim.hateitorrateit.utils.CameraTakePictureData
import com.grappim.hateitorrateit.utils.FileData
import com.grappim.hateitorrateit.utils.FileUtils
import com.grappim.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HateOrRateViewModel @Inject constructor(
    private val fileUtils: FileUtils,
    private val docsRepository: DocsRepository,
    private val dataCleaner: DataCleaner,
    private val localDataStorage: LocalDataStorage,
) : ViewModel(),
    SnackbarStateViewModel by SnackbarStateViewModelImpl() {

    private val _viewState = MutableStateFlow(
        HateOrRateViewState(
            setDescription = ::setDescription,
            setName = ::setName,
            setShop = ::setShop,
            onRemoveFileTriggered = ::removeFile,
            onAddImageFromGalleryClicked = ::addImageFromGallery,
            onAddCameraPictureClicked = ::addCameraPicture,
            removeData = ::removeData,
            saveData = ::saveData,
            createDocument = ::createDocument,
            getCameraImageFileUri = ::getCameraImageFileUri,
            onTypeClicked = ::onTypeClicked,
            onShowAlertDialog = ::onShowAlertDialog,
            onForceQuit = ::onForceQuit,
        )
    )
    val viewState = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            localDataStorage.typeFlow.collect { value ->
                _viewState.update {
                    it.copy(type = value)
                }
            }
        }

        addDraftDoc()
    }

    private fun onForceQuit() {
        _viewState.update {
            it.copy(forceQuit = true)
        }
    }

    private fun onShowAlertDialog(show: Boolean) {
        _viewState.update {
            it.copy(
                showAlertDialog = show
            )
        }
    }

    private fun onTypeClicked(newType: HateRateType) {
        if (_viewState.value.type == newType) return
        _viewState.update {
            it.copy(type = HateRateType.changeType(it.type))
        }
    }

    private fun addDraftDoc() {
        viewModelScope.launch {
            val draftDoc = docsRepository.addDraftDocument()
            _viewState.update {
                it.copy(
                    type = draftDoc.type,
                    draftDocument = draftDoc,
                )
            }
        }
    }

    private fun addImageFromGallery(uri: Uri) {
        viewModelScope.launch {
            val fileData = fileUtils.getFileUrisFromGalleryUri(
                uri = uri,
                folderName = requireNotNull(viewState.value.draftDocument).folderName
            )
            addFileData(fileData)
        }
    }

    private fun addFileData(fileData: FileData) {
        val result = _viewState.value.filesUris + fileData
        _viewState.update {
            it.copy(filesUris = result)
        }
    }

    private fun addCameraPicture(cameraTakePictureData: CameraTakePictureData) {
        viewModelScope.launch {
            val fileData = fileUtils.getFileDataFromCameraPicture(
                cameraTakePictureData = cameraTakePictureData
            )
            addFileData(fileData)
        }
    }

    private fun getCameraImageFileUri(): CameraTakePictureData =
        fileUtils.getFileUriForTakePicture((requireNotNull(viewState.value.draftDocument)).folderName)

    private fun removeData() {
        viewModelScope.launch {
            val draftDoc = requireNotNull(viewState.value.draftDocument)
            dataCleaner.clearDocumentData(draftDoc)
        }
    }

    private fun saveData() {
        viewModelScope.launch {
            saveDocument()
        }
    }

    private fun setName(name: String) {
        _viewState.update {
            it.copy(documentName = name)
        }
    }

    private fun setDescription(description: String) {
        _viewState.update {
            it.copy(description = description)
        }
    }

    private fun setShop(shop: String) {
        _viewState.update {
            it.copy(shop = shop)
        }
    }

    private fun saveDocument() {
        viewModelScope.launch {
            val currentDraft = requireNotNull(viewState.value.draftDocument)
            val name = _viewState.value.documentName.trim()
            val description = _viewState.value.description.trim()
            val shop = _viewState.value.shop.trim()
            val type = _viewState.value.type

            docsRepository.addDocument(
                CreateDocument(
                    id = currentDraft.id,
                    name = name,
                    filesUri = _viewState.value.filesUris.map {
                        fileUtils.toDocumentFileData(it)
                    },
                    createdDate = currentDraft.date,
                    documentFolderName = currentDraft.folderName,
                    description = description,
                    shop = shop,
                    type = type,
                )
            )
            _viewState.update {
                it.copy(isCreated = true)
            }
        }
    }

    private fun createDocument() {
        viewModelScope.launch {
            when {
                _viewState.value.documentName.isBlank() -> {
                    setSnackbarMessageSuspend(NativeText.Resource(R.string.set_name))
                }

                _viewState.value.filesUris.isEmpty() -> {
                    setSnackbarMessageSuspend(NativeText.Resource(R.string.add_file))
                }

                else -> {
                    saveDocument()
                }
            }
        }
    }

    private fun removeFile(fileData: FileData) {
        if (fileUtils.deleteFile(fileData.uri)) {
            Timber.d("file removed: $fileData")
            _viewState.update { currentState ->
                val updatedFilesUris = currentState.filesUris.filterNot { it == fileData }
                currentState.copy(filesUris = updatedFilesUris)
            }
        }
    }
}
