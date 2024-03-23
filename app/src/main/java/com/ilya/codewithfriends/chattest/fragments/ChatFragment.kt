package com.ilya.codewithfriends.chattest.fragments


import android.content.Context
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.ilya.codewithfriends.R
import com.ilya.codewithfriends.chats.Message
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.reaction.logik.PreferenceHelper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.java_websocket.client.WebSocketClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID


class ChatFragment : Fragment() {

    private val client = OkHttpClient()
    // Глобальная переменная для хранения WebSocket
    private var webSocket: WebSocket? = null
    private var isConnected = false
    // Определите ваше состояние messages и его инициализацию
    private val messages = mutableStateOf<List<Message>>(emptyList())
    // Добавьте переменные для отслеживания последнего показанного месяца и дня
    var lastShownMonth: Int? = null
    var lastShownDayOfMonth: Int? = null



    private lateinit var webSocketClient: WebSocketClient
    var photo by mutableStateOf("")






    private var selectedImageUri: Uri? by mutableStateOf(null)
    private var pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedImageUri = it }
        }



    private var proces by mutableStateOf(false)

    private lateinit var storedRoomId: String

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = requireContext().applicationContext,
            oneTapClient = Identity.getSignInClient(requireContext().applicationContext)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
        val img =  PreferenceHelper.getimg(requireContext())
        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )
        storedRoomId = arguments?.getString("STORED_ROOM_ID_KEY") ?: ""

        // Вызываем setupWebSocket здесь, чтобы гарантировать, что WebSocket подключается до создания представления
        setupWebSocket("$storedRoomId", "$name", "$img", "$id", requireContext())


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //= arguments?.getString("STORED_ROOM_ID_KEY")
        Log.d("storedRoomId","$storedRoomId")
        // Создаем ComposeView и устанавливаем контент
        return ComposeView(requireContext()).apply {
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
                Box(modifier = Modifier.fillMaxSize()) {
                    // Отображаем список сообщений и изображение
                    MessageList(messages.value, "$img", "$id")
                    Spacer(modifier = Modifier.height(20.dp))
                    if (storedRoomId != null) {
                        // Создание экземпляра Creator
                        val creator = Creator(
                            onSendMessage = { message ->
                                // Здесь вы можете добавить логику для отправки сообщения через WebSocket

                                sendMessage(message )

                                // Используйте selectedImageUri и pickImage по вашему усмотрению
                            },
                            selectedImageUri = selectedImageUri,
                            pickImage = pickImage
                        )
                        // Используйте созданный экземпляр Creator
                    }

                }
            }
        }
    }

   /* private fun sendMessage(message: String, socketValue: String, nameValue: String,imgValue: String,idValue: String ) {

        val webSocketClient = com.ilya.codewithfriends.chats.WebSocketClient(
            "$socketValue",
            "$nameValue",
            "$imgValue",
            "$idValue"
        )
        webSocketClient.connect()

        webSocketClient.sendMessage("$message")



    }*/

    // Функция для отправки сообщения через WebSocket
    fun sendMessage(message: String) {
        // Проверяем, что WebSocket подключен
        if (webSocket != null) {
            webSocket?.send(message)
        } else {
            // WebSocket не подключен, выполните необходимые действия
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
        if (!isConnected) {
            val request: Request = Request.Builder()
                .url("https://getpost-ilya1.up.railway.app/local_chat/$roomId?username=$username&avatarUrl=$url&uid=$id&lasttime=0")
                .build()
            Log.d(
                "websoket",
                "https://getpost-ilya1.up.railway.app/chat/$roomId?username=$username&avatarUrl=$url&uid=$id&lasttime=10"
            )

            // Установка соединения с WebSocket
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
                        return
                    }
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isConnected) {
            webSocket?.close(1000, "User closed the connection")
            isConnected = false
        }
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



    private fun showToast(message: String, context: Context) {
        // Вывести Toast с заданным сообщением
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
                            } else {
                                context?.let { showToast("идёт загрузка фота.", it) }
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
    fun MessageList(messages: List<Message>?,url: String, id: String) {
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

    private fun uploadImageToFirebaseStorage(roomid: String) {



        if (selectedImageUri == null) {
            // Обработка случая, когда изображение не выбрано
            context?.let { showToast("Выберите изображение перед загрузкой.", it) }
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
                    context?.let { showToast(getString(R.string.addPhoto), it) }

                    // Обнулить selectedImageUri после успешной загрузки
                    selectedImageUri = null
                }
            } else {
                // Handle unsuccessful upload
                proces = false
                // Покажите Toast об ошибке загрузки
                context?.let { showToast(getString(R.string.upload_error_message), it) }
            }
        }
    }




}
