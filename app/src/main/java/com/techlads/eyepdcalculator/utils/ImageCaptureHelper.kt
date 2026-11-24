package com.techlads.eyepdcalculator.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object ImageCaptureHelper {

    fun createTempImageUri(context: Context): Uri {
        return uriFor(context, createTempImageFile(context))
    }

    fun createTempImageFile(context: Context): File {
        return File.createTempFile("tmp_image_file", ".jpg", context.cacheDir).apply {
            deleteOnExit()
        }
    }

    fun uriFor(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }
}
