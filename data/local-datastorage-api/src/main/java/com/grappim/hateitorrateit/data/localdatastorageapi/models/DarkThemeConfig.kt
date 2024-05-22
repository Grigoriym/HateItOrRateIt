package com.grappim.hateitorrateit.data.localdatastorageapi.models

enum class DarkThemeConfig(val value: String) {
    FOLLOW_SYSTEM("follow_system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromValue(value: String?): DarkThemeConfig? =
            DarkThemeConfig.entries.firstOrNull { it.value == value }

        fun default() = FOLLOW_SYSTEM
    }
}

fun DarkThemeConfig.isSystemDefault() = this == DarkThemeConfig.FOLLOW_SYSTEM

fun DarkThemeConfig.isDark() = this == DarkThemeConfig.DARK

fun DarkThemeConfig.isLight() = this == DarkThemeConfig.LIGHT
