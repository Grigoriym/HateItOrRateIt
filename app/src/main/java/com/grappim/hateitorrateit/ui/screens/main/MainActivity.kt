package com.grappim.hateitorrateit.ui.screens.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.grappim.hateitorrateit.appupdateapi.AppUpdateChecker
import com.grappim.hateitorrateit.appupdateapi.UpdateState
import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var appUpdateChecker: AppUpdateChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        checkForAppUpdates()
        observeUpdateState()

        setContent {
            val state by viewModel.viewState.collectAsStateWithLifecycle()
            val darkTheme = shouldUseDarkTheme(mainActivityViewState = state)
            HateItOrRateItTheme(darkTheme = darkTheme) {
                MainScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateChecker.registerUpdateListener()
        appUpdateChecker.checkUpdateStateOnResume()
    }

    override fun onPause() {
        super.onPause()
        appUpdateChecker.unregisterUpdateListener()
    }

    private fun observeUpdateState() {
        lifecycleScope.launch {
            appUpdateChecker.updateState.collectLatest { state ->
                when (state) {
                    is UpdateState.UpdateDownloaded -> showRestartSnackbar()
                }
            }
        }
    }

    private fun showRestartSnackbar() {
        Snackbar.make(
            window.decorView.rootView,
            getString(RString.app_update_downloaded),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(getString(RString.restart)) { appUpdateChecker.completeUpdate() }
            show()
        }
    }

    private fun checkForAppUpdates() {
        if (viewModel.inAppUpdateEnabled.value.not()) {
            return
        }
        appUpdateChecker.checkAndRequestUpdate()
    }
}

@Composable
private fun shouldUseDarkTheme(mainActivityViewState: MainActivityViewState): Boolean =
    when (mainActivityViewState.darkThemeConfig) {
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    }
