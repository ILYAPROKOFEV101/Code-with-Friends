package com.example.codewithfriends.findroom.chats


import android.util.Log
import com.example.codewithfriends.presentation.sign_in.UserData
import com.google.firebase.database.Exclude
import com.google.protobuf.ByteString
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.OkHttpClient





import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString.Companion.encodeUtf8
import java.util.concurrent.TimeUnit



/*
class WebSocketManager private constructor() {
    private var webSocket: WebSocket? = null
    private var isConnected = false
    val client = OkHttpClient()
    companion object {
        private var instance: WebSocketManager? = null

        fun getInstance(): WebSocketManager {
            if (instance == null) {
                instance = WebSocketManager()
            }
            return instance!!
        }
    }

    fun connectWebSocket(roomId: String, username: String, url: String, id: String) {
        if (!isConnected) {
            try {
                val request: Request = Request.Builder()
                    .url("https://getpost-ilya1.up.railway.app/chat/$roomId?username=$username&avatarUrl=$url&uid=$id")
                    .build()

                webSocket = client.newWebSocket(request, object : WebSocketListener() {
                    override fun onMessage(webSocket: WebSocket, text: String) {
                        // Обработка входящих сообщений
                    }

                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        isConnected = true
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        isConnected = false
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        isConnected = false
                        // Обработка ошибок WebSocket
                    }
                })
            } catch (e: Exception) {
                // Обработка ошибок при создании WebSocket
            }
        }
    }

    fun disconnectWebSocket() {
        webSocket?.close(1000, "User initiated disconnect")
        isConnected = false
    }

    fun sendMessage(message: String) {
        // Проверяем, что WebSocket подключен
        if (isConnected) {
            webSocket?.send(message)
        }
    }
}
*/
