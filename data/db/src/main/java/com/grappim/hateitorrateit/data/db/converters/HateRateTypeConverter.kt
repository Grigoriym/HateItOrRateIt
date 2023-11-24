package com.grappim.hateitorrateit.data.db.converters

import androidx.room.TypeConverter
import com.grappim.hateitorrateit.domain.HateRateType

class HateRateTypeConverter {

    @TypeConverter
    fun toHateRate(value: String): HateRateType = enumValueOf(value)

    @TypeConverter
    fun fromHateRate(value: HateRateType) = value.name
}