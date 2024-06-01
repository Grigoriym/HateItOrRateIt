package com.grappim.hateitorrateit.core.navigation

import androidx.compose.ui.graphics.vector.ImageVector

interface BottomNavDestinations {
    val route: String
    val title: String
    val imageVector: ImageVector
}
