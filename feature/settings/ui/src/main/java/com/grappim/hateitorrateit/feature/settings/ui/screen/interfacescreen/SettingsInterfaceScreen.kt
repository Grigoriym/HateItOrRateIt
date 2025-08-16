@file:OptIn(ExperimentalMaterial3Api::class)

package com.grappim.hateitorrateit.feature.settings.ui.screen.interfacescreen

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.grappim.hateitorrateit.data.localdatastorageapi.models.DarkThemeConfig
import com.grappim.hateitorrateit.data.localdatastorageapi.models.isDark
import com.grappim.hateitorrateit.data.localdatastorageapi.models.isLight
import com.grappim.hateitorrateit.data.localdatastorageapi.models.isSystemDefault
import com.grappim.hateitorrateit.feature.settings.ui.widgets.PlatoRadioButton
import com.grappim.hateitorrateit.strings.RString
import com.grappim.hateitorrateit.uikit.theme.HateItOrRateItTheme
import com.grappim.hateitorrateit.uikit.utils.PreviewDarkLight
import com.grappim.hateitorrateit.uikit.widgets.PlatoCard
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer16
import com.grappim.hateitorrateit.uikit.widgets.PlatoHeightSpacer8
import com.grappim.hateitorrateit.uikit.widgets.text.TextH5
import com.grappim.hateitorrateit.uikit.widgets.topbar.LocalTopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarBackButtonState
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarConfig
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarController
import com.grappim.hateitorrateit.uikit.widgets.topbar.TopBarState
import com.grappim.hateitorrateit.utils.ui.NativeText
import com.grappim.hateitorrateit.utils.ui.asString

@Composable
fun SettingsInterfaceScreenRoute(viewModel: SettingsInterfaceViewModel = hiltViewModel()) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val topBarController: TopBarController = LocalTopBarConfig.current

    LaunchedEffect(Unit) {
        topBarController.update(
            TopBarConfig(
                state = TopBarState.Visible(
                    title = NativeText.Resource(RString.interface_screen_name),
                    topBarBackButtonState = TopBarBackButtonState.Visible()
                )
            )
        )
    }
    SettingsInterfaceScreen(state = state)
}

@Composable
private fun SettingsInterfaceScreen(state: SettingsInterfaceViewState) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
                .padding(horizontal = 16.dp)
        ) {
            PlatoHeightSpacer16()

            LocaleSelectionSection(state)

            PlatoHeightSpacer16()

            DarkModePreferencesSection(state = state)

            PlatoHeightSpacer16()
        }
    }
}

@Composable
private fun LocaleSelectionSection(state: SettingsInterfaceViewState) {
    PlatoCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            TextH5(
                text = stringResource(id = RString.language),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LocaleDropdownMenu(state)
        }
    }
}

@Composable
private fun LocaleDropdownMenu(state: SettingsInterfaceViewState) {
    var expanded by remember {
        mutableStateOf(false)
    }
    ExposedDropdownMenuBox(
        modifier = Modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            value = stringResource(id = RString.language),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            state.localeOptions.keys.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(text = option.asString(LocalContext.current))
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    onClick = {
                        expanded = false
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(
                                state.localeOptions[option]
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun DarkModePreferencesSection(state: SettingsInterfaceViewState) {
    PlatoCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            TextH5(
                text = stringResource(id = RString.dark_mode_preferences),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ThemeOption(
                selected = state.darkThemeConfig.isSystemDefault(),
                onClick = {
                    state.onDarkThemeConfigClicked(DarkThemeConfig.FOLLOW_SYSTEM)
                },
                text = stringResource(id = RString.system_default)
            )

            PlatoHeightSpacer8()

            ThemeOption(
                selected = state.darkThemeConfig.isLight(),
                onClick = {
                    state.onDarkThemeConfigClicked(DarkThemeConfig.LIGHT)
                },
                text = stringResource(id = RString.light)
            )

            PlatoHeightSpacer8()

            ThemeOption(
                selected = state.darkThemeConfig.isDark(),
                onClick = {
                    state.onDarkThemeConfigClicked(DarkThemeConfig.DARK)
                },
                text = stringResource(id = RString.dark)
            )
        }
    }
}

@Composable
private fun ThemeOption(
    selected: Boolean,
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    PlatoRadioButton(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        text = text
    )
}

@[Composable PreviewDarkLight]
private fun SettingsInterfaceScreenPreview() {
    HateItOrRateItTheme {
        SettingsInterfaceScreen(
            state = SettingsInterfaceViewState()
        )
    }
}
