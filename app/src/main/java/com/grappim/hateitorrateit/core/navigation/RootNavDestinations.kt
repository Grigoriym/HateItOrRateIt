package com.grappim.hateitorrateit.core.navigation

sealed interface RootNavDestinations {
    val route: String

    data object Home : RootNavDestinations {
        override val route: String = "root_home"
    }

    data object HateOrRate : RootNavDestinations {
        override val route: String = "hate_or_rate"
    }

    data object Details : RootNavDestinations {
        private const val PREFIX = "details/"
        const val KEY = "productId"
        override val route: String = "${PREFIX}${KEY}={$KEY}"
        fun getRouteWithProductId(productId: Long) = "${PREFIX}${KEY}=$productId"
    }

    data object DetailsImage : RootNavDestinations {
        const val KEY_INDEX = "keyIndex"
        const val KEY_PRODUCT_ID = "keyProductId"

        private const val PREFIX = "detailsImage/"

        override val route: String = "${PREFIX}?productId={$KEY_PRODUCT_ID}&index={$KEY_INDEX}"
        fun getRouteWithUri(productId: String, index: Int) = "${PREFIX}?productId=$productId&index=$index"
    }
}