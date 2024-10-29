package com.example.taskbeat.ui.components

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.taskbeat.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CustomImageDisplay(
    context: Context,
    imageUrl: String,
) {
    val localImage = remember { getImageFromLocal(context, imageUrl) }

    val imagePainter = rememberAsyncImagePainter(
        if (localImage != null) {
            ImageRequest.Builder(context)
                .data(localImage)
                .apply {
                    placeholder(R.drawable.ic_launcher_foreground)
                    error(R.drawable.ic_launcher_foreground)
                }
                .build()
        }
        else {
            ImageRequest.Builder(context)
                .data(imageUrl)
                .apply {
                    listener(
                        onSuccess = { _, result ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val bitmap = (result.drawable as BitmapDrawable).bitmap
                                downloadImageToLocal(context, bitmap, imageUrl.substringAfterLast('/'))
                            }
                        },
                        onError = { _, throwable ->
                            Log.e("DBG", "Error in building ImageRequest: $throwable")
                        }
                    )
                }
                .build()
        }
    )

    Image(
        painter = imagePainter,
        contentDescription = null,
        alignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

fun getImageFromLocal(context: Context, imageUrl: String): Bitmap? {
    val fileName = imageUrl.substringAfterLast('/')

    val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
    val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
    val selectionArgs = arrayOf(fileName)

    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )

    cursor?.use {
        if (it.moveToFirst()) {
            val imagePath = it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
            return BitmapFactory.decodeFile(imagePath)
        }
    }

    Log.d("DBG", "Image $fileName not found in MediaStore")
    return null
}

fun downloadImageToLocal(context: Context, bitmap: Bitmap, fileName: String) {
    val extension = fileName.substringAfterLast(".", "").lowercase()
    val compressFormat = when (extension) {
        "png" -> Bitmap.CompressFormat.PNG
        "jpg", "jpeg" -> Bitmap.CompressFormat.JPEG
        "webp" -> Bitmap.CompressFormat.WEBP
        else -> Bitmap.CompressFormat.JPEG
    }

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(
            MediaStore.Images.Media.MIME_TYPE, when (extension) {
                "png" -> "image/png"
                "jpg", "jpeg" -> "image/jpeg"
                "webp" -> "image/webp"
                else -> "image/jpeg"
            })
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        context.contentResolver.openOutputStream(it).use { outputStream ->
            if (outputStream != null) {
                bitmap.compress(compressFormat, 100, outputStream)
            } else {
                Log.e("DBG", "Error saving image: OutputStream is null")
            }
        }
    } ?: run {
        Log.e("DBG", "Error saving image: URI is null")
    }
}