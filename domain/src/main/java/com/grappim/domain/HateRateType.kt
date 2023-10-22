package com.grappim.domain

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