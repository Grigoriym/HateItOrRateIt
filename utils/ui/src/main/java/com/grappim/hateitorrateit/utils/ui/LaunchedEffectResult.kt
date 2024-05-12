package com.grappim.hateitorrateit.utils.ui

import java.time.Instant

/**
 * The idea was taken from here https://stackoverflow.com/questions/68876679/compose-not-listening-to-any-repeated-value
 */
data class LaunchedEffectResult<T>(
    val data: T,
    val timestamp: Long = Instant.now().toEpochMilli()
)
