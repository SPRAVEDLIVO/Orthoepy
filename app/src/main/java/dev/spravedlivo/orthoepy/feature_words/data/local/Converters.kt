package dev.spravedlivo.orthoepy.feature_words.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

@ProvidedTypeConverter
object Converters {
    @TypeConverter
    fun stringToLocalTime(string: String): LocalTime {
        return LocalTime.parse(string)
    }

    @TypeConverter
    fun localTimeToString(localTime: LocalTime): String {
        return localTime.toString()
    }

    @TypeConverter
    fun stringToLocalDate(string: String): LocalDate {
        return LocalDate.parse(string)
    }

    @TypeConverter
    fun localDateToString(localDate: LocalDate): String {
        return localDate.toString()
    }

    @TypeConverter
    fun stringToLocalDateTime(string: String): LocalDateTime {
        return LocalDateTime.parse(string)
    }

    @TypeConverter
    fun localDateTimeToString(localDateTime: LocalDateTime): String {
        return localDateTime.toString()
    }

}