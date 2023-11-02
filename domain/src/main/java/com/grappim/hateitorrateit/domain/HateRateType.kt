package com.grappim.hateitorrateit.domain

enum class HateRateType {
    HATE,
    RATE;

    companion object {
        fun changeType(current: HateRateType): HateRateType = when (current) {
            HATE -> RATE
            RATE -> HATE
        }
    }
}