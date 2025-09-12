package com.openclassrooms.vitesse.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun persistImageToAppStorage(context: Context, source: Uri, existing: String?): String? {
  return try {
    if (!existing.isNullOrBlank()) {
      deleteImageIfLocal(existing)
    }
    val photosDir = File(context.filesDir, "photos")
    if (!photosDir.exists()) photosDir.mkdirs()
    val ext = when (context.contentResolver.getType(source)) {
      "image/png" -> ".png"
      "image/webp" -> ".webp"
      else -> ".jpg"
    }
    val outFile = File(photosDir, UUID.randomUUID().toString() + ext)
    context.contentResolver.openInputStream(source).use { input ->
      FileOutputStream(outFile).use { output ->
        if (input != null) input.copyTo(output)
      }
    }
    outFile.toURI().toString()
  } catch (_: Exception) {
    null
  }
}

fun deleteImageIfLocal(uriString: String?) {
  if (uriString.isNullOrBlank()) return
  try {
    val uri = java.net.URI(uriString)
    if (uri.scheme.equals("file", ignoreCase = true)) {
      val file = File(uri)
      if (file.exists()) file.delete()
    }
  } catch (_: Exception) {
    // ignore
  }
}

