package com.example.codewithfriends.roomsetting

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.codewithfriends.Aboutusers.Aboutuser
import com.example.codewithfriends.R
import com.example.codewithfriends.Startmenu.Main_menu
import com.example.codewithfriends.createamspeck.TeamSpeak
import com.example.codewithfriends.findroom.Room
import com.example.codewithfriends.firebase.Addtask
import com.example.codewithfriends.presentation.profile.ID
import com.example.codewithfriends.presentation.profile.IMG
import com.example.codewithfriends.presentation.profile.UID
import com.example.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.example.codewithfriends.roomsetting.ui.theme.Participant
import com.example.reaction.logik.PreferenceHelper.getRoomId
import com.google.android.gms.auth.api.identity.Identity
import com.google.common.reflect.TypeToken
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Shape
import androidx.core.graphics.rotationMatrix
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codewithfriends.MainViewModel
import com.example.codewithfriends.firebase.TaskRequest
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext


class Roomsetting : ComponentActivity() {


    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private var storedRoomId: String? = null // Объявляем на уровне класса
     var task = mutableStateOf(listOf<TaskData>())
    var tokens by mutableStateOf("")

    private fun restartActivity() {
        recreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        FirebaseMessaging.getInstance().token.addOnCompleteListener{ task ->
            if (!task.isSuccessful){
                return@addOnCompleteListener
            }

            val token = task.result
            Log.e("Tag" , "Token -> $token")
            tokens = token

        }





        val intent = intent
        val roomUrl = intent.getStringExtra("url")
        val Admin = intent.getStringExtra("Admin")

        storedRoomId = getRoomId(this)

        getTasks(storedRoomId!!)

        setContent {
            val viewModel = viewModel<MainViewModel>()
            val isLoading by viewModel.isLoading.collectAsState()
            val swipeRefresh = rememberSwipeRefreshState(isRefreshing = isLoading)



            val rooms = remember { mutableStateOf(emptyList<Room>()) }

            val participants = remember { mutableStateOf(emptyList<Participant>()) }


            // Проверяем, есть ли данные комнаты
            val firstRoom = rooms.value.firstOrNull()

            val name = UID(
                userData = googleAuthUiClient.getSignedInUser()
            )
            val img = IMG(
                userData = googleAuthUiClient.getSignedInUser()
            )
            val id = ID(
                userData = googleAuthUiClient.getSignedInUser()
            )

            if (id != null && tokens != null) {
                sendPostRequest(storedRoomId!!, "$id")
            }


            Handler(Looper.getMainLooper()).postDelayed({
                getData(storedRoomId!!, rooms)
                whoinroom(storedRoomId!!, participants)


            }, 500) // 3000 миллисекунд (3 секунды)

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

                SwipeRefresh(
                    state = swipeRefresh,
                    onRefresh = viewModel::LoadStuff
                ) {
                    LazyColumn {
                        item {
                            icon("$roomUrl")
                            Spacer(modifier = Modifier.height(30.dp))
                        }

                        // Вызываем roomname() только если есть данные комнаты

                        item {
                            firstRoom?.let {
                                roomname(
                                    roomName = it.roomName,
                                    "$id",
                                    storedRoomId!!
                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }


                        item {
                            userinroom(
                                participants.value,
                                "$Admin",
                                "$id"
                            ) // Передаем participants.value
                            Spacer(modifier = Modifier.height(30.dp))
                        }


                        item {
                            TaskList(task.value, storedRoomId!!)
                            // Передаем Task.value в Composable функцию, или пустой список, если Task.value == null
                            Spacer(modifier = Modifier.height(30.dp))

                        }
                        item {
                            addtask()
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        item {
                            Teamspeack()
                            Spacer(modifier = Modifier.height(30.dp))
                        }

                    }
                }
            }
        }
    }


    @Composable
    fun icon(roomUrl: String) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .padding(bottom = 15.dp, top = 15.dp, start = 70.dp, end = 70.dp)
                .clip(RoundedCornerShape(180.dp)),
            shape = RoundedCornerShape(180.dp), // Применяем закругленные углы к Card
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
        ) {

            Image(
                painter = if (roomUrl.isNotEmpty()) {
                    // Load image from URL
                    rememberImagePainter(data = roomUrl)
                } else {
                    // Load a default image when URL is empty
                    painterResource(id = R.drawable.android) // Replace with your default image resource
                },
                contentDescription = null,
                modifier = Modifier
                    .size(270.dp)
                    .clip(RoundedCornerShape(40.dp)),
                contentScale = ContentScale.Crop
            )


        }
    }

    private fun getData(roomId: String, rooms: MutableState<List<Room>>) {
        val url = "https://getpost-ilya1.up.railway.app/aboutroom/$roomId"

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                Log.d("Mylog", "Result: $response")
                val gson = Gson()
                val roomListType = object : TypeToken<List<Room>>() {}.type
                val utf8Response = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
                val newRooms: List<Room> = gson.fromJson(utf8Response, roomListType)

                rooms.value = newRooms

                // Получение первой комнаты из списка (если она есть)
                val firstRoom = newRooms.firstOrNull()
                if (firstRoom != null) {
                    // Вызов Composable roomname() и передача имени комнаты

                }
            },
            { error ->
                Log.d("Mylog", "Error: $error")
            }
        )

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }




    private fun whoinroom(roomId: String, participantsState: MutableState<List<Participant>>) {
        val url = "https://getpost-ilya1.up.railway.app/participants/$roomId"

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                Log.d("Mylog", "Result: $response")
                val gson = Gson()
                val participantListType = object : TypeToken<List<Participant>>() {}.type

                try {
                    val newParticipants: List<Participant> = gson.fromJson(response.toString(), participantListType)
                    participantsState.value = newParticipants
                } catch (e: JsonSyntaxException) {
                    Log.e("Mylog", "Error parsing JSON: $e")
                }
            },
            { error ->
                Log.d("Mylog", "Error: $error")
            }
        )

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }


    fun getTasks(roomId: String) {
        // Создайте экземпляр Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создайте экземпляр службы API
        val apiService = retrofit.create(ApiService::class.java)

        // Вызовите GET-запрос
        val call = apiService.getTasks(roomId)
        call.enqueue(object : Callback<List<TaskResponse>> {
            override fun onResponse(call: Call<List<TaskResponse>>, response: Response<List<TaskResponse>>) {
                if (response.isSuccessful) {
                    val taskList = response.body()
                    if (taskList != null) {
                        // Преобразуйте данные из taskList в список TaskData
                        val taskDataList: List<TaskData> = taskList.map { taskResponse ->
                            TaskData(
                                gitbranch = taskResponse.gitbranch,
                                filename = taskResponse.filename,
                                photo = taskResponse.photo,
                                mession = taskResponse.mession,
                                id = taskResponse.id

                            )
                        }

                        // Установите значение Task.value
                        task.value = taskDataList
                    }
                } else {
                    // Обработайте ошибку, если есть
                }
            }

            override fun onFailure(call: Call<List<TaskResponse>>, t: Throwable) {
                // Обработайте ошибку при отправке запроса
            }
        })
    }







    @Composable
    fun roomname(roomName: String, uid: String, roomId: String) {
        val cornerShape: Shape = RoundedCornerShape(20.dp) // устанавливаем радиус закругления углов

        var shows by remember {
            mutableStateOf(false)
        }
        if(shows == true) {
            ComposeAlertDialog(roomName, uid, roomId)
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(start = 10.dp, end = 10.dp)
                .border(2.dp, Color.Blue, shape = cornerShape)
                .clip(RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(Color.White),

        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center){
                    Text(text = roomName, fontSize = 24.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(start = 10.dp, end = 10.dp))
                }

                Spacer(modifier = Modifier.width(100.dp))
                Button(modifier = Modifier
                    .width(200.dp)
                    .padding(2.dp)
                    .fillMaxHeight()
                    , shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    onClick = {

                    shows = true
                }) {
                Text(text = "выйти из комнаты ", fontSize = 18.sp, textAlign = TextAlign.Center)

                }
            }
        }
    }


    @Composable
    fun ComposeAlertDialog(roomName: String, uid: String, roomId: String) {

        var show by remember {
            mutableStateOf(true)
        }

        if(show) {
            AlertDialog(
                onDismissRequest = { /* ... */ },
                title = { Text(text = "Подтверждение") },
                text = { Text(text = "Вы действительно хотите удалиться из комнаты '$roomName'?") },
                buttons = {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 10.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { show = !show

                                deleteRequest(uid, roomId)// вызываю функцию для удоления пользователя из комнаты

                                val intent = Intent(this@Roomsetting, Main_menu::class.java)
                                 startActivity(intent)
                                      },
                            colors = ButtonDefaults.buttonColors(Color.Red),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text("Удалиться", color = Color.White)
                        }
                        Button(
                            onClick = { show = !show  },
                            colors = ButtonDefaults.buttonColors(Color.LightGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text("Отмена", color = Color.DarkGray)
                        }
                    }
                }
            )
        }
    }@Composable
    fun DeleteAlertDialog(soket: String, uid: String) {

        var show by remember {
            mutableStateOf(true)
        }

        if(show) {
            AlertDialog(
                onDismissRequest = { /* ... */ },
                title = { Text(text = "Подтверждение") },
                text = { Text(text = "Вы действительно хотите удалить этого пользователя из  комнаты ") },
                buttons = {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 10.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                kickuser(soket, uid)
                                restartActivity()
                            },
                            colors = ButtonDefaults.buttonColors(Color.Red),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text("Удалиться", color = Color.White)
                        }
                        Button(
                            onClick = { show = !show  },
                            colors = ButtonDefaults.buttonColors(Color.LightGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text("Отмена", color = Color.DarkGray)
                        }
                    }
                }
            )
        }
    }




    fun deleteRequest(uid: String, roomId: String) {
        // Создаем Retrofit клиент
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создаем API интерфейс
        val api = retrofit.create(Api::class.java)

        // Создаем запрос
        val request = api.deleteRoom(uid, roomId)

        // Выполняем запрос
        request.enqueue(object : Callback<Unit> {
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                // Ошибка
                Log.e("deleteRequest", t.message ?: "Неизвестная ошибка")

                // Курятина
                if (t.message?.contains("404") ?: false) {
                    Log.d("deleteRequest", "Комната не найдена")
                } else {
                    Log.d("deleteRequest", "Неизвестная ошибка")
                }
            }

            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                // Успех
                if (response.isSuccessful) {
                    // Комната удалена
                    Log.d("deleteRequest", "Комната удалена")
                } else {
                    // Ошибка
                    Log.e("deleteRequest", "Ошибка удаления комнаты: ")
                }
            }
        })
    }
    fun kickuser( roomId: String, uid: String) {
        // Создаем Retrofit клиент
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создаем API интерфейс
        val api = retrofit.create(Kick::class.java)

        // Создаем запрос
        val call = api.user(roomId, uid)

        // Выполняем запрос асинхронно
        call.enqueue(object : Callback<Unit> {
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                // Ошибка
                Log.e("deleteRequest", t.message ?: "Неизвестная ошибка")

                // Курятина
                if (t.message?.contains("404") ?: false) {
                    Log.d("deleteRequest", "Комната не найдена")
                } else {
                    Log.d("deleteRequest", "Неизвестная ошибка")
                }
            }

            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                // Успех
                if (response.isSuccessful) {
                    // Комната удалена
                    Log.d("deleteRequest", "Комната удалена")
                } else {
                    // Ошибка
                    Log.e("deleteRequest", "Ошибка удаления комнаты: ${response.code()}")
                }
            }
        })
    }




    @Composable
    fun userinroom(participantsState: List<Participant>, Admin: String, uids: String) {  // this fun mean how match users in room
        val cornerShape: Shape = RoundedCornerShape(20.dp) // устанавливаем радиус закругления углов

        var shows by remember {
            mutableStateOf(false)
        }


        LazyRow(modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)) {
                item {
                    Card(
                        modifier = Modifier

                            .padding(
                                start = 10.dp,
                                end = 10.dp
                            )
                            .border(2.dp, Color.Blue, shape = cornerShape)
                            .clip(RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(Color.White),
                    ) {
                        //
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(participantsState) { participant ->
                                // Здесь вы можете создать элемент списка для каждого участника
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    // Выводим изображение аватарки с помощью библиотеки Coil
                                    Image(
                                        painter = rememberImagePainter(data = participant.imageUrl),
                                        contentDescription = null, // Устанавливаем null для contentDescription
                                        modifier = Modifier
                                            .size(100.dp)
                                            .padding(10.dp)
                                            .clip(RoundedCornerShape(30.dp))
                                    )
                                    Text(
                                        text = participant.username,
                                        fontSize = 24.sp,
                                        modifier = Modifier.padding(top = 30.dp)
                                    )
                                    if(shows == true) {
                                            DeleteAlertDialog("${storedRoomId!!}", "${participant.userId}", )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))
                                    Box(modifier = Modifier)
                                    Button(
                                        colors = ButtonDefaults.buttonColors(Color.Blue),
                                        modifier = Modifier
                                            .padding(top = 20.dp, end = 5.dp)
                                            .wrapContentWidth()
                                            .height(50.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        onClick = {
                                            val intent = Intent(this@Roomsetting, Aboutuser::class.java)
                                            intent.putExtra(
                                                "userId",
                                                participant.userId
                                            ) // Здесь вы добавляете данные в Intent
                                            startActivity(intent)
                                        }
                                        ) {
                                        Text(text = "about user")
                                        //{participant.userId} это надо передать
                                        }
                                    if(Admin == uids) {
                                        Spacer(modifier = Modifier.width(20.dp))
                                        Button(
                                            colors = ButtonDefaults.buttonColors(Color.Red),
                                            modifier = Modifier
                                                .padding(top = 20.dp)
                                                .wrapContentWidth()
                                                .height(50.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            onClick = {
                                                shows = true
                                            }) {
                                            Text(
                                                text = "Delete user",
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
    fun TaskList(tasks: List<TaskData>, roomId: String) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .heightIn(max = 1000.dp) // Максимальная высота 1000.dp
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                items(tasks) { task ->
                    TaskCard(task, roomId)
                }
            }
        }
    }


    @Composable
    fun TaskCard(task: TaskData,roomId: String) {
        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        val cornerShape: Shape = RoundedCornerShape(20.dp) // устанавливаем радиус закругления углов
        var uptext  = 800.dp


       /* BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1f)
        ) {
            val state = rememberTransformableState{zoomChange, panChang, rotationChange->
                    scale = (scale * zoomChange).coerceIn(1f, 5f)
                val extraWidth = (scale - 1) * constraints.maxWidth
                val extraHeight = (scale - 1) * constraints.maxHeight

                val maxX = extraWidth / 2
                val maxY= extraHeight / 2

                offset = Offset(
                    x = (offset.x + scale + panChang.x).coerceIn(-maxX, maxX),
                    y = (offset.y + scale + panChang.y).coerceIn(-maxY, maxY)
                )
                offset += panChang
            }
            Image(
                painter = rememberAsyncImagePainter(model = task.photo),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .transformable(state)
            )
        }
*/





        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(uptext)
                .padding(1.dp, top = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(2.dp, Color.Blue, shape = cornerShape),
            colors = CardDefaults.cardColors(Color.White),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(700.dp)
                    .padding(16.dp)
            ) {
                Text(text = "Git Branch: ${task.gitbranch}", fontSize = 24.sp)

                Text(text = "Filename: ${task.filename}", fontSize = 24.sp)

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f / 1f)
                ) {
                    val state = rememberTransformableState{zoomChange, panChang, rotationChange->
                        scale = (scale * zoomChange).coerceIn(1f, 5f)
                        val extraWidth = (scale - 1) * constraints.maxWidth
                        val extraHeight = (scale - 1) * constraints.maxHeight

                        val maxX = extraWidth / 2
                        val maxY= extraHeight / 2

                        offset = Offset(
                            x = (offset.x + scale + panChang.x).coerceIn(-maxX, maxX),
                            y = (offset.y + scale + panChang.y).coerceIn(-maxY, maxY)
                        )
                        offset += panChang
                    }
                    Image(
                        painter = rememberAsyncImagePainter(model = task.photo),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(700.dp)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                            .transformable(state)
                    )
                }

                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                ){
                    item{
                        Text(text = "Mission: ${task.mession}", fontSize = 24.sp)
                    }
                }


            }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {

                    IconButton(
                        modifier = Modifier.padding(top = 3.dp),
                        onClick = {
                            deleteData("$roomId", "${task.id}" )
                          //  deleteUserFromRoomAsync
                            restartActivity()
                        },
                    ){
                        Icon(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(60.dp),
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red // Цвет иконки
                        )
                    }


                    Text(text = "Delete", fontSize = 24.sp, modifier = Modifier.padding(top = 10.dp))

                    IconButton(
                        modifier = Modifier.padding(top = 15.dp),
                        onClick = {
                            deleteData("$roomId", "${task.id}" )
                            restartActivity()
                            // Ваш код, который будет выполнен при нажатии кнопки
                        },

                    ) {
                        Icon(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(60.dp),
                            imageVector = Icons.Default.Check,
                            contentDescription = "Delete",
                            tint = Color.Green // Цвет иконки
                        )

                    }
                    Text(text = "finished", fontSize = 24.sp, modifier = Modifier.padding(top = 20.dp))

                }

            }
        }






        fun deleteData(id: String, roomId: String) {
            // Запускаем корутину
            CoroutineScope(Dispatchers.IO).launch {
                // Создаем Retrofit клиент
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://getpost-ilya1.up.railway.app/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                // Создаем API интерфейс
                val api = retrofit.create(Apidelte::class.java)

                // Вызываем suspend функцию внутри корутины
                try {
                    val response = api.delete(id, roomId)
                    if (response.isSuccessful) {
                        // Комната удалена
                        Log.d("deleteRequest", "Комната удалена")
                    } else {
                        // Ошибка
                        Log.e("deleteRequest", "Ошибка удаления комнаты: ${response.message()}")
                    }
                } catch (e: Exception) {
                    // Ошибка
                    Log.e("deleteRequest", "Ошибка удаления комнаты: ${e.message}")
                }
            }
        }

                // ... }











    fun sendPostRequest(soket : String, uid : String ) {
        // Создайте экземпляр Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создайте экземпляр службы API
        val apiService = retrofit.create(Addids::class.java)

        // Создайте объект TaskRequest
        val request = ids("$soket", "$tokens", "$uid", )

        // Отправьте POST-запрос с передачей roomId в качестве параметра пути
        val call = apiService.Sanduser(request)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Запрос успешно отправлен
                    // Можете выполнить какие-либо дополнительные действия здесь
                } else {
                    // Обработайте ошибку, если есть
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Обработайте ошибку при отправке запроса
            }
        })
    }





    @Preview(showBackground = true)
    @Composable
    fun addtask(){

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {


        Button(
            colors = ButtonDefaults.buttonColors(Color.Blue),
            onClick = {
                val intent = Intent(this@Roomsetting, Addtask::class.java)
                startActivity(intent)

            },
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 2.dp, end = 2.dp)
                .clip(RoundedCornerShape(100.dp))
        ) {
            Text(text = stringResource(id = R.string.Addtask),fontSize = 24.sp)
        }
    }
}


    @Preview(showBackground = true)
    @Composable
    fun Teamspeack() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {


            Button(
                colors = ButtonDefaults.buttonColors(Color.Green),
                onClick = {
                    val intent = Intent(this@Roomsetting, TeamSpeak::class.java)
                    startActivity(intent)

                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 2.dp, end = 2.dp)
                    .clip(RoundedCornerShape(100.dp))
            ) {
                Text(text = stringResource(id = R.string.Teamspek), fontSize = 24.sp)
            }
        }
    }







}