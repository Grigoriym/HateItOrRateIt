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
        const val KEY = "docId"
        override val route: String = "${PREFIX}docId={$KEY}"
        fun getRouteWithDocId(docId: Long) = "${PREFIX}docId=$docId"
    }

    data object DetailsImage : RootNavDestinations {
        const val KEY_INDEX = "keyIndex"
        const val KEY_DOC_ID = "keyDocId"

        private const val PREFIX = "detailsImage/"

        override val route: String = "${PREFIX}?docId={$KEY_DOC_ID}&index={$KEY_INDEX}"
        fun getRouteWithUri(docId: String, index: Int) = "${PREFIX}?docId=$docId&index=$index"
    }
}