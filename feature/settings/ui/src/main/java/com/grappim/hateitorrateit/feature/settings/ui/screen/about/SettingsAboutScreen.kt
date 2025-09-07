package com.grappim.hateitorrateit.feature.settings.ui.screen.about

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grappim.hateitorrateit.feature.settings.ui.R
import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.uikit.icons.PlatoIconType
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer16
import com.grappim.hateitorrateit.uikit.widgets.PlatoOutlinedButton
import com.grappim.hateitorrateit.uikit.widgets.topbar.LocalTopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarBackButtonState
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarController
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarState
import com.grappim.hateitorrateit.utils.ui.NativeText

@Composable
fun SettingsAboutScreenRoute(viewModel: SettingsAboutScreenViewModel = hiltViewModel()) {
    val topBarController: TopBarController = LocalTopBarConfig.current
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        topBarController.update(
            TopBarConfig(
                state = TopBarState.Visible(
                    title = NativeText.Resource(RString.about),
                    topBarBackButtonState = TopBarBackButtonState.Visible()
                )
            )
        )
    }

    SettingsAboutScreen(state = state)
}

@Composable
private fun SettingsAboutScreen(state: SettingsAboutScreenState) {
    Surface {
        Column {
            GithubRepoContent(state = state)

            PrivacyPolicyContent(state = state)

            PlatoHeightSpacer16()
            VersionContent(state = state)
        }
    }
}

@Composable
private fun GithubRepoContent(state: SettingsAboutScreenState) {
    if (state.githubRepoLink.isNotEmpty()) {
        val uriHandler = LocalUriHandler.current
        PlatoHeightSpacer16()
        PlatoOutlinedButton(
            painter = painterResource(id = R.drawable.github_mark),
            text = stringResource(id = RString.github_repo_link),
            onClick = { uriHandler.openUri(state.githubRepoLink) }
        )
    }
}

@Composable
private fun PrivacyPolicyContent(state: SettingsAboutScreenState) {
    if (state.privacyPolicyLink.isNotEmpty()) {
        val uriHandler = LocalUriHandler.current
        PlatoHeightSpacer16()
        PlatoOutlinedButton(
            imageVector = PlatoIconType.PrivacyPolicy.imageVector,
            text = stringResource(id = RString.privacy_policy_link),
            onClick = { uriHandler.openUri(state.privacyPolicyLink) }
        )
    }
}

@Composable
private fun VersionContent(state: SettingsAboutScreenState) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = state.appInfo)
    }
}
