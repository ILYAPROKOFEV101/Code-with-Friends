package com.example.codewithfriends.chats


import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient


import okhttp3.*
import okio.ByteString



class WebSocketClient(private val roomId: String, private val username: String, private val url: String, private val id: String) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder()
            .url("wss://getpost-ilya1.up.railway.app/chat/$roomId?username=$username&avatarUrl=$url&uid=$id")
            .build()

        webSocket = client.newWebSocket(request, MyWebSocketListener())
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun disconnect() {
        webSocket?.close(1000, "Goodbye, WebSocket!")
    }

    private inner class MyWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            // WebSocket connection opened
            // You can handle any actions on connection open here
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            // Received a text message
            // You can handle the received message here
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            // Received a binary message
            // You can handle the received message here
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            // WebSocket is about to close
            // You can handle any actions before closing here
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            // WebSocket connection closed
            // You can handle any actions after closing here
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            // WebSocket connection failure
            // You can handle the failure here
        }
    }
}
