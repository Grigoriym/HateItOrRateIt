package com.grappim.hateitorrateit.core.navigation

import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootNavigator @Inject constructor(

) {

    private lateinit var navController: NavController

    private val _navigationDestination = MutableSharedFlow<RootNavDestinations>()
    val navigationDestination
        get() = _navigationDestination.asSharedFlow()

    fun setupNavController(controller: NavController) {
        navController = controller
    }

    fun navigateTo(rootNavDestinations: RootNavDestinations) {
        _navigationDestination.tryEmit(rootNavDestinations)
    }
}