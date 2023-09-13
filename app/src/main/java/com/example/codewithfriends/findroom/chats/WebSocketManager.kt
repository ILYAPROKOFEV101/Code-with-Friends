package com.example.codewithfriends.findroom.chats


import android.util.Log
import com.example.codewithfriends.presentation.sign_in.UserData
import com.google.firebase.database.Exclude
import com.google.protobuf.ByteString
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener






import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString.Companion.encodeUtf8
import java.util.concurrent.TimeUnit



object WebSocketManager {
    private var webSocketClient: WebSocketClient? = null

    fun getClient(roomId: String, username: String, avatarUrl: String, uid: String, messageCallback: (String) -> Unit): WebSocketClient {
        if (webSocketClient == null) {
            webSocketClient = WebSocketClient(roomId, username, avatarUrl, uid, messageCallback)
        }
        return webSocketClient!!
    }
}


class WebSocketClient(
    private val roomId: String,
    private val username: String,
    private val avatarUrl: String,
    private val uid: String,
    private val messageCallback: (String) -> Unit // Колбэк для обработки полученных сообщений
) {
    private var webSocket: WebSocket? = null
    private var isConnected = false // Флаг состояния подключения

    fun connect() {
        if (isConnected) {
            return // Если уже подключены, то не делаем ничего
        }

        val request = Request.Builder()
            .url("https://getpost-ilya1.up.railway.app/chat/$roomId?username=$username&avatarUrl=$avatarUrl&uid=$uid")
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // WebSocket успешно подключен
                this@WebSocketClient.webSocket = webSocket
                isConnected = true // Устанавливаем флаг подключения
                // Отправить данные при подключении
                send("Привет, сервер!")

            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // Обработка полученного текстового сообщения
                // Вызываем колбэк для обработки сообщения
                messageCallback(text)
            }



            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                // Закрытие соединения
                webSocket.close(1000, "User disconnected")
                this@WebSocketClient.webSocket = null
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Обработка ошибок WebSocket-соединения
            }
        }

        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        webSocket = client.newWebSocket(request, listener)
    }

    fun send(message: String) {
        webSocket?.send(message)
    }

    val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()


    fun close() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }



}
