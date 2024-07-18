package com.ilya.codewithfriends.chats

import LoadingComponent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
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

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.ilya.codewithfriends.R
import com.ilya.codewithfriends.MainViewModel
import com.ilya.codewithfriends.Viewphote.ViewPhoto
import com.ilya.codewithfriends.createamspeck.ui.theme.CodeWithFriendsTheme
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.reaction.logik.PreferenceHelper.loadMessagesFromMemory

import com.ilya.reaction.logik.PreferenceHelper.saveMessages
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.ilya.codewithfriends.APIclass.JoinDataManager
import com.ilya.codewithfriends.Startmenu.FragmentManagerProvider_manu
import com.ilya.codewithfriends.Startmenu.Main_menu
import com.ilya.codewithfriends.Startmenu.Room
import com.ilya.codewithfriends.Viewphote.isVideoUrl
import com.ilya.codewithfriends.chattest.ShowVideo

import com.ilya.reaction.logik.PreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope


import kotlinx.coroutines.launch
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID


class Chat : FragmentActivity(), FragmentManagerProvider_manu {

    private var messageIdCounter = 0


    override fun provideFragmentManager(): FragmentManager {
        return supportFragmentManager
    }

    var show = mutableStateOf(false)
    var kick = mutableStateOf(false)
    var photo by mutableStateOf("")

    var uploadProgress = mutableStateOf(0f)


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
            uri?.let {
                selectedImageUri = it
                val mimeType = contentResolver.getType(it)
                showToast("Выбран файл типа: $mimeType")
            }
        }

    private var pickMedia = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // Дальнейшая обработка URI...
        }
    }
    var playvideo = ""



    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var isConnected by mutableStateOf(false)
    private var proces by mutableStateOf(false)

    private var storedRoomId: String? = null // Объявляем на уровне класса




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
        val img =  PreferenceHelper.getimg(this)
        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )

          storedRoomId  = intent.getStringExtra("roomid")

        Log.d("roomids" , "$storedRoomId")



        val loadingComponent = LoadingComponent()
        loadingComponent.userexsist("$id", this)



        userexsist(storedRoomId!!, "$id")




        if (storedRoomId != null) {
            getData(storedRoomId!!, "$id", "$name")

        }


        setContent {

            val navController = rememberNavController()

            val viewModel = viewModel<MainViewModel>()
            val isLoading by viewModel.isLoading.collectAsState()
            val swipeRefresh = rememberSwipeRefreshState(isRefreshing = isLoading)

            Log.d("uploadProgress", "$uploadProgress")

            NavHost(
                navController = navController,
                startDestination = "Main_Menu"
            ) {
                composable("Main_Menu") {

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
                            upbar(storedRoomId!!, "$id", "$name", "$img",navController)

                        }

                    }

                    Spacer(modifier = Modifier.height(100.dp))
                    if (storedRoomId != null && show.value) {
                        MessageList(messages.value, "$name", "$img", "$id", navController)
                    }


                    Spacer(modifier = Modifier.height(20.dp))

                    if (storedRoomId != null) {
                        Creator(
                            onSendMessage = { message ->

                                sendMessage(message)

                            },
                           // selectedImageUri,
                            pickImage
                        )


                    }
                }
            }
                }
                composable("Room") {
                    Room()
                }
                composable("Video") {
                    ShowVideo(playvideo)
                }
            }
        }


    }

    private fun sendMessage(message: String) {
        // Проверяем, что WebSocket подключен
        if (webSocket != null) {

            val messageId = messageIdCounter++
            val messageWithId = "$message" // Добавляем ID к сообщению
            webSocket?.send(message)
            Log.d("WebSocketStatus", "WebSocket: $webSocket")
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
        val img =  PreferenceHelper.getimg(this)
        val ids = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )

        // Проверяем, активно ли уже WebSocket соединение
        if (!isConnected) {
            // Если не активно, то открываем новое соединение
            setupWebSocket(storedRoomId!!, "$name", "$img", "$ids", this)

        }
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
       fun MessageList(messages: List<Message>?, username: String, url: String, id: String,navController: NavController ) {
           Log.d("MessageList", "Number of messages: ${messages?.size}")

           if (messages != null && messages.isNotEmpty()) {
               if (messages.isNotEmpty()) { // Проверяем, что список не пуст
                   val listState = rememberLazyListState()
                   val coroutineScope = rememberCoroutineScope()
                   val hasScrolled = rememberSaveable { mutableStateOf(false) }
                   // Состояние для отслеживания времени последнего клика
                   val lastClickTime = remember { mutableStateOf(0L) }


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
                   LaunchedEffect(messages.size) {
                       listState.animateScrollToItem(messages.size - 1)
                   }

                   LazyColumn(
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(bottom = 60.dp, top = 50.dp)
                           //.background(Color(0x2FFFFFFF))
                           .fillMaxHeight(),
                       reverseLayout = false,
                       state = listState
                   ) {
                       items(messages) { message ->
                           val isMyMessage = message.uid == id
                           val paint = extractImageFromMessage(message.message)

                           val messageDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.time), ZoneId.systemDefault())

                           val showDayMarker = lastShownMonth == null || lastShownDayOfMonth == null ||
                                   messageDateTime.monthValue != lastShownMonth ||
                                   messageDateTime.dayOfMonth != lastShownDayOfMonth
                           // Проверка, совпадает ли месяц и день с момента последнего сообщения

                           // Обновление текущего месяца и дня
                           lastShownMonth = messageDateTime.monthValue
                           lastShownDayOfMonth = messageDateTime.dayOfMonth
                           val currentDate = LocalDate.now()
                           val isToday = messageDateTime.toLocalDate() == currentDate

                           val dayOfWeekText = when (messageDateTime.dayOfWeek) {
                               DayOfWeek.MONDAY -> getString(R.string.monday)
                               DayOfWeek.TUESDAY -> getString(R.string.tuesday)
                               DayOfWeek.WEDNESDAY -> getString(R.string.wednesday)
                               DayOfWeek.THURSDAY -> getString(R.string.thursday)
                               DayOfWeek.FRIDAY -> getString(R.string.friday)
                               DayOfWeek.SATURDAY -> getString(R.string.saturday)
                               DayOfWeek.SUNDAY -> getString(R.string.sunday)
                           }

                           val monthText = when (messageDateTime.month) {
                               Month.JANUARY -> getString(R.string.january)
                               Month.FEBRUARY -> getString(R.string.february)
                               Month.MARCH -> getString(R.string.march)
                               Month.APRIL -> getString(R.string.april)
                               Month.MAY -> getString(R.string.may)
                               Month.JUNE -> getString(R.string.june)
                               Month.JULY -> getString(R.string.july)
                               Month.AUGUST -> getString(R.string.august)
                               Month.SEPTEMBER -> getString(R.string.september)
                               Month.OCTOBER -> getString(R.string.october)
                               Month.NOVEMBER -> getString(R.string.november)
                               Month.DECEMBER -> getString(R.string.december)
                           }
                           val imageUrl = message.img

                           val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                           val formattedText = "${messageDateTime.dayOfMonth} ${monthText} ${dayOfWeekText} "

                           if (showDayMarker) {
                               // Отображение маркера дня
                               Box(
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .padding(8.dp),
                                   contentAlignment = Alignment.Center
                               ) {
                                   if (isToday) {
                                       // Вывод "сегодня" вместо даты
                                       Text(
                                           text = stringResource(id = R.string.today),
                                           fontWeight = FontWeight.Bold,
                                           color = Color(0xFF595D67),
                                       )
                                   } else {
                                       // Вывод даты в обычном формате
                                       Text(
                                           text = formattedText,
                                           fontWeight = FontWeight.Bold,
                                           color = Color(0xFF595D67),
                                       )
                                   }
                               }
                           }


                               Row(
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .padding(8.dp),
                                   horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
                               ) {
                                   val imageModifier = Modifier
                                       .size(40.dp)
                                       .clip(RoundedCornerShape(40.dp))


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
                                    if(removeImageLinkFromMessage(message.message) != ""){
                                   Card(
                                       modifier = Modifier
                                           .wrapContentWidth()
                                           .padding(
                                               end = if (isMyMessage) 0.dp else screenWidth * 0.2f,
                                               top = 2.dp,
                                               start = if (isMyMessage) screenWidth * 0.2f else 0.dp,
                                               bottom = 2.dp
                                           ),
                                       backgroundColor = if (isMyMessage) Color(0xFF315FF3) else Color(0xFFFFFFFF),
                                      // elevation = 10.dp,
                                       shape = RoundedCornerShape(12.dp)
                                   ) {
                                       Column(
                                           modifier = Modifier
                                               .padding(8.dp)
                                       )
                                       {
                                           Text(
                                               text = removeImageLinkFromMessage(message.message),
                                               textAlign = TextAlign.Start,
                                               fontSize = 18.sp,
                                               fontWeight = FontWeight.SemiBold,
                                               color = if (isMyMessage) Color(0xFFFFFFFF) else Color(
                                                   0xFF1B1B1B
                                               ),
                                               overflow = TextOverflow.Ellipsis
                                           )

                                           Box(
                                               modifier = Modifier
                                                   // .fillMaxWidth()
                                                   .wrapContentHeight(),
                                               contentAlignment = Alignment.CenterEnd
                                           )
                                           {
                                               val localDateTime =
                                                   messageDateTime.atZone(ZoneId.systemDefault())
                                                       .toLocalDateTime()

                                               Text(
                                                   text = localDateTime.format(
                                                       DateTimeFormatter.ofPattern(
                                                           "HH:mm"
                                                       )
                                                   ), // Извлекаем только время
                                                   fontSize = 12.sp,
                                                   color = if (isMyMessage) Color(0xFFFFFFFF) else Color(
                                                       0xFF1B1B1B
                                                   ),
                                               )
                                           }
                                       }
                                   }
                                   }

                           }

                           if (paint != null && paint.isNotEmpty()) {
                               Box(
                                   modifier = Modifier
                                       .fillMaxWidth()
                                       .height(if (isVideoUrl(paint)) 500.dp else 300.dp),
                               ) {
                                   Box(modifier = Modifier
                                       .fillMaxSize()
                                       .padding(8.dp),
                                       contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
                                   ) {
                                       if (isVideoUrl(paint)) {
                                           Column(modifier = Modifier.fillMaxSize()) {
                                               Box(
                                                   modifier = Modifier
                                                       .fillMaxWidth()
                                                       .height(500.dp)
                                                       .clickable(
                                                           onClick = {
                                                               val currentTime =
                                                                   System.currentTimeMillis()
                                                               if (currentTime - lastClickTime.value < 300) { // 300 мс для двойного клика
                                                                   playvideo = paint
                                                                   navController.navigate("Video")
                                                               }
                                                               // Обновляем время последнего клика
                                                               lastClickTime.value = currentTime
                                                           },
                                                           // Отключение волнового эффекта при клике
                                                           indication = null,
                                                           interactionSource = remember { MutableInteractionSource() }
                                                       )
                                               ) {
                                                   CustomVideoPlayer(paint)
                                               }
                                           }

                                       } else {
                                           Image(
                                               painter = rememberImagePainter(data = paint),
                                               contentDescription = null,
                                               modifier = Modifier
                                                   .fillMaxHeight()
                                                   .fillMaxWidth(0.8f)
                                                   .clip(RoundedCornerShape(20.dp))
                                                   .clickable {
                                                       openLargeImage("$paint")
                                                   }
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
                        val intent = Intent(this@Chat, Main_menu::class.java)
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



    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun Creator(
        onSendMessage: (String) -> Unit,
        pickImage: ActivityResultLauncher<String>
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        var textSize by remember { mutableStateOf(20.sp) }
        var text by remember { mutableStateOf("") }
        var submittedText by remember { mutableStateOf("") }
        var showimg by remember { mutableStateOf(false) }
        var main by remember { mutableStateOf(true)}
       // var selectedImageUri by remember { mutableStateOf<Uri?>(null) } // Локальное управляемое состояние


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
                    Column(modifier = Modifier.fillMaxSize()) {

                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberImagePainter(selectedImageUri),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(
                                        bottom = 15.dp,
                                        top = 15.dp,
                                        start = 70.dp,
                                        end = 70.dp
                                    )
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {},
                                contentScale = ContentScale.Crop
                            )

                        }

                    }
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 100.dp, end = 80.dp)
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
                            uploadProgress.value = 0f
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
                            //showimg = false  // Если необходимо управлять видимостью элемента, раскомментируйте
                            proces = true  // Показываем индикатор загрузки

                            selectedImageUri?.let { uri ->
                                if (storedRoomId != null) {
                                    uploadFileToFirebaseStorage()
                                    uploadProgress.value = 0f
                                } else {
                                    showToast("Room ID is not set")
                                    proces = false  // Останавливаем индикатор загрузки, если нет room ID
                                }
                            } ?: run {
                                showToast("No file selected")
                                proces = false  // Останавливаем индикатор загрузки, если файл не выбран
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
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .align(Alignment.CenterHorizontally)
                ) {

                    CircularProgressIndicatorSample()
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


                        pickMedia.launch(arrayOf("image/*", "video/*"))
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
                                            "$submittedText<image>$photo</image>" // Вызываем функцию для отправки сообщения

                                        } else {
                                            "$submittedText" // Вызываем функцию для отправки сообщения
                                        }
                                    )
                                    photo = ""
                                    selectedImageUri = null // Обнулить selectedImageUri после успешной загрузки
                                    showimg = false
                                    uploadProgress.value = 0f

                                }
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
    val joinDataManager = JoinDataManager()

    @Composable
    fun upbar(roomId: String, id: String, name: String, img: String,navController: NavController){
        val room = "Room"
            if (show.value) {
                    Button(
                        colors = ButtonDefaults.buttonColors(Color(0xB900CE0A)),
                        modifier = Modifier
                            .background(Color(0x2F3083FF))
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        onClick = {

                            val intent = Intent(this@Chat, Main_menu::class.java)
                            intent.putExtra(
                                "Room",
                                room
                            ) // Здесь вы добавляете данные в Intent
                            startActivity(intent)

                            PreferenceHelper.saveRoomId(this, roomId)

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
                                joinDataManager.pushData_join(roomId,id, name,""){ success ->

                                }

                                recreate()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.room), fontSize = 24.sp)
                        }
                }
            }


    @Composable
    fun CircularProgressIndicatorSample() {
        val progress = uploadProgress.value // Прямое использование uploadProgress как State

        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(progress = animatedProgress, color = Color(0xFF42FC3C), strokeWidth = 5.dp)
            Spacer(Modifier.requiredHeight(5.dp))
            Text("Загрузка: ${String.format("%.1f", progress * 100)}%", fontSize = 16.sp)
        }
    }
        private fun uploadFileToFirebaseStorage() {
            if (selectedImageUri == null) {
                showToast("Выберите файл перед загрузкой.")
                return
            }

            proces = true
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference

            val fileName = UUID.randomUUID().toString()
            val mimeType = contentResolver.getType(selectedImageUri!!) ?: "application/octet-stream"
            val fileRef = storageRef.child("$mimeType/$storedRoomId/$fileName")

            val metadata = StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()

            val uploadTask = fileRef.putFile(selectedImageUri!!, metadata)

            uploadTask.addOnProgressListener { taskSnapshot ->
                // Обновляем глобальную переменную прогресса
                uploadProgress.value = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat() / 100
                Log.d("UploadProgress", "Прогресс загрузки: ${uploadProgress.value}")
            }

            uploadTask.addOnSuccessListener { taskSnapshot ->
                // Успешно загружено
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    // Получаем ссылку на загруженное видео
                    photo = uri.toString()
                    proces = false
                    showToast("Файл успешно загружен: $photo")

                    Log.d("Uploudphoto", photo)
                    selectedImageUri = null // Сбрасываем выбранный Uri после загрузки
                }.addOnFailureListener { exception ->
                    // Ошибка при получении ссылки на загруженный файл
                    proces = false
                    showToast("Ошибка при получении ссылки на файл: ${exception.message}")
                }
            }.addOnFailureListener { exception ->
                // Ошибка загрузки файла
                proces = false
                showToast("Ошибка загрузки файла: ${exception.message}")
            }
        }




}


