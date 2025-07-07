package com.openclassrooms.vitesse.data.converter

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Used to convert between LocalDate and String when storing in the database
class LocalDateConverter {
  private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

  @TypeConverter
  fun fromString(value: String?): LocalDate? {
    return value?.let { LocalDate.parse(it, formatter) }
  }

  @TypeConverter
  fun toString(date: LocalDate?): String? {
    return date?.format(formatter)
  }
}