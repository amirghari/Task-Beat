package com.example.taskbeat.data

import android.Manifest
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.taskbeat.network.ModelDownloadApi
import com.example.taskbeat.network.createRetrofit
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class Gemma22BModel(
    context: Context
) {
    private var llmInference: LlmInference

    private val modelExists: Boolean = File(MODEL_PATH).exists()

    private val _partialResults = MutableSharedFlow<Pair<String, Boolean>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val partialResults: SharedFlow<Pair<String, Boolean>> = _partialResults.asSharedFlow()

    init {
        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(MODEL_PATH)
            .setMaxTokens(768)
            .setResultListener { partialResult, done ->
                _partialResults.tryEmit(partialResult to done)
            }
            .build()

        llmInference = LlmInference.createFromOptions(context, options)
    }

    private val systemEnforcement = "<start_of_turn>system\nYou are an AI called HealthBot resides within a mobile application called TaskBeat that provide health related tips and instructions<end_of_turn>\n"
    private val userEnforcement = ". Answer with moderate length. If the requests are unrelated to health, politely redirect the conversation to health topics. Always respond in a professional and health-focused manner."

    fun generateResponseAsync(prompt: String) {
        val insertionPoint = prompt.indexOf("<end_of_turn>")
        val modifiedUserPrompt = StringBuilder(prompt)
            .insert(insertionPoint, userEnforcement)
            .toString()
        val modifiedMessage = "$systemEnforcement$modifiedUserPrompt\n<start_of_turn>model\n"
        llmInference.generateResponseAsync(modifiedMessage)
    }

    companion object {
        private lateinit var MODEL_PATH: String
        private var instance: Gemma22BModel? = null

        fun getInstance(context: Context): Gemma22BModel {
            return if (instance != null) {
                instance!!
            } else {
                Gemma22BModel(context).also { instance = it }
            }
        }

        fun isOnLocalDevice(context: Context): Boolean {
            val targetDir = File(context.getExternalFilesDir(null), "model/llm")
            val fileName = "gemma2-2b-it-gpu-int8.bin"
            MODEL_PATH = "$targetDir/$fileName"

            if (!targetDir.exists()) {
                targetDir.mkdirs()
                return false
            }

            val targetFile = File(targetDir, fileName)

            return targetFile.exists()
        }

        suspend fun downloadModel(context: Context) {
            val targetDir = File(context.getExternalFilesDir(null), "model/llm")
            val targetFile = File(targetDir, "gemma2-2b-it-gpu-int8.bin")
            val url = "https://www.dropbox.com/scl/fi/izoppx7o9nt5b85nmw86i/gemma2-2b-it-gpu-int8.bin?rlkey=4qhlup9a4gcucnth7pbg4lxdi&st=0ewpxvga&dl=1"
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(url))

            request.setDestinationUri(Uri.fromFile(targetFile))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle("Downloading model")
                .setDescription("Downloading model...")
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)

            val downloadId = downloadManager.enqueue(request)

            monitorDownloadProgress(context, downloadManager, downloadId)
        }

        private suspend fun monitorDownloadProgress(context: Context, downloadManager: DownloadManager, downloadId: Long) {
            val query = DownloadManager.Query()
            query.setFilterById(downloadId)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(context, "download_channel")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Downloading model")
                .setProgress(100, 0, false)

            withContext(Dispatchers.IO) {
                var downloading = true

                while (downloading) {
                    val cursor = downloadManager.query(query)
                    if (cursor != null && cursor.moveToFirst()) {
                        val status = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val totalBytes = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                        val downloadedBytes = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)

                        when (status) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                downloading = false
                                builder.setContentText("Download complete")
                                    .setProgress(0, 0, false)
                                notificationManager.notify(downloadId.toInt(), builder.build())
                            }
                            DownloadManager.STATUS_FAILED -> {
                                downloading = false
                                builder.setContentText("Download failed")
                                    .setProgress(0, 0, false)
                                notificationManager.notify(downloadId.toInt(), builder.build())
                            }
                            DownloadManager.STATUS_RUNNING -> {
                                val progress = (downloadedBytes * 100 / totalBytes)
                                builder.setProgress(100, progress, false)
                                    .setContentText("Downloading model: $progress%")
                                notificationManager.notify(downloadId.toInt(), builder.build())
                            }
                        }
                    }
                    cursor.close()

                    delay(500)
                }
            }
        }


//        suspend fun downloadAndSaveModelToExternalStorage(context: Context) {
//            val fileId = "1QK5QejAyZSKUUpmSrMQ8qPQ0bYS0bGZl"
//            val fileName = "gemma2-2b-it-gpu-int8.bin"
//            val targetDirectory = File(context.filesDir, "model/llm")
//
//            if (!targetDirectory.exists()) {
//                targetDirectory.mkdirs()
//            }
//
//            val targetFile = File(targetDirectory, fileName)
//
//            if (targetFile.exists()) {
//                Log.d("DBG", "File already exists at: ${targetFile.absolutePath}")
//                return
//            }
//
//            val retrofitModelDownload = createRetrofit("https://www.dropbox.com/")
//            val api = retrofitModelDownload.create(ModelDownloadApi::class.java)
//
//            val channelId = "download_progress"
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = NotificationChannel(
//                    channelId, "Download Progress",
//                    NotificationManager.IMPORTANCE_LOW
//                )
//                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                notificationManager.createNotificationChannel(channel)
//            }
//
//            val notificationManager = NotificationManagerCompat.from(context)
//            val notificationId = 1
//            val builder = NotificationCompat.Builder(context, channelId)
//                .setContentTitle("Downloading model")
//                .setContentText("Download in progress")
//                .setSmallIcon(android.R.drawable.stat_sys_download)
//                .setProgress(100, 0, true)
//                .setOngoing(true)
//
//            if (ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return
//            }
//            notificationManager.notify(notificationId, builder.build())
//
//            try {
//                val response = api.downloadFile()
//                if (!response.isSuccessful) {
//                    Log.d("DBG", "Download failed: ${response.message()}")
//                    builder.setContentText("Download failed")
//                        .setProgress(0, 0, false)
//                        .setOngoing(false)
//                        .setSmallIcon(android.R.drawable.stat_notify_error)
//                    notificationManager.notify(notificationId, builder.build())
//                    return
//                }
//
//                val body = response.body() ?: throw IOException("Empty response body")
//                val contentLength = body.contentLength()
//
//                builder.setProgress(100, 0, false)
//
//                if (ActivityCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.POST_NOTIFICATIONS
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    return
//                }
//
//                notificationManager.notify(notificationId, builder.build())
//
//                withContext(Dispatchers.IO) {
//                    FileOutputStream(targetFile).use { outputStream ->
//                        body.byteStream().use { inputStream ->
//                            saveFileWithProgress(
//                                inputStream,
//                                outputStream,
//                                contentLength
//                            ) { progress ->
//                                builder.setProgress(100, progress, false)
//                                builder.setContentText("Downloading model: $progress%")
//                                notificationManager.notify(notificationId, builder.build())
//                            }
//                        }
//                    }
//
//                    builder.setContentText("Download complete")
//                        .setProgress(0, 0, false)
//                        .setOngoing(false)
//                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
//                    notificationManager.notify(notificationId, builder.build())
//                }
//
//                Log.d("DBG", "Model downloaded to: ${targetFile.absolutePath}")
//            } catch (e: IOException) {
//                Log.d("DBG", "Error saving file: ${e.message}")
//                builder.setContentText("Download failed")
//                    .setProgress(0, 0, false)
//                    .setOngoing(false)
//                    .setSmallIcon(android.R.drawable.stat_notify_error)
//                notificationManager.notify(notificationId, builder.build())
//            }
//        }
//
//        private fun saveFileWithProgress(
//            inputStream: InputStream,
//            outputStream: FileOutputStream,
//            contentLength: Long,
//            onProgress: (progress: Int) -> Unit
//        ) {
//            val buffer = ByteArray(1024 * 1024)
//            var downloadedBytes = 0L
//            var bytesRead: Int
//            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                outputStream.write(buffer, 0, bytesRead)
//                downloadedBytes += bytesRead
//
//                val progress = ((downloadedBytes * 100) / contentLength).toInt()
//                onProgress(progress)
//            }
//            outputStream.flush()
//        }
    }
}