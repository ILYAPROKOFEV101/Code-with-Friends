package com.example.codewithfriends.findroom.chats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.codewithfriends.datas.User
import com.example.codewithfriends.presentation.profile.IMG
import com.example.codewithfriends.presentation.profile.ProfileName
import com.example.codewithfriends.presentation.profile.UID
import com.example.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.example.codewithfriends.presentation.sign_in.UserData
import com.google.android.gms.auth.api.identity.Identity


class Chat : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private val pieSocketListener = PieSocketListener()
    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket
    private var storedRoomId: String? = null // Объявляем на уровне класса
    private val messages = mutableStateOf(listOf<Message>()) // Хранение сообщений

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storedRoomId = getRoomId(this)



        setContent {
            val name = UID(
                userData = googleAuthUiClient.getSignedInUser()
            )
            val img = IMG(
                userData = googleAuthUiClient.getSignedInUser()
            )

                Creator()
            if (storedRoomId != null) {
                setupWebSocket(storedRoomId!!, "$name", "$img")
            } else {
                // roomId не сохранен, обработайте этот случай по вашему усмотрению
            }
        }// Проверяем, что storedRoomId не равен null


    }


    private fun setupWebSocket(roomId: String, username: String, url: String) {
        val request: Request = Request.Builder()
            .url("https://getpost-ilya1.up.railway.app/chat/$roomId?username=$username&avatarUrl=$url")
            .build()

        val listener = object : WebSocketListener() {
            // Переопределение методов WebSocketListener для обработки сообщений
            override fun onMessage(webSocket: WebSocket, text: String) {
                // Обработка полученного текстового сообщения
                val newMessage = Message(sender = "", content = text)
                messages.value = messages.value + newMessage // Добавление сообщения в список
            }
            // ... другие методы WebSocketListener ...
        }
        webSocket = client.newWebSocket(request, listener)
    }

    fun removeBrackets(input: String): String {
        return input.replace(Regex("\\[|\\]"), "")
    }

    fun splitMessageContent(content: String): Pair<String, String> {
        val pattern = "\\[(https?://[^\\]]+)\\]".toRegex()
        val matchResult = pattern.find(content)
        val beforeUrl = content.substring(0, matchResult?.range?.start ?: content.length)
        val afterUrl = content.substring(matchResult?.range?.endInclusive?.plus(1) ?: content.length)
        return Pair(beforeUrl, afterUrl)
    }


    fun extractUrlFromString(input: String): String? {
        val pattern = "\\[(https?://[^\\]]+)\\]".toRegex()
        val matchResult = pattern.find(input)
        return matchResult?.groups?.get(1)?.value
    }

    @Composable
    fun MessageList(messages: List<Message>) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            reverseLayout = false
        ) {
            items(messages) { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeightIn(min = 50.dp) // Установите минимальную высоту
                        .padding(8.dp), // Добавьте отступы
                    backgroundColor = Color.Blue, // Задайте цвет фона
                    elevation = 4.dp, // Задайте высоту поднятия (тени)
                    shape = RoundedCornerShape(8.dp) // Задайте скругление углов
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeightIn(min = 50.dp) // Установите минимальную высоту
                            .padding(8.dp), // Добавьте отступы
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val imageModifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))

                        val imageUrl = extractUrlFromString(message.content)
                        val (beforeUrl, afterUrl) = splitMessageContent(message.content)

                        if (imageUrl != null) {
                            // Вывод изображения слева от сообщения, если есть imageUrl
                            val painter: Painter = rememberAsyncImagePainter(model = imageUrl)
                            Image(
                                painter = painter,
                                contentDescription = null,
                                modifier = imageModifier
                            )

                            Spacer(modifier = Modifier.width(8.dp)) // Добавим промежуток между изображением и текстом
                        }

                        val cleanBeforeUrl = removeBrackets(beforeUrl)
                        val cleanAfterUrl = removeBrackets(afterUrl)
                        Text("${message.sender ?: ""}: $cleanBeforeUrl$cleanAfterUrl",
                            textAlign = TextAlign.Start,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White)
                    }
                }
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






