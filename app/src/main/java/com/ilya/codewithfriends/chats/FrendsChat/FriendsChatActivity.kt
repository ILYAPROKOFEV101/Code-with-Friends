package com.ilya.codewithfriends.chats.FrendsChat


import LoadingComponent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.messaging.FirebaseMessaging
import com.ilya.codewithfriends.chats.FrendsChat.ui.theme.CodeWithFriendsTheme
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.reaction.logik.PreferenceHelper
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.auth.api.identity.Identity

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.ilya.codewithfriends.APIclass.JoinDataManager
import com.ilya.codewithfriends.MainViewModel
import com.ilya.codewithfriends.R
import com.ilya.codewithfriends.Startmenu.Main_menu
import com.ilya.codewithfriends.Startmenu.Room
import com.ilya.codewithfriends.Viewphote.ViewPhoto
import com.ilya.codewithfriends.chats.Message
import com.ilya.codewithfriends.findroom.FindRoom
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.java_websocket.client.WebSocketClient
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

class FriendsChatActivity : ComponentActivity() {

    private var storedRoomId: String? = null


    private var messageIdCounter = 0


    private lateinit var webSocketClient: WebSocketClient

    var show = mutableStateOf(false)
    var kick = mutableStateOf(false)
    var photo by mutableStateOf("")


    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private val messages = mutableStateOf<List<Message>>(emptyList())

    // Добавьте переменные для отслеживания последнего показанного месяца и дня
    var lastShownMonth: Int? = null
    var lastShownDayOfMonth: Int? = null

    // Определите ваше состояние messages и его инициализацию

    private var selectedImageUri: Uri? by mutableStateOf(null)
    private var pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedImageUri = it }
        }


    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var isConnected by mutableStateOf(false)
    private var proces by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Получаем Intent, который запустил эту активность
        val intent = intent

        // Проверяем, содержит ли Intent дополнительные данные с ключом "CHAT_ID"

            // Извлекаем данные по ключу "CHAT_ID" и присваиваем их переменной класса
            storedRoomId = intent.getStringExtra("CHAT_ID")
            Log.d("roomid", "$storedRoomId")

            // Теперь у вас есть чат ID, который вы передали из предыдущей активности
            // Вы можете использовать его в вашей активности

        // Получить контекст
        val context = this

        // Вызвать функцию для загрузки сообщений
        val loadedMessages = PreferenceHelper.loadMessagesFromMemory(context)

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




        val loadingComponent = LoadingComponent()
        loadingComponent.userexsist("$id", this)



        setContent {

            val navController = rememberNavController()

            val viewModel = viewModel<MainViewModel>()
            val isLoading by viewModel.isLoading.collectAsState()
            val swipeRefresh = rememberSwipeRefreshState(isRefreshing = isLoading)



            NavHost(
                navController = navController,
                startDestination = "Main_Menu"
            ) {
                composable("Main_Menu") {



                    com.ilya.codewithfriends.createamspeck.ui.theme.CodeWithFriendsTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {


                           

                            Spacer(modifier = Modifier.height(100.dp))
                            if (storedRoomId != null) {
                                MessageList(messages.value, "$img", "$id")
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
                composable("Room") {
                    Room()
                }
            }
        }


    }



    private fun restartActivity() {
        recreate()
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
        if (!isConnected) {
            val request: Request = Request.Builder()
                .url("wss://getpost-ilya1.up.railway.app/local_chat/$roomId?username=$username&avatarUrl=$url&uid=$id&lasttime=0")
                .build()
            Log.d(
                "WebSocket",
                "Connecting to WebSocket with URL: ${request.url}"
            )

            // Установка соединения с WebSocket
            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("WebSocket", "WebSocket connection opened")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d("WebSocket", "Received message: $text")
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

                            GlobalScope.launch(Dispatchers.Main) {
                                // Обновить UI после добавления новых сообщений
                                messages.value = newMessages
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

                            GlobalScope.launch(Dispatchers.Main) {
                                // Обновляем значение messages
                                messages.value = messages.value + newMessage
                            }
                        }
                    } catch (e: JSONException) {
                        // Если разбор JSON не удался, обработайте ошибку
                        Log.e("WebSocket", "Error parsing JSON message: $text", e)
                    }
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("WebSocket", "WebSocket connection closed. Code: $code, Reason: $reason")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("WebSocket", "WebSocket connection failure", t)
                }
            })
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
    fun MessageList(messages: List<Message>? ,url: String, id: String) {
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

                LaunchedEffect(messages.size) {
                    listState.animateScrollToItem(messages.size - 1)
                }

                var lastShownDate: LocalDate? by remember { mutableStateOf(null) }


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


                        val maxTextWidth = 0.8f // Максимальная ширина текста
                        val messageText = removeImageLinkFromMessage(message.message) // Текст сообщения

// Если ширина текста превышает максимальную ширину, обрезаем текст и добавляем многоточие
                        val displayedText = if (messageText.length > maxTextWidth * 1000) {
                            // Умножаем на 1000, чтобы преобразовать ширину в пиксели
                            val maxWidthIndex = (maxTextWidth * 1000).toInt()
                            messageText.substring(0, maxWidthIndex) + "..."
                        } else {
                            messageText
                        }


                        val messageDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(message.time), ZoneId.systemDefault())


                        // Проверка, совпадает ли месяц и день с момента последнего сообщения
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
                                        .wrapContentWidth()
                                        .padding(
                                            end = if (isMyMessage) 0.dp else screenWidth * 0.2f,
                                            top = 2.dp,
                                            start = if (isMyMessage) screenWidth * 0.2f else 0.dp,
                                            bottom = 2.dp
                                        ),
                                    backgroundColor = if (isMyMessage) Color(0xFF315FF3) else Color(0xFFFFFFFF),
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
                                            val localDateTime = messageDateTime.atZone(ZoneId.systemDefault()).toLocalDateTime()

                                            Text(
                                                text = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm")), // Извлекаем только время
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
                                                //openLargeImage("$paint")
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
