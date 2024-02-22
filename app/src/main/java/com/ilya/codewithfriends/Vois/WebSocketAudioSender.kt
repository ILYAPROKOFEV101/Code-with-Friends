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

class WebSocketAudioSender {

    private var webSocket: WebSocket? = null
    private var isSending = false

    fun startSending(file: File, url: String) {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder().url(url).build()

        client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                this@WebSocketAudioSender.webSocket = webSocket
                isSending = true

                GlobalScope.launch(Dispatchers.IO) {
                    val bufferSize = 4096 // Размер буфера
                    val buffer = ByteArray(bufferSize)
                    val inputStream = FileInputStream(file)
                    try {
                        var bytesRead = 0 // Инициализация переменной bytesRead
                        while (isSending && inputStream.read(buffer).also { bytesRead = it } != -1) {
                            webSocket.send(buffer.toByteString(0, bytesRead))
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        // Закрытие WebSocket после отправки всех данных
                        webSocket.close(1000, "File sent")
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                // Обработка ошибки
                t.printStackTrace()
            }
        })
    }

    fun stopSending() {
        isSending = false
        webSocket?.cancel()
    }
}

class AudioSender(private val webSocketUrl: String) {

    private var webSocket: WebSocket? = null

    fun sendAudio(audioFile: File) {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder().url(webSocketUrl).build()

        client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                this@AudioSender.webSocket = webSocket

                GlobalScope.launch(Dispatchers.IO) {
                    val inputStream = FileInputStream(audioFile)
                    val buffer = ByteArray(audioFile.length().toInt())
                    inputStream.read(buffer)
                    inputStream.close()

                    // Отправляем содержимое файла через WebSocket
                    webSocket.send(ByteString.of(*buffer))
                    // Отправляем сообщение о завершении передачи данных
                    webSocket.send("EndOfFile")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                // Обработка ошибок
            }
        })
    }

    fun stopSending() {
        webSocket?.close(1000, null)
    }
}