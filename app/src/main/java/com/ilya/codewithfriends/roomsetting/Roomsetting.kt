package com.ilya.codewithfriends.roomsetting

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
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
import com.ilya.codewithfriends.Aboutusers.Aboutuser
import com.ilya.codewithfriends.R
import com.ilya.codewithfriends.Startmenu.Main_menu
import com.ilya.codewithfriends.createamspeck.TeamSpeak
import com.ilya.codewithfriends.findroom.Room
import com.ilya.codewithfriends.firebase.Addtask
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.codewithfriends.roomsetting.ui.theme.Participant
import com.ilya.reaction.logik.PreferenceHelper.getRoomId
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.ilya.codewithfriends.MainViewModel
import com.ilya.codewithfriends.Viewphote.ViewPhoto
import com.ilya.codewithfriends.test.TestActivity
import com.ilya.reaction.logik.PreferenceHelper
import com.ilya.reaction.logik.PreferenceHelper.clearAllMessages
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ilya.codewithfriends.Viewphote.ViewPhoto_fragment
import com.ilya.codewithfriends.Vois.ViceActivity
import com.ilya.codewithfriends.chattest.ChatRoomm
import com.ilya.codewithfriends.chattest.ChatScreen
import com.ilya.codewithfriends.chattest.ChatmenuContent

import com.ilya.codewithfriends.chattest.ViewPhoto
import com.ilya.codewithfriends.chattest.fragments.FreandsFragments
import com.ilya.codewithfriends.chattest.fragments.RoomChat
import kotlinx.coroutines.GlobalScope
import retrofit2.create


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
    // Состояния для отслеживания значений
    var over by   mutableStateOf(0f)
    var delete by   mutableStateOf(0f)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }

            val token = task.result
            Log.e("Tag", "Token -> $token")
            tokens = token

        }




        storedRoomId = getRoomId(this)


        storedRoomId?.let { nonNullValue ->
            getTasks(nonNullValue)
        }

        storedRoomId?.let { nonNullValue ->
            OVER_DELETE(nonNullValue)
        }


        setContent {
            val viewModel = viewModel<MainViewModel>()
            val isLoading by viewModel.isLoading.collectAsState()
            val swipeRefresh = rememberSwipeRefreshState(isRefreshing = isLoading)

            val data_from_myroom = remember { mutableStateOf(emptyList<Room2>()) }

            val rooms = remember { mutableStateOf(emptyList<Room>()) }

            val ivite_list = remember { mutableStateOf(emptyList<Usrs_ivite>()) }

            val participants = remember { mutableStateOf(emptyList<Participant>()) }

            GET_MYROOM(storedRoomId!!, data_from_myroom)
            Get_invite_list(storedRoomId!!, ivite_list)
            // Проверяем, есть ли данные комнаты


            val firstRoom = rooms.value.firstOrNull()


            val id = ID(
                userData = googleAuthUiClient.getSignedInUser()
            )

            if (id != null && tokens != null) {
                sendPostRequest(storedRoomId!!, "$id")
            }


            storedRoomId = getRoomId(this)




            // Сохранение значения для ключа KEY_STRING_1
            PreferenceHelper.saveid(this, "$id")



            // Сохранение значения для ключа KEY_STRING_4
            PreferenceHelper.saveSoket(this, "$storedRoomId")


            Handler(Looper.getMainLooper()).postDelayed({
                getData(storedRoomId!!, rooms)

                whoinroom(storedRoomId!!, participants)


            }, 500) // 3000 миллисекунд (3 секунды)

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val context = LocalContext.current
                SwipeRefresh(
                    state = swipeRefresh,
                    onRefresh =
                    {
                        recreate()
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f) // Отдает оставшееся пространство RoomList
                                .background(Color(0x2F3083FF))
                        ) {
                            item {
                                icon(data_from_myroom)
                                Spacer(modifier = Modifier.height(30.dp))
                            }

                            // Вызываем roomname() только если есть данные комнаты

                            item {
                                firstRoom?.let {
                                    roomname(
                                        roomName = it.roomName,
                                        "$id",
                                        storedRoomId!!,
                                        "$id",
                                                data_from_myroom
                                    )
                                }
                                Spacer(modifier = Modifier.height(30.dp))
                            }
                            item {
                                User_ivite(ivite_list.value)
                                Spacer(modifier = Modifier.height(30.dp))
                            }


                            item {
                                userinroom(
                                    participants.value,
                                    "$id",
                                    data_from_myroom
                                ) // Передаем participants.value
                                Spacer(modifier = Modifier.height(30.dp))
                            }


                            item {
                                TaskList(task.value, storedRoomId!!)
                                // Передаем Task.value в Composable функцию, или пустой список, если Task.value == null
                                Spacer(modifier = Modifier.height(30.dp))

                            }
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(450.dp)
                                ) {
                                    SimpleDonutChart(context, over, delete)
                                    // Передаем Task.value в Composable функцию, или пустой список, если Task.value == null
                                }
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                        ) {
                            val testActivity = TestActivity()
                            testActivity.ButtonBar(context)
                        }
                    }
                }
            }
        }

    }
    @Composable
    fun icon( rooms: MutableState<List<Room2>>) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .padding(bottom = 15.dp, top = 15.dp, start = 70.dp, end = 70.dp)
                .clip(RoundedCornerShape(180.dp)),
            shape = RoundedCornerShape(180.dp), // Применяем закругленные углы к Card
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
            colors = CardDefaults.cardColors(Color.White),
        ) {
            val roomsList: List<Room2> = rooms.value

            for (room in roomsList) {
            Image(
                painter = if (room.url.isNotEmpty()) {
                    // Load image from URL
                    rememberImagePainter(data = room.url)
                } else {
                    // Load a default image when URL is empty
                    painterResource(id = R.drawable.android) // Replace with your default image resource
                },
                contentDescription = null,
                modifier = Modifier
                    .size(270.dp)
                    .clip(RoundedCornerShape(180.dp))
                    .align(Alignment.CenterHorizontally)
                ,
                contentScale = ContentScale.Crop,
            )


        }
    }
    }



    fun OVER_DELETE(roomId: String) {
        // Создайте экземпляр Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создайте экземпляр службы API
        val apiService = retrofit.create(OVER_DELETE::class.java)

        // Вызовите GET-запрос
        val call = apiService.OVER_AND_DELETE(roomId)
        call.enqueue(object : Callback<List<Over_DeletetItem>> {
            override fun onResponse(call: Call<List<Over_DeletetItem>>, response: Response<List<Over_DeletetItem>>) {
                if (response.isSuccessful) {
                    val overDeleteList = response.body()
                    if (overDeleteList != null && overDeleteList.isNotEmpty()) {
                        // Пример: присвоение значений переменным over и delete
                        over = overDeleteList[0].over.toFloat()
                        delete = overDeleteList[0].deletet.toFloat()
                    }
                } else {
                    // Обработайте ошибку, если есть
                }
            }

            override fun onFailure(call: Call<List<Over_DeletetItem>>, t: Throwable) {
                // Обработайте ошибку при отправке запроса
            }
        })
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

    private fun Get_invite_list(roomId: String, ivitelist: MutableState<List<Usrs_ivite>>) {
        // Создаем Retrofit клиент
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создаем API интерфейс
        val api = retrofit.create(Git_ivite::class.java)

        // Создаем запрос
        val request = api.get_ivite(roomId)

        Log.d("Getivite", "Starting request for room ID: $roomId")

        // Выполняем запрос
        request.enqueue(object : Callback<List<Usrs_ivite>> {
            override fun onFailure(call: Call<List<Usrs_ivite>>, t: Throwable) {
                // Ошибка
                Log.e("Getivite", "Failed to get invite list for room ID: $roomId", t)
                // Проверка на ошибку 404
                if (t.message?.contains("404") ?: false) {
                    Log.d("Getivite", "Data not found for room ID: $roomId")
                } else {
                    Log.e("Getivite", "Unknown error occurred for room ID: $roomId")
                }
            }

            override fun onResponse(call: Call<List<Usrs_ivite>>, response: Response<List<Usrs_ivite>>) {
                // Успех
                if (response.isSuccessful) {
                    // Получаем данные
                    val newRooms = response.body() ?: emptyList()

                    // Логируем полученные данные
                    Log.d("Getivite", "Invite list retrieved successfully for room ID: $roomId")
                    Log.d("Getivite", "Received invite list: $newRooms")

                    // Обновляем состояние
                    ivitelist.value = newRooms
                } else {
                    // Ошибка
                    Log.e("Getivite", "Error getting invite list for room ID: $roomId, Response code: ${response.code()}, Error body: ${response.errorBody()?.string()}")
                }
            }
        })
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
        val apiService = retrofit.create(ApiService_ilya::class.java)

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
    fun roomname(
        roomName: String,
        uid: String,
        roomId: String,
        uids: String,
        rooms: MutableState<List<Room2>>,) {

        val COLOR_FOR_DELETE_BUTTON: Color = colorResource(id = R.color.custom00FFE8)

        val cornerShape: Shape = RoundedCornerShape(20.dp) // устанавливаем радиус закругления углов

        var shows by remember {
            mutableStateOf(false)
        }
        var delete by remember {
            mutableStateOf(false)
        }
        if (shows == true) {
            ComposeAlertDialog(roomName, uid, roomId, this)
        }
        if (delete == true) {
            DeleteRoom(uid, roomId, this)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(start = 10.dp, end = 10.dp)
                .border(4.dp, Color.Blue, shape = RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(Color.White),

            ) {
            LazyRow(modifier = Modifier.fillMaxSize()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = roomName,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(100.dp))
                    Button(modifier = Modifier
                        .width(200.dp)
                        .padding(5.dp)
                        .fillMaxHeight(), shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        onClick = {

                            shows = true

                        }) {
                        Text(
                            text = stringResource(id = R.string.Exit),// change
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )

                    }
                    val roomsList: List<Room2> = rooms.value
                    for (room in roomsList) {
                        if (room.Admin == uids) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(modifier = Modifier
                                .width(200.dp)
                                .padding(5.dp)
                                .fillMaxHeight(), shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(COLOR_FOR_DELETE_BUTTON),
                                onClick = {
                                    delete = true
                                }) {
                                Text(
                                    text = stringResource(id = R.string.deleteroom),
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun User_ivite(inviteList: List<Usrs_ivite>) {
        var expanded by remember { mutableStateOf(false) }

        // Анимируем высоту карточки
        val cardHeight by animateDpAsState(
            targetValue = if (expanded) 600.dp else 80.dp
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight) // Используем анимированную высоту
                .padding(start = 10.dp, end = 10.dp)
                .border(4.dp, Color.Blue, shape = RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .clickable { expanded = !expanded },
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Text(
                    text = "Invite List",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(550.dp)
                        .clip(RoundedCornerShape(30.dp)),
                    reverseLayout = false,
                ) {

                    items(inviteList) { ivitelist ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(start = 5.dp, end = 5.dp)
                                .clip(RoundedCornerShape(30.dp))
                                .border(2.dp, Color.Blue, shape = RoundedCornerShape(30.dp)),
                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(0.3f)
                                ) {
                                    Image(
                                        painter = rememberImagePainter(ivitelist.url),
                                        contentDescription = null, // Устанавливаем null для contentDescription
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(30.dp))
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(0.7f)

                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(30.dp)
                                    ) {
                                        Text(ivitelist.name)

                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Row(modifier = Modifier
                                        .fillMaxSize())
                                    {
                                        Button(
                                            modifier = Modifier
                                                .weight(0.45f)
                                                .clip(RoundedCornerShape(10.dp)),
                                            colors = ButtonDefaults.buttonColors(Color(0xFF315FF3)),
                                            onClick = {
                                                val intent = Intent(this@Roomsetting, Aboutuser::class.java)
                                                intent.putExtra(
                                                    "userId",
                                                    ivitelist.uid
                                                ) // Здесь вы добавляете данные в Intent
                                                startActivity(intent)
                                            })
                                        {
                                            Text(text = stringResource(id = R.string.aboutuser))
                                        }

                                        Spacer(modifier = Modifier.weight(0.1f))
                                        Button(
                                            modifier = Modifier
                                                .weight(0.45f)
                                                .clip(RoundedCornerShape(10.dp)),
                                            colors = ButtonDefaults.buttonColors(Color(0xFF18AD08)),
                                            onClick = {

                                            })
                                        {
                                            Text(text = stringResource(id = R.string.adduser))
                                        }

                                    }


                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }

    @Composable
    fun ComposeAlertDialog(roomName: String, uid: String, roomId: String, context: Context) {

        val Text = stringResource(id = R.string.want)
        var show by remember {
            mutableStateOf(true)
        }

        if(show) {
            AlertDialog(
                onDismissRequest = { /* ... */ },
                title = { Text(text = stringResource(id = R.string.Confirmation)) },
                text = { Text(text = "$Text 'room:$roomName'?") },
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
                                clearAllMessages(context)
                                      },
                            colors = ButtonDefaults.buttonColors(Color.Red),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text(stringResource(id = R.string.Delete), color = Color.White)
                        }
                        Button(
                            onClick = { show = !show  },
                            colors = ButtonDefaults.buttonColors(Color.LightGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text(stringResource(id = R.string.Cancel), color = Color.DarkGray)
                        }
                    }
                }
            )
        }
    }




    @Composable
    fun DeleteRoom(uid: String, roomId: String, context: Context) {

        var show by remember {
            mutableStateOf(true)
        }

        if(show) {
            AlertDialog(
                onDismissRequest = { /* ... */ },
                title = { Text(text = stringResource(id = R.string.Confirmation)) },
                text = { Text(text = stringResource(id = R.string.destroy)) },
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

                                //deleteRequest(uid, roomId)// вызываю функцию для удоления пользователя из комнаты
                                DeleteeRoom(uid, roomId)
                                val intent = Intent(this@Roomsetting, Main_menu::class.java)
                                 startActivity(intent)
                                // Вызов функции для очистки всех сообщений
                                clearAllMessages(context)

                            },
                            colors = ButtonDefaults.buttonColors(Color.Red),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text(stringResource(id = R.string.destroyroom), color = Color.White)
                        }
                        Button(
                            onClick = { show = !show  },
                            colors = ButtonDefaults.buttonColors(Color.LightGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text(stringResource(id = R.string.Cancel), color = Color.DarkGray)
                        }
                    }
                }
            )
        }
    }
    @Composable
    fun DeleteAlertDialog(soket: String, uid: String) {

        var show by remember {
            mutableStateOf(true)
        }

        if(show) {
            AlertDialog(
                onDismissRequest = { /* ... */ },
                title = { Text(text = stringResource(id = R.string.Confirmation)) },
                text = { Text(text = stringResource(id = R.string.Confirmationrealy)) },
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
                                recreate()
                            },
                            colors = ButtonDefaults.buttonColors(Color.Red),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text(stringResource(id = R.string.Delete), color = Color.White)
                        }
                        Button(
                            onClick = { show = false},
                            colors = ButtonDefaults.buttonColors(Color.LightGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text(stringResource(id = R.string.Cancel), color = Color.DarkGray)

                        }
                    }
                }
            )
        }
    }

    fun DeleteeRoom(uid: String, roomId: String) {
        // Создаем Retrofit клиент
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создаем API интерфейс
        val api = retrofit.create(DeleteRoom::class.java)

        // Создаем запрос
        val request = api.deleteRooms( roomId, uid)

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
    fun userinroom(participantsState: List<Participant>, uids: String,rooms: MutableState<List<Room2>>,) {  // this fun mean how match users in room
        val cornerShape: Shape = RoundedCornerShape(20.dp) // устанавливаем радиус закругления углов

        var shows by remember {
            mutableStateOf(false)
        }


        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
        ) {
            item {
                Card(
                    modifier = Modifier

                        .padding(
                            start = 10.dp,
                            end = 10.dp
                        )
                        .border(4.dp, Color.Blue, shape = cornerShape)
                        .clip(RoundedCornerShape(20.dp)),
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
                                    modifier = Modifier
                                        .padding(top = 30.dp)
                                        .width(200.dp)
                                )
                                if (shows == true) {
                                    DeleteAlertDialog("${storedRoomId!!}", "${participant.userId}",)
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
                                    Text(text = stringResource(id = R.string.aboutuser))
                                    //{participant.userId} это надо передать
                                }
                                val roomsList: List<Room2> = rooms.value

                                for (room in roomsList) {
                                    if (room.Admin == uids) {
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
                                        Spacer(modifier = Modifier.width(10.dp))


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
                .heightIn(min = 100.dp, max = if (tasks.isNotEmpty()) 1000.dp else 100.dp)
        ) {
            val lazyListState = rememberLazyListState()
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .heightIn(min = 100.dp, max = if (tasks.isNotEmpty()) 1000.dp else 100.dp),
                state = lazyListState
            ) {
                itemsIndexed(tasks) { index, task ->
                    TaskCard(task, roomId)
                    Spacer(modifier = Modifier.width(30.dp))
                }
            }
        }
    }



    @Composable
    fun TaskCard(task: TaskData,roomId: String) {



        val navController = rememberNavController()

        Card(
            modifier = Modifier
                .width(500.dp)
                .height(1000.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(4.dp, Color.Blue, shape = RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(Color.White),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(text = "Git Branch: ${task.gitbranch}", fontSize = 24.sp)

                Text(text = "Filename: ${task.filename}", fontSize = 24.sp)



                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {

                    Image(
                        painter = rememberAsyncImagePainter(model = task.photo),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(20.dp)
                            .height(600.dp)
                            .clickable {
                                
                            }
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    item {
                        Text(text = "Mission: ${task.mession}", fontSize = 24.sp)
                    }
                }



                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {

                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        onClick = {
                            deleteDataComplit("$roomId", "${task.id}")

                            //  deleteUserFromRoomAsync
                            recreate()
                        },
                    ) {

                        Text(text = "Delete", fontSize = 15.sp, modifier = Modifier)

                        Icon(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(60.dp),
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White // Цвет иконки
                        )
                    }

                    Spacer(modifier = Modifier.width(50.dp))


                    Button(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(Color.Green),
                        onClick = {
                            deleteData("$roomId", "${task.id}")
                            recreate()
                        },
                    ) {
                        Text(text = "finished", fontSize = 15.sp, modifier = Modifier)

                        Icon(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(60.dp),
                            imageVector = Icons.Default.Check,
                            contentDescription = "Delete",
                            tint = Color.White // Цвет иконки
                        )

                    }
                    Spacer(modifier = Modifier.width(10.dp))

                }
            }
        }
    }





    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun SimpleDonutChart(context: Context, over: Float, delet: Float) {
        var selectedLabelText  by remember { mutableStateOf("") }
        val accessibilitySheetState =
            rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        val scope = rememberCoroutineScope()

        // Новые данные с двумя секторами


        val donutChartData = PieChartData(
            slices = listOf(
                PieChartData.Slice("finished", over, Color(0xFF00FF05)),
                PieChartData.Slice("deleted", delet, Color(0xFFFF1111)),

                ),
            plotType = PlotType.Donut
        )




        val pieChartConfig =
            PieChartConfig(
                labelVisible = true,
                strokeWidth = 120f,
                labelColor = Color.Black,
                activeSliceAlpha = .9f,
                isEllipsizeEnabled = true,
                labelTypeface = Typeface.defaultFromStyle(Typeface.BOLD),
                isAnimationEnable = true,
                chartPadding = 25,
                labelFontSize = 42.sp,
                backgroundColor = Color(0x2F3083FF)
            )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
        ) {
            DonutPieChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(),

                donutChartData, // Передаем новые данные
                pieChartConfig
            ) { slice ->
                // Обновляем выбранный текст при выборе сегмента
                selectedLabelText = slice.label

            }
            // Отображаем выбранный текст внизу

            androidx.compose.material.Text(

                text = " $selectedLabelText",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally)
            )
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
    fun deleteDataComplit(id: String, roomId: String) {
        // Запускаем корутину
        CoroutineScope(Dispatchers.IO).launch {
            // Создаем Retrofit клиент
            val retrofit = Retrofit.Builder()
                .baseUrl("https://getpost-ilya1.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Создаем API интерфейс
            val api = retrofit.create(API_DELET::class.java)

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
                finish()
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
                    val intent = Intent(this@Roomsetting, ViceActivity::class.java)
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
    private fun GET_MYROOM(uid:String, rooms: MutableState<List<Room2>>) {
        // Создаем Retrofit клиент
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создаем API интерфейс
        val api = retrofit.create(GetmyAPI::class.java)

        // Создаем запрос
        val request = api.GETIMG(uid)

        // Выполняем запрос
        request.enqueue(object : Callback<List<Room2>> {
            override fun onFailure(call: Call<List<Room2>>, t: Throwable) {
                // Ошибка
                Log.e("getData", t.message ?: "Неизвестная ошибка")

                // Курятина
                if (t.message?.contains("404") ?: false) {
                    Log.d("getData", "Данные не найдены")
                } else {
                    Log.d("getData", "Неизвестная ошибка")
                }
            }

            override fun onResponse(call: Call<List<Room2>>, response: Response<List<Room2>>) {
                // Успех
                if (response.isSuccessful) {
                    // Получаем данные
                    val newRooms = response.body() ?: emptyList()

                    // Обновляем состояние
                    rooms.value = newRooms
                } else {
                    // Ошибка
                    Log.e("getData", "Ошибка получения данных: ")
                }
            }
        })
    }



}
