package com.grappim.hateitorrateit.core.navigation

sealed interface NavDestinations {
    val route: String

    data object BottomBarNavDestination : NavDestinations {
        override val route: String = "root_home"
    }

    data object ProductManager : NavDestinations {
        private const val PREFIX = "product_manager"

        const val KEY_EDIT_PRODUCT_ID = "keyEditProductId"

        override val route: String = "$PREFIX/?$KEY_EDIT_PRODUCT_ID={$KEY_EDIT_PRODUCT_ID}"

        fun getRouteToNavigate(id: String?) = "$PREFIX/?$KEY_EDIT_PRODUCT_ID=$id"
    }

    data object Details : NavDestinations {
        private const val PREFIX = "details/"
        const val KEY = "productId"
        override val route: String = "$PREFIX$KEY={$KEY}"

        const val IS_FROM_EDIT = "is_from_edit"
        fun getRouteToNavigate(productId: Long) = "$PREFIX$KEY=$productId"
    }

    data object DetailsImage : NavDestinations {
        const val KEY_INDEX = "keyIndex"
        const val KEY_PRODUCT_ID = "keyProductId"

        private const val PREFIX = "detailsImage/"

        override val route: String = "$PREFIX?productId={$KEY_PRODUCT_ID}&index={$KEY_INDEX}"
        fun getRouteToNavigate(productId: String, index: Int) =
            "$PREFIX?productId=$productId&index=$index"
    }
}
