package com.example.codewithfriends.findroom.chats

import android.content.Intent
import android.net.Uri
import android.net.http.HttpResponseCache.install
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.reaction.logik.PreferenceHelper.getRoomId
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Popup
import androidx.lifecycle.MutableLiveData

import coil.compose.rememberAsyncImagePainter
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.codewithfriends.findroom.chats.ui.theme.TestActivity
import com.example.codewithfriends.presentation.profile.ID
import com.example.codewithfriends.presentation.profile.IMG
import com.example.codewithfriends.presentation.profile.UID
import com.example.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.example.codewithfriends.roomsetting.Roomsetting
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import com.google.protobuf.ByteString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.http.HttpMethod
import java.io.IOException
import java.util.concurrent.TimeUnit




class Chat : ComponentActivity() {
    private lateinit var webSocketClient: WebSocketClient



  //  val pieSocketListener = PieSocketListener()
    var show = mutableStateOf(false)
    var developers = mutableStateOf(false)

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }


    private var webSocket: WebSocket? = null // Добавьте переменную для отслеживания состояния соединения

    private val client = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS) // Устанавливаем интервал отправки пинг-запросов
        .build()

        ///  private lateinit var webSocket: WebSocket
    private var storedRoomId: String? = null // Объявляем на уровне класса
    private val messages = mutableStateOf(listOf<Message>()) // Хранение
   // private val messagesLiveData = MutableLiveData<List<Message>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Настройте WebSocket с помощью pieSocketListener


        storedRoomId = getRoomId(this)




            //  pieSocketListener.setupWebSocket(roomId, username, url, id)

        setContent {

            val name = UID(
                userData = googleAuthUiClient.getSignedInUser()
            )
            val img = IMG(
                userData = googleAuthUiClient.getSignedInUser()
            )
            val id = ID(
                userData = googleAuthUiClient.getSignedInUser()
            )



            if (storedRoomId != null) {

                Join(storedRoomId!!, "$id", "$name", "$img", show.value, "$name")


            }

            if (storedRoomId != null) {
            //    openWebSocket(storedRoomId!!, "$name", "$img","$id" )
            }
            Spacer(modifier = Modifier.height(100.dp))

            if (storedRoomId != null) {
                MessageList(messages.value, "$name", "$img", "$id")
            }

            Spacer(modifier = Modifier.height(20.dp))
            if (storedRoomId != null) {
                Creator()
            }
            if (storedRoomId != null) {
                    //setupWebSocket(storedRoomId!!, "$name", "$img", "$id")
            } else {
                // roomId не сохранен, обработайте этот случай по вашему усмотрению
            }







        }



    }

    private var isConnected = false



    private fun setupWebSocket(roomId: String, username: String, url: String, id: String) {
        if (!isConnected) {
            val request: Request = Request.Builder()
                .url("https://getpost-ilya1.up.railway.app/chat/$roomId?username=$username&avatarUrl=$url&uid=$id")
                .build()

            val listener = object : WebSocketListener() {

                override fun onMessage(webSocket: WebSocket, text: String) {

                    val newMessage = Message(sender = "", content = text)
                    messages.value = messages.value + newMessage // Add message to the list
                }

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    isConnected = true
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    isConnected = false
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    isConnected = false
                }

            }
            webSocket = client.newWebSocket(request, listener)
        }
    }




    fun splitMessageContent(content: String): Pair<String, String> {
        val pattern = "\\[(https?://[^\\]]+)\\]".toRegex()
        val matchResult = pattern.find(content)
        val beforeUrl = content.substring(0, matchResult?.range?.start ?: content.length)
        val afterUrl =
            content.substring(matchResult?.range?.endInclusive?.plus(1) ?: content.length)
        return Pair(beforeUrl, afterUrl)
    }

    fun extractUrlFromString(input: String): String? {
        val pattern = "\\[(https?://[^\\]]*)\\]".toRegex()
        val matchResult = pattern.find(input)
        return matchResult?.groups?.get(1)?.value
    }


    @Composable
    fun MessageList(messages: List<Message>?, username: String, url: String, id: String) {
        if (messages != null && messages.isNotEmpty()) {
            if (messages.isNotEmpty()) { // Проверяем, что список не пуст
                val currentUserUrl = url.take(60) // Получаем первые 30 символов URL
                val listState = rememberLazyListState()
                val lastVisibleItemIndex = messages.size - 1
                val coroutineScope = rememberCoroutineScope()
                val hasScrolled = rememberSaveable { mutableStateOf(false) }

                if (!hasScrolled.value || messages.last().sender == url) {
                    LaunchedEffect(messages) {
                        if (lastVisibleItemIndex >= 0) {
                            coroutineScope.launch {
                                // listState.animateScrollToItem(lastVisibleItemIndex)
                                listState.scrollToItem(messages.size - 1)
                                hasScrolled.value = true
                            }
                        }
                    }
                }


                LaunchedEffect(messages) {
                    if (lastVisibleItemIndex >= 0) {
                        coroutineScope.launch {
                            // listState.animateScrollToItem(lastVisibleItemIndex)
                            listState.scrollToItem(messages.size - 1)
                            hasScrolled.value = true
                        }
                    }
                }




                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 60.dp, top = 100.dp)
                        .wrapContentHeight(),
                    reverseLayout = false,
                    state = listState
                ) {


                    items(messages) { message ->
                        val isMyMessage = message.sender == url
                        val isMyUrlMessage = message.content.contains(currentUserUrl)




                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            contentAlignment = if (isMyMessage || isMyUrlMessage) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp),
                                horizontalArrangement = if (isMyMessage || isMyUrlMessage) Arrangement.End else Arrangement.Start
                            ) {
                                val imageModifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                val imageUrl = extractUrlFromString(message.content)
                                val (beforeUrl, afterUrl) = splitMessageContent(message.content)

                                if (!(isMyMessage || isMyUrlMessage)) {
                                    if (imageUrl != null) {
                                        val painter: Painter =
                                            rememberAsyncImagePainter(model = imageUrl)
                                        Image(
                                            painter = painter,
                                            contentDescription = null,
                                            modifier = imageModifier
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                    }
                                }
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .padding(2.dp),
                                    backgroundColor = if (isMyMessage || isMyUrlMessage) Color.Green else Color.Blue,
                                    elevation = 10.dp,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "${message.sender}$beforeUrl$afterUrl",
                                            textAlign = TextAlign.Start,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun Join(
        roomId: String,
        id: String,
        username: String,
        url: String,
        show: Boolean,
        name: String,
    ) {

        var showPopup by remember { mutableStateOf(false) }

        val density = LocalDensity.current.density
        val tooltipModifier = Modifier.padding(density.dp, 0.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.Blue)
        ) {
        if (show == false) {
                val coroutineScope = rememberCoroutineScope()
                Button(
                    onClick = {
                        coroutineScope.launch {

                            // Задержка перехода на новую страницу через 3 секунды
                            Handler(Looper.getMainLooper()).postDelayed({
                                getData(storedRoomId!!, "$id", "$name")
                                joininroom(roomId, id, username, url)
                            }, 500) // 3000 миллисекунд (3 секунды)

                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(Color.Green), modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .clip(RoundedCornerShape(20.dp))
                      )
                {
                    Text(text = "If you want make  this project, join in the room")
                }

        } else {
                Button(
                    onClick = {
                         val intent = Intent(this@Chat, TestActivity::class.java)
                         startActivity(intent)

                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(Color.Green)
                    ) {}
            }

        }
}




    fun joininroom(roomId: String, id: String, username: String, url: String){
        val baseUrl = "https://getpost-ilya1.up.railway.app/join"

        val uriBuilder = Uri.parse(baseUrl).buildUpon()
            .appendQueryParameter("roomId", roomId)
            .appendQueryParameter("user_id", id)
            .appendQueryParameter("username", username)
            .appendQueryParameter("image_url", url)
            .build()


        val url = uriBuilder.toString()

        val client = OkHttpClient()
        val mediaType = "application/x-www-form-urlencoded".toMediaType()

        val requestBody = "".toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    // Обработка успешного ответа сервера
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }
        })

    }




    private fun getData(roomId: String, id: String, username: String) {
        val url = "https://getpost-ilya1.up.railway.app/exists/$roomId/$id/$username"

        val request = StringRequest(
            com.android.volley.Request.Method.GET,
            url,
            { response ->
                Log.d("Mylog", "Result: $response")
                val trueOrFalse = response.toBoolean()
                show.value = trueOrFalse // Обновляем значение MutableState<Boolean>
            },
            { error ->
                Log.d("Mylog", "Error: $error")
            }
        )

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
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
                .fillMaxSize(), // Занимает всю доступную вертикальную высоту
            verticalArrangement = Arrangement.Bottom
        ) {


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
                            // Отправка сообщения через WebSocket
                                // webSocketClient.send(messageToSend)
// Добавляем сообщение в список сообщений


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




    /*override fun onDestroy() {
        super.onDestroy()

        // Закрываем WebSocket соединение при завершении активности
        pieSocketListener.onClosing()
    }*/



}






