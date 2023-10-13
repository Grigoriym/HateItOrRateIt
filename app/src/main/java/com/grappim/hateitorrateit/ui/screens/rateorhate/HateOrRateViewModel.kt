package com.grappim.hateitorrateit.ui.screens.rateorhate

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.hateitorrateit.R
import com.grappim.hateitorrateit.core.DataCleaner
import com.grappim.hateitorrateit.core.NativeText
import com.grappim.hateitorrateit.core.SnackbarStateViewModel
import com.grappim.hateitorrateit.core.SnackbarStateViewModelImpl
import com.grappim.hateitorrateit.data.DocsRepository
import com.grappim.hateitorrateit.domain.DocumentFileData
import com.grappim.hateitorrateit.model.CreateDocument
import com.grappim.hateitorrateit.utils.CameraTakePictureData
import com.grappim.hateitorrateit.utils.DraftDocument
import com.grappim.hateitorrateit.utils.FileData
import com.grappim.hateitorrateit.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HateOrRateViewModel @Inject constructor(
    private val fileUtils: FileUtils,
    private val docsRepository: DocsRepository,
    private val dataCleaner: DataCleaner,
) : ViewModel(),
    SnackbarStateViewModel by SnackbarStateViewModelImpl() {

    private val _draftDocument = MutableStateFlow<DraftDocument?>(null)
    private val draftDocument: Flow<DraftDocument>
        get() = _draftDocument.filterNotNull()

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
        )
    )
    val viewState = _viewState.asStateFlow()

    init {
        addDraftDoc()
    }

    private fun addDraftDoc() {
        viewModelScope.launch {
            _draftDocument.value = docsRepository.addDraftDocument()
        }
    }

    private fun addImageFromGallery(uri: Uri) {
        viewModelScope.launch {
            val fileData = fileUtils.getFileUrisFromGalleryUri(uri, draftDocument.first())
            addFileData(fileData)
        }
    }

    private fun addFileData(fileData: FileData) {
        val result = _viewState.value.filesUris.toMutableList() + fileData
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
        fileUtils.getFileUriForTakePicture(_draftDocument.value!!.folderName)

    private fun removeData() {
        viewModelScope.launch {
            val draftDoc = draftDocument.first()
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
            val currentDraft = draftDocument.first()
            val name = _viewState.value.documentName.ifEmpty {
                currentDraft.folderName
            }
            val description = _viewState.value.description
            val shop = _viewState.value.shop

            docsRepository.addDocument(
                CreateDocument(
                    id = currentDraft.id,
                    name = name,
                    filesUri = _viewState.value.filesUris.map {
                        DocumentFileData(
                            name = it.name,
                            mimeType = it.mimeType,
                            uriPath = it.uri.path ?: "",
                            uriString = it.uri.toString(),
                            size = it.size,
                            md5 = it.md5
                        )
                    },
                    createdDate = currentDraft.date,
                    documentFolderName = currentDraft.folderName,
                    description = description,
                    shop = shop
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
                _viewState.value.documentName.isEmpty() -> {
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
        if (fileUtils.removeFile(fileData)) {
            Timber.d("file removed: $fileData")
            _viewState.update { currentState ->
                val updatedFilesUris = currentState.filesUris.filterNot { it == fileData }
                currentState.copy(filesUris = updatedFilesUris)
            }

//            _viewState.update { currentState ->
//                currentState.copy(
//                    filesUris = currentState.filesUris.toMutableList()
//                        .apply { remove(fileData) })
//            }
//            val newList = _viewState.value.filesUris.toMutableList()
//            newList.remove(fileData)
//            _viewState.update {
//                it.copy(filesUris = newList)
//            }
        }
    }
}