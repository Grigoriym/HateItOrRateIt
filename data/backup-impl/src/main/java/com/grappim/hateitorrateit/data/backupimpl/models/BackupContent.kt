package com.grappim.hateitorrateit.data.backupimpl.models

import com.grappim.hateitorrateit.data.backupapi.models.ExportData

internal data class BackupContent(val exportData: ExportData, val images: Map<String, ByteArray>)
