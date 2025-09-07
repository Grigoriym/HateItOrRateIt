package com.grappim.hateitorrateit.feature.details.ui.navigation

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

const val IS_FROM_EDIT = "is_from_edit"

@Serializable
data class DetailsNavDestination(val productId: Long)

fun NavController.navigateToDetails(productId: Long) {
    navigate(route = DetailsNavDestination(productId))
}
