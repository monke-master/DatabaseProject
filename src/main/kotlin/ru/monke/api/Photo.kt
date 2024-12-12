package ru.monke.api

import java.io.File

fun savePhotoToFileSystem(photoData: ByteArray?, name: String): String {
    val directory = File("uploaded_photos")
    if (!directory.exists()) {
        directory.mkdirs()
    }

    val fileName = "$name-${System.currentTimeMillis()}.jpg"
    val file = File(directory, fileName)
    photoData?.let {
        file.writeBytes(it)
    }

    return "/${file.path}"
}