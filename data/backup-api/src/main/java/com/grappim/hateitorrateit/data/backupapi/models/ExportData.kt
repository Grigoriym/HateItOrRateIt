package com.grappim.hateitorrateit.data.backupapi.models

import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.data.repoapi.models.HateRateType
import kotlinx.serialization.Serializable

@Serializable
data class ExportData(
    val metadata: ExportMetadata,
    val products: List<ProductExport>,
    val settings: SettingsExport
)

@Serializable
data class ExportMetadata(
    val version: String,
    val appVersionName: String,
    val exportTimestamp: Long,
    val deviceInfo: String,
    val totalProducts: Int,
    val totalImages: Int
)

@Serializable
data class ProductExport(
    val id: Long,
    val name: String,
    val description: String,
    val shop: String,
    val type: HateRateType,
    val createdDate: String, // ISO 8601 format
    val productFolderName: String,
    val images: List<ProductImageExport>
)

@Serializable
data class ProductImageExport(
    val imageId: Long,
    val originalName: String,
    val exportFileName: String, // Name in the ZIP file
    val mimeType: String,
    val size: Long,
    val md5: String
)

@Serializable
data class SettingsExport(
    val defaultType: HateRateType,
    val darkThemeConfig: DarkThemeConfig,
    val analyticsEnabled: Boolean,
    val crashesEnabled: Boolean
)
