package com.example.taskbeat.data

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.io.File

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
            val fileName = "gemma2-2b-it-gpu-int8.bin"
            val targetDir = File(context.getExternalFilesDir(null), "model/llm")
            val targetFile = File(targetDir, "gemma2-2b-it-gpu-int8.bin")

            MODEL_PATH = "$targetDir/$fileName"

            if (!targetDir.exists()) {
                targetDir.mkdirs()
                return
            }

            if (targetFile.exists()) {
                Log.d("DBG", "File already exists")
                return
            }

            val url = "https://www.dropbox.com/scl/fi/7975qvuf609j9emivewgm/gemma2-2b-it-gpu-int8.bin?rlkey=oy5p0l3554c6nxs61tni75qk8&st=oc62cyfd&dl=1"
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(url))

            request.setDestinationUri(Uri.fromFile(targetFile))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle("Downloading model")
                .setDescription("Downloading model...")
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)

            withContext(Dispatchers.IO) {
                val downloadId = downloadManager.enqueue(request)
                monitorDownloadProgress(context, downloadManager, downloadId)
            }
        }

        private suspend fun monitorDownloadProgress(
            context: Context,
            downloadManager: DownloadManager,
            downloadId: Long
        ) {
            val query = DownloadManager.Query()
            query.setFilterById(downloadId)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(context, "download_channel")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Downloading model")
                .setProgress(100, 0, false)

            while (true) {
                val cursor = downloadManager.query(query)
                if (cursor != null && cursor.moveToFirst()) {
                    val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    var status = -1
                    if (statusIndex != 1) {
                        status = cursor.getInt(statusIndex)
                        Log.d("DBG", "Status $status")
                    }
                    val totalBytes = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    val downloadedBytes = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)

                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            builder.setContentText("Download complete")
                                .setProgress(0, 0, false)
                            notificationManager.notify(downloadId.toInt(), builder.build())
                            return
                        }
                        DownloadManager.STATUS_FAILED -> {
                            builder.setContentText("Download failed")
                                .setProgress(0, 0, false)
                            notificationManager.notify(downloadId.toInt(), builder.build())
                            return
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
}