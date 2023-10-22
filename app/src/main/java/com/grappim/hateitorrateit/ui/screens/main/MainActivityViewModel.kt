package com.grappim.hateitorrateit.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.grappim.hateitorrateit.core.navigation.RootNavDestinations
import com.grappim.hateitorrateit.core.navigation.RootNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val rootNavigator: RootNavigator
) : ViewModel() {

    val navigationDestination
        get() = rootNavigator.navigationDestination

    fun setupNavController(navController: NavController) {
        rootNavigator.setupNavController(navController)
    }

    fun goToHateIt() {
        rootNavigator.navigateTo(RootNavDestinations.HateOrRate)
    }
}
