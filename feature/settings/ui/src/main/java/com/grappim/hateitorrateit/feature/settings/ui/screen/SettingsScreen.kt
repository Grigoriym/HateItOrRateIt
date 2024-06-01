package com.grappim.hateitorrateit.feature.settings.ui.screen

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.data.localdatastorageapi.models.isDark
import com.grappim.hateitorrateit.data.localdatastorageapi.models.isLight
import com.grappim.hateitorrateit.data.localdatastorageapi.models.isSystemDefault
import com.grappim.hateitorrateit.feature.settings.ui.R
import com.grappim.hateitorrateit.uikit.icons.PlatoIconType
import com.grappim.hateitorrateit.uikit.theme.AtomicTangerine
import com.grappim.hateitorrateit.uikit.theme.Feijoa
import com.grappim.hateitorrateit.uikit.widgets.PlatoAlertDialog
import com.grappim.hateitorrateit.uikit.widgets.PlatoCard
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer16
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer32
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer8
import com.grappim.hateitorrateit.uikit.widgets.PlatoIcon
import com.grappim.hateitorrateit.uikit.widgets.PlatoLoadingDialog
import com.grappim.hateitorrateit.uikit.widgets.PlatoOutlinedButton
import com.grappim.hateitorrateit.uikit.widgets.PlatoTopBar
import com.grappim.hateitorrateit.uikit.widgets.text.TextH5
import com.grappim.hateitorrateit.utils.ui.asString
import com.grappim.hateitorrateit.utils.ui.type.color
import com.grappim.hateitorrateit.utils.ui.type.icon

const val CRASHLYTICS_TILE_TAG = "crashlytics_tile_tag"
const val ANALYTICS_TILE_TAG = "analytics_tile_tag"

private const val ANIMATION_DURATION = 500

@Composable
fun SettingsRoute(goBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    DisposableEffect(Unit) {
        state.trackScreenStart()
        onDispose { }
    }
    SettingsScreen(
        state = state,
        goBack = goBack
    )
}

@Composable
internal fun SettingsScreen(state: SettingsViewState, goBack: () -> Unit) {
    SettingsScreenContent(
        state = state,
        goBack = goBack
    )
}

@Composable
private fun SettingsScreenContent(state: SettingsViewState, goBack: () -> Unit) {
    Scaffold(
        topBar = {
            PlatoTopBar(
                text = stringResource(id = R.string.settings),
                goBack = goBack
            )
        }
    ) { padding ->
        PlatoLoadingDialog(state.isLoading)

        PlatoAlertDialog(
            text = stringResource(id = R.string.are_you_sure_clear_all_data),
            dismissButtonText = stringResource(id = R.string.no),
            showAlertDialog = state.showAlertDialog,
            onDismissRequest = {
                state.onDismissDialog()
            },
            onConfirmButtonClicked = {
                state.onAlertDialogConfirmButtonClicked()
            },
            onDismissButtonClicked = {
                state.onDismissDialog()
            }
        )

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                PlatoHeightSpacer8()
            }
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LocaleDropdownMenu(state)
                }
            }

            item {
                ListItem(
                    modifier = Modifier.clickable {
                        state.onClearDataClicked()
                    },
                    text = {
                        Text(text = stringResource(id = R.string.clear_data))
                    }
                )
            }
            item {
                ListItem(
                    modifier = Modifier.clickable {
                        state.setNewType()
                    },
                    text = {
                        Text(text = stringResource(id = R.string.default_type))
                    },
                    trailing = {
                        TypeIcon(state = state)
                    }
                )
            }
            item {
                ListItem(
                    modifier = Modifier
                        .clickable {
                            state.onCrashlyticsToggle()
                        }
                        .testTag(CRASHLYTICS_TILE_TAG),
                    text = {
                        Text(text = stringResource(id = R.string.toggle_crashlytics))
                    },
                    trailing = {
                        FeatureEnabledIcon(state.isCrashesCollectionEnabled)
                    },
                    secondaryText = {
                        Text(text = stringResource(id = R.string.crashlytics_settings_subtitle))
                    }
                )
            }
            item {
                ListItem(
                    modifier = Modifier
                        .clickable {
                            state.onAnalyticsToggle()
                        }
                        .testTag(ANALYTICS_TILE_TAG),
                    text = {
                        Text(text = stringResource(id = R.string.toggle_analytics))
                    },
                    trailing = {
                        FeatureEnabledIcon(state.isAnalyticsCollectionEnabled)
                    },
                    secondaryText = {
                        Text(text = stringResource(id = R.string.analytics_settings_subtitle))
                    }
                )
            }

            item {
                PlatoHeightSpacer16()
                DarkModePreferencesContent(state = state)
            }

            item {
                GithubRepoContent(state = state)
            }

            item {
                PrivacyPolicyContent(state = state)
            }

            item {
                PlatoHeightSpacer16()
                VersionContent(state = state)
            }

            item {
                PlatoHeightSpacer32()
            }
        }
    }
}

@Composable
private fun VersionContent(state: SettingsViewState) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = state.appInfo)
    }
}

@Composable
private fun LocaleDropdownMenu(state: SettingsViewState) {
    var expanded by remember {
        mutableStateOf(false)
    }
    ExposedDropdownMenuBox(
        modifier = Modifier,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            value = stringResource(id = R.string.language),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            state.localeOptions.keys.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(
                                state.localeOptions[option]
                            )
                        )
                    }
                ) {
                    Text(text = option.asString(LocalContext.current))
                }
            }
        }
    }
}

@Composable
private fun GithubRepoContent(state: SettingsViewState) {
    if (state.githubRepoLink.isNotEmpty()) {
        val uriHandler = LocalUriHandler.current
        PlatoHeightSpacer16()
        PlatoOutlinedButton(
            painter = painterResource(id = R.drawable.github_mark),
            text = stringResource(id = R.string.github_repo_link),
            onClicked = { uriHandler.openUri(state.githubRepoLink) }
        )
    }
}

@Composable
private fun PrivacyPolicyContent(state: SettingsViewState) {
    if (state.privacyPolicyLink.isNotEmpty()) {
        val uriHandler = LocalUriHandler.current
        PlatoHeightSpacer16()
        PlatoOutlinedButton(
            imageVector = PlatoIconType.PrivacyPolicy.imageVector,
            text = stringResource(id = R.string.privacy_policy_link),
            onClicked = { uriHandler.openUri(state.privacyPolicyLink) }
        )
    }
}

@Composable
private fun DarkModePreferencesContent(state: SettingsViewState) {
    PlatoCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column {
            PlatoHeightSpacer8()
            TextH5(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.dark_mode_preferences)
            )
            PlatoRadioButton(
                selected = state.darkThemeConfig.isSystemDefault(),
                onClick = {
                    state.onDarkThemeConfigClicked(DarkThemeConfig.FOLLOW_SYSTEM)
                },
                text = stringResource(id = R.string.system_default)
            )
            PlatoRadioButton(
                selected = state.darkThemeConfig.isLight(),
                onClick = {
                    state.onDarkThemeConfigClicked(DarkThemeConfig.LIGHT)
                },
                text = stringResource(id = R.string.light)
            )
            PlatoRadioButton(
                selected = state.darkThemeConfig.isDark(),
                onClick = {
                    state.onDarkThemeConfigClicked(DarkThemeConfig.DARK)
                },
                text = stringResource(id = R.string.dark)
            )
        }
    }
}

@Composable
private fun PlatoRadioButton(selected: Boolean, onClick: () -> Unit, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = text)
    }
}

@Composable
fun TypeIcon(state: SettingsViewState) {
    Crossfade(
        targetState = state.type,
        label = "type_crossfade_icon",
        animationSpec = tween(ANIMATION_DURATION)
    ) { type ->
        PlatoIcon(
            modifier = Modifier
                .testTag(type.icon().name),
            imageVector = type.icon(),
            tint = type.color()
        )
    }
}

@Composable
fun FeatureEnabledIcon(state: Boolean) {
    Crossfade(
        targetState = state,
        label = "custom_switch_label",
        animationSpec = tween(ANIMATION_DURATION)
    ) { enabled ->
        val imageVector = if (enabled) {
            PlatoIconType.CheckCircleOutline.imageVector
        } else {
            PlatoIconType.HighlightOff.imageVector
        }
        Icon(
            modifier = Modifier
                .testTag(imageVector.name),
            imageVector = imageVector,
            contentDescription = null,
            tint = if (enabled) {
                Feijoa
            } else {
                AtomicTangerine
            }
        )
    }
}
