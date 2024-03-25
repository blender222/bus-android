package com.ashtar.bus.data.database

import androidx.room.TypeConverter
import com.ashtar.bus.common.StopStatus

class Converters {
    @TypeConverter
    fun toStopStatus(value: Int?): StopStatus? {
        return if (value == null) {
            null
        } else {
            StopStatus.fromCode(value)
        }
    }

    @TypeConverter
    fun fromStopStatus(value: StopStatus?): Int? {
        return value?.code
    }
}