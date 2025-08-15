package com.grappim.hateitorrateit.feature.productmanager.ui.navigation

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
data class ProductManagerNavDestination(val productId: Long?)

fun NavController.navigateToProductManager(productId: Long? = null) {
    navigate(route = ProductManagerNavDestination(productId = productId))
}
