package com.example.codewithfriends.chats

import LoadingComponent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer


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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel

import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.codewithfriends.MainViewModel
import com.example.codewithfriends.Viewphote.ViewPhoto
import com.example.codewithfriends.createamspeck.ui.theme.CodeWithFriendsTheme
import com.example.codewithfriends.findroom.FindRoom
import com.example.codewithfriends.presentation.profile.ID
import com.example.codewithfriends.presentation.profile.IMG
import com.example.codewithfriends.presentation.profile.UID
import com.example.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.example.codewithfriends.roomsetting.Roomsetting
import com.example.reaction.logik.PreferenceHelper
import com.example.reaction.logik.PreferenceHelper.loadMessagesFromMemory

import com.example.reaction.logik.PreferenceHelper.saveMessages
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.auth.api.identity.Identity
import com.google.common.reflect.TypeToken
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope


import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.java_websocket.client.WebSocketClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID


class Chat : ComponentActivity() {

    private var messageIdCounter = 0


    private lateinit var webSocketClient: WebSocketClient
    var show = mutableStateOf(false)
    var kick = mutableStateOf(false)
    var photo by mutableStateOf("")
    private  val KEY_MESSAGE_LIST = "messageList"


    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    // Добавьте переменные для отслеживания последнего показанного месяца и дня
    var lastShownMonth: Int? = null
    var lastShownDayOfMonth: Int? = null

    // Определите ваше состояние messages и его инициализацию
    private val messages = mutableStateOf(mutableListOf<Message>())

    private var selectedImageUri: Uri? by mutableStateOf(null)
    private var pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedImageUri = it }
        }


    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var isConnected by mutableStateOf(false)
    private var proces by mutableStateOf(false)

    private var storedRoomId: String? = null // Объявляем на уровне класса

/*
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedImageUri ->
                // Здесь вы можете загрузить изображение в Firebase Storage
                uploadImageToFirebaseStorage(selectedImageUri, storedRoomId!!)
            }
        }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получить контекст
        val context = this

        // Вызвать функцию для загрузки сообщений
        val loadedMessages = loadMessagesFromMemory(context)

        // Преобразовать List в MutableList
        messages.value = loadedMessages.toMutableList()

        Log.e(" messages.value", "$messages")

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result
            Log.e("Tag", "Token -> $token")
        }




        val name = UID(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val img = IMG(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )

        storedRoomId = getRoomId(this)


        val loadingComponent = LoadingComponent()
        loadingComponent.userexsist("$id", this)



        userexsist(storedRoomId!!, "$id")




        if (storedRoomId != null) {
            getData(storedRoomId!!, "$id", "$name")

        }







        setContent {


            val viewModel = viewModel<MainViewModel>()
            val isLoading by viewModel.isLoading.collectAsState()
            val swipeRefresh = rememberSwipeRefreshState(isRefreshing = isLoading)



            CodeWithFriendsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    SwipeRefresh(
                        state = swipeRefresh,
                        onRefresh = {
                            recreate()
                        }
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)

                        ) {
                            upbar(storedRoomId!!, "$id", "$name", "$img",)

                        }

                    }

                    Spacer(modifier = Modifier.height(100.dp))
                    if (storedRoomId != null && show.value) {
                        MessageList(messages.value, "$name", "$img", "$id")

                    }


                    Spacer(modifier = Modifier.height(20.dp))

                    if (storedRoomId != null) {
                        Creator(
                            onSendMessage = { message ->
                                // Здесь вы можете добавить логику для отправки сообщения через WebSocket
                                sendMessage(message)

                                // Используйте selectedImageUri и pickImage по вашему усмотрению
                            },
                            selectedImageUri = selectedImageUri,
                            pickImage = pickImage
                        )


                    }
                }
            }
        }


    }

    // Функция для загрузки сообщений из памяти и обновления переменной messages


    private fun restartActivity() {
        recreate()
    }

    private fun sendMessage(message: String) {
        // Проверяем, что WebSocket подключен
        if (webSocket != null) {

            val messageId = messageIdCounter++
            val messageWithId = "$message" // Добавляем ID к сообщению
            webSocket?.send(messageWithId)
        } else {
            // WebSocket не подключен, возможно, нужно выполнить повторное подключение
            // setupWebSocket(...)
        }
    }


    override fun onResume() {
        super.onResume()

        val name = UID(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val img = IMG(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val ids = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )


        // Автоматическое подключение при входе в активность
        setupWebSocket(storedRoomId!!, "$name", "$img", "$ids", this)
        println("подключение ")
    }


    private fun setupWebSocket(
        roomId: String,
        username: String,
        url: String,
        id: String,
        context: Context
    ) {
        val numberOfMessages = messages.value.size
        Log.d(
            "numberOfMessages",
            "$numberOfMessages"
        )

        if (!isConnected) {
            try {
                val request: Request = Request.Builder()
                    .url("https://getpost-ilya1.up.railway.app/chat/$roomId?username=$username&avatarUrl=$url&uid=$id&lasttime=$numberOfMessages")
                    .build()

                Log.d(
                    "websoket",
                    "https://getpost-ilya1.up.railway.app/chat/$roomId?username=$username&avatarUrl=$url&uid=$id&lasttime=10"
                )

                webSocket = client.newWebSocket(request, object : WebSocketListener() {

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        try {
                            if (text.startsWith("[")) {
                                // Если пришел массив сообщений
                                val jsonArray = JSONArray(text)

                                val newMessages = mutableListOf<Message>()

                                for (i in 0 until jsonArray.length()) {
                                    val json = jsonArray.getJSONObject(i)
                                    val newMessage = Message(
                                        img = json.getString("img"),
                                        uid = json.getString("uid"),
                                        name = json.getString("name"),
                                        message = json.getString("message"),
                                        time = json.getLong("time")
                                    )
                                    newMessages.add(newMessage)
                                }

                                // Создать новый MutableList, добавив в него все старые сообщения и новые
                                val updatedMessages = mutableListOf<Message>()
                                updatedMessages.addAll(messages.value)
                                updatedMessages.addAll(newMessages)

                                GlobalScope.launch(Dispatchers.Main) {
                                    // Обновить состояние messages новым MutableList
                                    messages.value = updatedMessages

                                    // Сохранить обновленные сообщения в память устройства
                                    saveMessages(context, updatedMessages)
                                }

                            } else {
                                // Если пришло отдельное сообщение
                                val json = JSONObject(text)
                                val newMessage = Message(
                                    img = json.getString("img"),
                                    uid = json.getString("uid"),
                                    name = json.getString("name"),
                                    message = json.getString("message"),
                                    time = json.getLong("time")
                                )

                                // Создать новый MutableList, добавив в него все старые сообщения и новое
                                val updatedMessages: MutableList<Message> = messages.value.toMutableList()
                                updatedMessages.add(newMessage)

                                GlobalScope.launch(Dispatchers.Main) {
                                    // Обновить состояние messages новым MutableList
                                    messages.value = updatedMessages

                                    // Сохранить обновленные сообщения в память устройства
                                    saveMessages(context, updatedMessages)
                                }
                            }
                        } catch (e: JSONException) {
                            // Если разбор JSON не удался, обработайте ошибку
                            return
                        }
                    }

                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        isConnected = true
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        isConnected = false
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        isConnected = false

                        // Добавьте лог для отслеживания ошибок WebSocket
                        Log.e("WebSocket", "WebSocket failure: ${t.message}")
                    }
                })
            } catch (e: Exception) {
                // Обработка ошибок при создании WebSocket
                // Можно добавить логирование или другие действия по обработке ошибок
                showToast("Ошибка при создании WebSocket: ${e.message}")
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        // Проверяем, если WebSocket соединение активно, то закрываем его
        if (isConnected) {
            webSocket?.close(1000, "User closed the connection")
            isConnected = false
        }
    }



    private fun showToast(message: String) {
        // Вывести Toast с заданным сообщением
        Toast.makeText(this@Chat, message, Toast.LENGTH_SHORT).show()
    }



    fun extractImageFromMessage(input: String): String? {
        val pattern = "<image>(.+?)</image>".toRegex()
        val matchResult = pattern.find(input)
        return matchResult?.groups?.get(1)?.value
    }



    fun removeImageLinkFromMessage(message: String): String {
        val imageUrl = extractImageFromMessage(message)
        return if (imageUrl != null) {
            // Если ссылка на изображение найдена, заменяем ее на пустую строку
            message.replace("<image>$imageUrl</image>", "")
        } else {
            // Если ссылка на изображение не найдена, возвращаем исходное сообщение
            message
        }
    }



      @Composable
       fun MessageList(messages: List<Message>?, username: String, url: String, id: String) {
           Log.d("MessageList", "Number of messages: ${messages?.size}")

           if (messages != null && messages.isNotEmpty()) {
               if (messages.isNotEmpty()) { // Проверяем, что список не пуст
                   val currentUserUrl = url.take(60) // Получаем первые 30 символов URL
                   val listState = rememberLazyListState()
                   val lastVisibleItemIndex = messages.size - 1
                   val coroutineScope = rememberCoroutineScope()
                   val hasScrolled = rememberSaveable { mutableStateOf(false) }


                   LaunchedEffect(hasScrolled.value, messages) {
                       if (!hasScrolled.value || messages.last().img == url) {
                           val lastVisibleItemIndex = messages.size - 1
                           if (lastVisibleItemIndex >= 0) {
                               coroutineScope.launch {
                                   // listState.animateScrollToItem(lastVisibleItemIndex)
                                   listState.scrollToItem(lastVisibleItemIndex)
                                   hasScrolled.value = true
                               }
                           }
                       }
                   }


                   var lastShownDate: LocalDate? by remember { mutableStateOf(null) }


                   LazyColumn(
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(bottom = 60.dp, top = 50.dp)
                           .background(Color(0x2F3083FF))
                           .wrapContentHeight(),
                       reverseLayout = false,
                       state = listState
                   ) {
                       items(messages) { message ->
                           val isMyMessage = message.uid == id
                           val paint = extractImageFromMessage(message.message)




                           val messageDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.time), ZoneId.systemDefault())


                           // Проверка, совпадает ли месяц и день с момента последнего сообщения
                           val showDayMarker = lastShownMonth == null || lastShownDayOfMonth == null ||
                                   messageDateTime.monthValue != lastShownMonth ||
                                   messageDateTime.dayOfMonth != lastShownDayOfMonth
                           // Проверка, совпадает ли месяц и день с момента последнего сообщения

                           // Обновление текущего месяца и дня
                           lastShownMonth = messageDateTime.monthValue
                           lastShownDayOfMonth = messageDateTime.dayOfMonth



                           if (showDayMarker) {
                               // Отображение маркера дня
                               Box(
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .padding(8.dp),
                                   contentAlignment = Alignment.Center
                               ) {
                                   Text(
                                       text = messageDateTime.format(DateTimeFormatter.ofPattern("MM-dd")),
                                       fontWeight = FontWeight.Bold,
                                       color = Color.White
                                   )
                               }
                           }



                           Box(
                               modifier = Modifier
                                   .fillMaxWidth()
                                   .padding(8.dp),
                              contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
                               )
                           {
                               Row(
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .padding(0.dp),
                                   horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
                               ) {
                                   val imageModifier = Modifier
                                       .size(40.dp)
                                       .clip(RoundedCornerShape(40.dp))
                                   val imageUrl = message.img
                                   
                                   if (!(isMyMessage)) {
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
                                       backgroundColor = if (isMyMessage) Color(
                                           0xE650B973
                                       ) else Color(0xFFFFFFFF),
                                       elevation = 10.dp,
                                       shape = RoundedCornerShape(12.dp)
                                   ) {
                                       Column(modifier = Modifier
                                           .padding(8.dp)
                                         //  .background ( if (isMyMessage) Color(0xE650B973) else Color(0xFFFFFFFF))
                                       )
                                       {

                                           Text(
                                               text = removeImageLinkFromMessage(message.message),
                                               textAlign = TextAlign.Start,
                                               fontSize = 18.sp,
                                               fontWeight = FontWeight.SemiBold,
                                               color = Color.Black,
                                               overflow = TextOverflow.Ellipsis
                                           )




                                           Box(
                                               modifier = Modifier
                                                   .fillMaxWidth()
                                                   .wrapContentHeight(),
                                               contentAlignment = Alignment.CenterEnd
                                           )
                                           {
                                               val localDateTime = messageDateTime.atZone(ZoneId.systemDefault()).toLocalDateTime()

                                               Text(
                                                   text = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm")), // Извлекаем только время
                                                   fontSize = 10.sp,
                                                   color = Color.Black,
                                               )
                                           }


                                       }
                                   }
                               }
                           }

                           if (paint != null)

                           {

                                       if (paint.isNotEmpty()) {
                                           Box(
                                               modifier = Modifier
                                                   .fillMaxWidth()
                                                   .padding(start = 40.dp, end = 40.dp)
                                                   // .padding(start = 50.dp, end = 50.dp)
                                                   .height(300.dp),
                                                   //.zIndex(1f), // Устанавливает z-индекс, чтобы поместиться наверху других
                                               contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
                                           ) {
                                               Image(
                                                   painter = if (paint.isNotEmpty()) {
                                                       // Load image from URL
                                                       rememberImagePainter(data = paint)
                                                   } else {
                                                       // Load a default image when URL is empty
                                                       painterResource(id = R.drawable.android) // Replace with your default image resource
                                                   },
                                                   contentDescription = null,
                                                   modifier = Modifier
                                                       .fillMaxSize()
                                                       .clip(RoundedCornerShape(20.dp))
                                                       .clickable {
                                                           openLargeImage("$paint")
                                                       },
                                        )
                                }
                              }
                           }
                       }
                   }
               }
    }
  }
    fun openLargeImage( photo: String) {
        // Здесь реализуйте логику открытия большой версии изображения
        // Например, использование Intent для открытия новой активности с большим изображением
        val intent = Intent(this, ViewPhoto::class.java)
        intent.putExtra("PHOTO_URL", photo) // Передача URL изображения в активность
        startActivity(intent)

    }





    private fun getData(roomId: String, id: String, username: String) {
        // Создаем клиент OkHttp
        val client = OkHttpClient()

        // Создаем запрос
        val request = Request.Builder()
            .url("https://getpost-ilya1.up.railway.app/exists/$roomId/$id/$username")
            .build()

        // Выполняем запрос
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                // Ошибка
                Log.e("getData", e.message ?: "Неизвестная ошибка")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    // Получаем данные
                    val data = response.body!!.string()
                    val trueOrFalse = data.toBoolean()

                    // Обновляем значение MutableState<Boolean>
                    show.value = trueOrFalse
                } else {
                    // Ошибка
                    Log.e("getData", "Ошибка получения данных: ${response.code}")
                }
            }
        })
    }
    private fun userexsist(roomId: String, uid: String) {
        // Создаем клиент OkHttp
        val client = OkHttpClient()

        // Создаем запрос
        val request = Request.Builder()
            .url("https://getpost-ilya1.up.railway.app/kickuser/$roomId/$uid")
            .build()

        // Выполняем запрос
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                // Ошибка
                Log.e("getData", e.message ?: "Неизвестная ошибка")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    // Получаем данные
                    val data = response.body!!.string()
                    val trueOrFalse = data.toBoolean()


                    // Обновляем значение MutableState<Boolean>
                    kick.value = trueOrFalse

                    if(kick.value == true){
                        val intent = Intent(this@Chat, FindRoom::class.java)
                        startActivity(intent)
                        finish() // Завершаем текущую активность
                    }

                } else {
                    // Ошибка
                    Log.e("getData", "Ошибка получения данных: ${response.code}")
                }
            }
        })
    }




    private fun pushData(
        roomId: String, user_id: String, username: String, image_url: String
    ) {
        val baseUrl = "https://getpost-ilya1.up.railway.app/join"
        val uriBuilder = Uri.parse(baseUrl).buildUpon()
            .appendQueryParameter("roomId", roomId)
            .appendQueryParameter("user_id", user_id)
            .appendQueryParameter("username", username)
            .appendQueryParameter("image_url", image_url)
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






    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun Creator(
        onSendMessage: (String) -> Unit ,
        selectedImageUri: Uri?,
        pickImage: ActivityResultLauncher<String>
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        var textSize by remember { mutableStateOf(20.sp) }
        var text by remember { mutableStateOf("") }
        var submittedText by remember { mutableStateOf("") }
        var showimg by remember { mutableStateOf(false) }
        var main by remember { mutableStateOf(true) }


        Column(
            modifier = Modifier
                .fillMaxSize(), // Занимает всю доступную вертикальную высоту
            verticalArrangement = Arrangement.Bottom
        ) {
            if(showimg == true) {
                Box(modifier = Modifier
                    .width(400.dp)
                    .height(400.dp)
                    .zIndex(1f), // Устанавливает z-индекс, чтобы поместиться наверху других элементов
                    contentAlignment = Alignment.BottomCenter // Выравнивание по нижнему кра
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberImagePainter(selectedImageUri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(bottom = 15.dp, top = 15.dp, start = 70.dp, end = 70.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {},
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 100.dp , end = 100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .height(50.dp)
                    .alpha(0.9f) // Пример половинной прозрачности
                    .background(Color(0xFF4795CA)),
                    horizontalArrangement = Arrangement.Center,
                        //verticalArrangement = Arrangement.Center
                ){
                    IconButton(
                        modifier = Modifier
                            .size(100.dp), // Выравнивание по центру вертикально
                        onClick = {
                            showimg = false
                            photo = ""
                        }
                    ){
                        Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Cancel"
                    )
                    }
                    IconButton(
                        modifier = Modifier
                            .size(100.dp), // Выравнивание по центру вертикально
                        onClick = {
                            //showimg = false
                            proces = true
                            selectedImageUri?.let {
                                uploadImageToFirebaseStorage(storedRoomId!!)
                            }
                        }
                    ){
                        Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Check",
                            tint = Color.Black
                    )

                    }
                }
            }



            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 8.dp, end = 20.dp),
                verticalAlignment = Alignment.Bottom // Прижимаем содержимое к верхней части
            ) {

                IconButton(
                    modifier = Modifier
                        .weight(0.1f)
                        .align(Alignment.CenterVertically), // Выравнивание по центру вертикально
                    onClick = {

                            showimg = !showimg


                        pickImage.launch("image/*")

                    }
                ) {
                    if (showimg == false) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Send"

                        )
                        showimg = false
                    }
                }
                TextField(
                    modifier = Modifier
                        .weight(0.8f)
                        .clip(RoundedCornerShape(20.dp)),
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

                        if (show.value) {

                            submittedText = text

                            if(proces == false) { // глобальная для тогочтобы отслеживать фото щас грузится
                                 text = "" //обнуление текста
                                if (submittedText != "" || photo != "") {
                                    onSendMessage(
                                        if (photo != "") {
                                            "$submittedText photo: <image>$photo</image>" // Вызываем функцию для отправки сообщения
                                        } else {
                                            "$submittedText" // Вызываем функцию для отправки сообщения
                                        }
                                    )
                                    // Обнулить selectedImageUri после успешной загрузки

                                }
                                 showimg = false
                            } else {
                                showToast("идёт загрузка фота.")
                            }

                        }







                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.send),
                        contentDescription = "Send",
                    )
                }
            }
        }
    }









    @Composable
    fun upbar(roomId: String, id: String, name: String, img: String){

                if (show.value) {
                    Button(
                        colors = ButtonDefaults.buttonColors(Color(0xB900CE0A)),
                        modifier = Modifier
                            .background(Color(0x2F3083FF))
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        onClick = {
                            val intent = Intent(this@Chat, Roomsetting::class.java)
                            startActivity(intent)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.outroom), fontSize = 24.sp)
                      }


                } else {
                        Button(
                            colors = ButtonDefaults.buttonColors(Color(0xFF1472FF)),
                            modifier = Modifier
                                .height(100.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            onClick = {
                                pushData(roomId,"$id", "$name","$img",)

                                restartActivity()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.room), fontSize = 24.sp)
                        }

                }
            }

    private fun uploadImageToFirebaseStorage(roomid: String) {



        if (selectedImageUri == null) {
            // Обработка случая, когда изображение не выбрано
            showToast("Выберите изображение перед загрузкой.")
            return
        }

        proces = true

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        // Create a unique name for the image to avoid overwriting
        val imageName = UUID.randomUUID().toString()

        // Path to store the image: "images/{roomid}/{imageName}"
        val imageRef = storageRef.child("images/$roomid/$imageName")

        val metadata = StorageMetadata.Builder()
            .setContentType("image/png") // Указываем тип контента как PNG
            .build()

        val uploadTask = imageRef.putFile(selectedImageUri!!, metadata)

        // Add a listener to handle successful or unsuccessful upload
        uploadTask.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                // Get the download URL from the task result
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    photo = uri.toString()
                    // Do something with the URL, such as save it to Firestore
                    proces = false
                    // Покажите Toast об успешной загрузке
                    showToast(getString(R.string.addPhoto))

                    // Обнулить selectedImageUri после успешной загрузки
                    selectedImageUri = null
                }
            } else {
                // Handle unsuccessful upload
                proces = false
                // Покажите Toast об ошибке загрузки
                showToast(getString(R.string.upload_error_message))
            }
        }
    }



}
