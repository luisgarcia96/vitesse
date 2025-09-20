package com.openclassrooms.vitesse.data.converter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class LocalDateConverterTest {
  private val converter = LocalDateConverter()

  @Test
  fun `fromString returns LocalDate when input is ISO string`() {
    val date = converter.fromString("2024-10-05")

    assertEquals(LocalDate.of(2024, 10, 5), date)
  }

  @Test
  fun `conversion handles null values`() {
    assertNull(converter.fromString(null))
    assertNull(converter.toString(null))
  }

  @Test
  fun `toString returns ISO string`() {
    val text = converter.toString(LocalDate.of(2023, 1, 15))

    assertEquals("2023-01-15", text)
  }
}
