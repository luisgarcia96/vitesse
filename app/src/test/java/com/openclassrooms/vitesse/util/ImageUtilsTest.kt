package com.openclassrooms.vitesse.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ImageUtilsTest {

  @Test
  fun `deleteImageIfLocal removes local file`() {
    val tempFile = File.createTempFile("image-utils", ".tmp").apply {
      writeText("content")
      deleteOnExit()
    }

    val uriString = tempFile.toURI().toString()
    assertTrue(tempFile.exists())

    deleteImageIfLocal(uriString)

    assertFalse(tempFile.exists())
  }

  @Test
  fun `deleteImageIfLocal ignores remote uri`() {
    val tempFile = File.createTempFile("image-utils", ".tmp").apply {
      writeText("content")
      deleteOnExit()
    }

    deleteImageIfLocal("https://example.com/image.jpg")

    assertTrue(tempFile.exists())
    tempFile.delete()
  }
}
