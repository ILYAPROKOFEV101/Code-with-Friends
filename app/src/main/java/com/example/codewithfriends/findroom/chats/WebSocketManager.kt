package com.example.codewithfriends.findroom.chats


import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener






open class PieSocketListener() : WebSocketListener() {

    private lateinit var webSocket: WebSocket
    private val userId = "jdjj"





    override fun onOpen(webSocket: WebSocket, response: Response) { // Вызывается при открытии соединения с WebSocket


        Log.e("burak", "baglandi")

    }


    fun sendMessage(webSocket: WebSocket, message: String) {
        Log.d("PieSocket", "Sending message: $message")
        webSocket.send(message)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {  // Вызывается при получении сообщения от сервера

        output("Received : $text") // Вывод полученного сообщения в лог
    }


    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {  // Вызывается при закрытии соединения с WebSocket

        webSocket.close(NORMAL_CLOSURE_STATUS, null)// Закрытие соединения с указанным кодом и причиной

        output("Closing : $code / $reason")  // Вывод в лог информации о закрытии соединения
    }


    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {// Вызывается при возникновении ошибки во время работы с WebSocket

        output("Error : " + t.message + "fsda")// Вывод информации об ошибке в лог
    }


    fun output(text: String?) { // Функция для вывода текста в лог с тегом "PieSocket"
        Log.d("PieSocket", text!!)
    }


    companion object { // Объект-компаньон для хранения констант

        private const val NORMAL_CLOSURE_STATUS = 1000 // Код статуса для нормального закрытия соединения
    }
}
