package com.example.codewithfriends.findroom

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

/*
class WebSocketManager {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connectToWebSocket(url: String): Flow<String> {
        return flow {
            val request = Request.Builder().url(url).build()

            val listener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                    this@WebSocketManager.webSocket = webSocket
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    emit(text)
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    // Handle binary messages if needed
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                    // Handle connection failure
                }
            }

            webSocket = client.newWebSocket(request, listener)

            awaitClose {
                webSocket?.close(1000, null)
            }
        }.flowOn(Dispatchers.IO)
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }
}
*/

