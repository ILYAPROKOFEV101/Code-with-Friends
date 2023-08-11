package com.example.codewithfriends.findroom.chats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codewithfriends.R
import com.example.codewithfriends.findroom.chats.PieSocketListener
import com.example.reaction.logik.PreferenceHelper
import com.example.reaction.logik.PreferenceHelper.getRoomId
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontVariation.weight

import androidx.compose.ui.unit.dp


class Chat : ComponentActivity() {

    private val pieSocketListener = PieSocketListener()
    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket
    private var storedRoomId: String? = null // Объявляем на уровне класса
    private val messages = mutableStateOf(listOf<Message>()) // Хранение сообщений

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storedRoomId = getRoomId(this)


        setContent {
                Creator()

        }// Проверяем, что storedRoomId не равен null
        if (storedRoomId != null) {
            setupWebSocket(storedRoomId!!)
        } else {
            // roomId не сохранен, обработайте этот случай по вашему усмотрению
        }
    }

    private fun setupWebSocket(roomId: String) {
        val request: Request = Request.Builder()
            .url("https://getpost-ilya1.up.railway.app/chat/$roomId")
            .build()

        val listener = object : WebSocketListener() {
            // Переопределение методов WebSocketListener для обработки сообщений
            override fun onMessage(webSocket: WebSocket, text: String) {
                // Обработка полученного текстового сообщения
                val newMessage = Message(sender = "Sender Name", content = text)
                messages.value = messages.value + newMessage // Добавление сообщения в список
            }
            // ... другие методы WebSocketListener ...
        }
        webSocket = client.newWebSocket(request, listener)
    }


    @Composable
    fun MessageList(messages: List<Message>) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight() // Занимает высоту контента
            ) {
                items(messages) { message ->
                    Text("${message.sender}: ${message.content}")
                }
            }

    }



    @Preview(showBackground = true)
    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun Creator() {
        val roomId = intent.getStringExtra("roomId")
        val keyboardController = LocalSoftwareKeyboardController.current
        var textSize by remember { mutableStateOf(20.sp) }
        var text by remember { mutableStateOf("") }
        var submittedText by remember { mutableStateOf("") }




            Column(
                modifier = Modifier
                    .padding(bottom = 2.dp)
                    .fillMaxHeight(), // Занимает всю доступную вертикальную высоту
                verticalArrangement = Arrangement.Bottom
            ) {
                MessageList(messages.value)

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(start = 8.dp, end = 20.dp),
                            verticalAlignment = Alignment.Bottom // Прижимаем содержимое к верхней части
                ) {
                    TextField(
                        modifier = Modifier.weight(0.9f),
                        value = text,
                        onValueChange = { text = it },
                        textStyle = TextStyle(fontSize = textSize),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.White,
                            disabledIndicatorColor = Color.White,
                            containerColor = Color.White
                        ),

                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        maxLines = 10 // Устанавливаем максимальное количество строк, чтобы TextField мог увеличиваться по высоте
                    )


                    IconButton(
                        modifier = Modifier
                            .weight(0.1f)
                            .align(Alignment.CenterVertically), // Выравнивание по центру вертикально

                        onClick = {
                            submittedText = text
                            text = ""

                            val messageToSend = "$submittedText"
                            pieSocketListener.sendMessage(webSocket, messageToSend)
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.send),
                            contentDescription = "Send"
                        )
                    }
                }
            }
        }
    }






