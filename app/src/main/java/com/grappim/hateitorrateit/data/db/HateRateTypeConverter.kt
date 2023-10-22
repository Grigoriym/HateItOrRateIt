package com.grappim.hateitorrateit.data.db

import androidx.room.TypeConverter
import com.grappim.domain.HateRateType

class HateRateTypeConverter {

    @TypeConverter
    fun toHateRate(value: String) = enumValueOf<HateRateType>(value)

    @TypeConverter
    fun fromHateRate(value: HateRateType) = value.name
}