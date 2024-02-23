package com.ilya.codewithfriends.Vois

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Base64
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread


class AudioSender(private val webSocketUrl: String) {

    private var webSocket: okhttp3.WebSocket? = null

    fun sendAudio(audioFile: File) {
        val client = OkHttpClient.Builder().build()
        val request = okhttp3.Request.Builder().url(webSocketUrl).build()

        client.newWebSocket(request, object : okhttp3.WebSocketListener() {
            override fun onOpen(webSocket: okhttp3.WebSocket, response: okhttp3.Response) {
                super.onOpen(webSocket, response)
                this@AudioSender.webSocket = webSocket

                GlobalScope.launch(Dispatchers.IO) {
                    val inputStream = FileInputStream(audioFile)
                    val buffer = ByteArray(audioFile.length().toInt())
                    inputStream.read(buffer)
                    inputStream.close()

                    // Send the content of the file via WebSocket
                    webSocket.send(okio.ByteString.of(*buffer))
                    // Send a message indicating the end of file transmission
                    webSocket.send("EndOfFile")
                }
            }

            override fun onFailure(webSocket: okhttp3.WebSocket, t: Throwable, response: okhttp3.Response?) {
                super.onFailure(webSocket, t, response)
                // Handle errors
            }
        })
    }

    fun stopSending() {
        webSocket?.close(1000, null)
    }
}