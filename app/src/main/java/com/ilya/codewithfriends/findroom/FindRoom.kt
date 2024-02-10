package com.ilya.codewithfriends.findroom

import LoadingComponent
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import okhttp3.OkHttpClient

import androidx.compose.foundation.Image
import androidx.compose.foundation.border


import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.ilya.codewithfriends.R
import com.ilya.codewithfriends.chats.Chat




import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.reaction.logik.PreferenceHelper.saveRoomId
import com.google.android.gms.auth.api.identity.Identity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel

import com.ilya.codewithfriends.MainViewModel
import com.ilya.codewithfriends.findroom.ui.theme.CodeWithFriendsTheme
import com.ilya.codewithfriends.test.TestActivity
import com.ilya.reaction.logik.PreferenceHelper.getRoomId


class FindRoom : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    var showCircle by mutableStateOf(false)

    private val client = OkHttpClient()
    private var storedRoomId: String? = null // Объявляем на уровне класса
   private val handler = Handler()


    var myroom by  mutableStateOf(false)
    var select by  mutableStateOf(false)

    val languages = listOf(
        "android development",
        "ios development",
        "Web development",
        "Game development",
        "C++ Software",
        "Machine learning "
    )
    var selectedLanguage by  mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )

        val loadingComponent = LoadingComponent()
        loadingComponent.userexsist("$id", this)

        storedRoomId = getRoomId(this)

        super.onCreate(savedInstanceState)

        setContent {
            val viewModel = viewModel<MainViewModel>()
            val isLoading by viewModel.isLoading.collectAsState()
            val swipeRefresh = rememberSwipeRefreshState(isRefreshing = isLoading)

            CodeWithFriendsTheme{
                SwipeRefresh(
                    state = swipeRefresh,
                    onRefresh = {
                        recreate()
                    }
                ) {
                    val rooms = remember { mutableStateOf(emptyList<Room>()) }
                    val data_from_myroom = remember { mutableStateOf(emptyList<Room>()) }

                    // Задержка перехода на новую страницу через 3 секунды
                    Handler(Looper.getMainLooper()).postDelayed({
                        getData(rooms)
                    }, 500) // 3000 миллисекунд (3 секунды)

                    // Вызывайте getData только после установки ContentView
                    GET_MYROOM("$id", data_from_myroom)

                    SwipeRefresh(
                        state = swipeRefresh,
                        onRefresh = {
                            recreate()
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colorScheme.background)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f) // Отдает оставшееся пространство RoomList
                            ) {
                                RoomList(rooms.value, data_from_myroom.value)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                            ) {
                                val testActivity = TestActivity()
                                testActivity.ButtonBar(this@FindRoom)
                            }
                        }
                    }

                }
            }
        }
    }

    private fun getData(rooms: MutableState<List<Room>>) {
        // Создаем Retrofit клиент
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создаем API интерфейс
        val api = retrofit.create(Api::class.java)

        // Создаем запрос
        val request = api.getRooms()

        // Выполняем запрос
        request.enqueue(object : Callback<List<Room>> {
            override fun onFailure(call: Call<List<Room>>, t: Throwable) {
                // Ошибка
                Log.e("getData", t.message ?: "Неизвестная ошибка")

                // Курятина
                if (t.message?.contains("404") ?: false) {
                    Log.d("getData", "Данные не найдены")
                } else {
                    Log.d("getData", "Неизвестная ошибка")
                }
            }

            override fun onResponse(call: Call<List<Room>>, response: Response<List<Room>>) {
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

    private fun GET_MYROOM(uid:String, rooms: MutableState<List<Room>>) {
        // Создаем Retrofit клиент
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создаем API интерфейс
        val api = retrofit.create(Getmyroom::class.java)

        // Создаем запрос
        val request = api.getRooms(uid)

        // Выполняем запрос
        request.enqueue(object : Callback<List<Room>> {
            override fun onFailure(call: Call<List<Room>>, t: Throwable) {
                // Ошибка
                Log.e("getData", t.message ?: "Неизвестная ошибка")

                // Курятина
                if (t.message?.contains("404") ?: false) {
                    Log.d("getData", "Данные не найдены")
                } else {
                    Log.d("getData", "Неизвестная ошибка")
                }
            }

            override fun onResponse(call: Call<List<Room>>, response: Response<List<Room>>) {
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


    @Preview(showBackground = true)
    @Composable
    fun NOTFound(){



        Column(Modifier.fillMaxSize()) {
            Image(
                 painter = painterResource(id = R.drawable.notfound),
                contentDescription = "Nothing",

                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(350.dp)
                    .clip(RoundedCornerShape(50.dp))

            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(id = R.string.notfoun),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp)
            )


        }
    }




    @Composable
    fun RoomList(rooms: List<Room>, Myroom: List<Room>) {
        var notFoundVisible by remember { mutableStateOf(false) }
        var showroom by remember { mutableStateOf(true) }

        LaunchedEffect(true) {
            // Задержка на 4 секунды
            delay(4000)
            notFoundVisible = true
        }

        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )


        LazyColumn(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                item {
                    Selecte()
                }
            if (rooms.isNotEmpty()) {

                if(myroom == true){
                    items(rooms) { room ->
                        if (room.Admin == id) {
                            RoomItem(room)//hello
                        }

                    }

                    if (showroom == true) {
                        items(Myroom) { Myroom ->
                            RoomItem(Myroom)
                        }
                    }

                } else if (select == true && selectedLanguage != ""){
                    items(rooms) { room ->
                        if(room.language == selectedLanguage){
                            RoomItem(room)
                        }
                        
                    }
                } else {
                    items(rooms) { room ->
                            RoomItem(room)
                    }
                }

            } else {

                if (notFoundVisible) {
                    item {
                        NOTFound()
                    }
                }

            }
        }
    }



    @Composable
   fun RoomItem(room: Room){
    val joinroom: Color = colorResource(id = R.color.joinroom)
    val creatroom: Color = colorResource(id = R.color.creatroom)





        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .height(500.dp)
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color.Black)
                .border(
                    border = BorderStroke(5.dp, SolidColor(joinroom)),
                    shape = RoundedCornerShape(30.dp)
                ),
            colors = CardDefaults.cardColors(
                MaterialTheme.colorScheme.background,
            ),

        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.White)) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .height(150.dp),
                    )
                    {
                        Box(
                            modifier = Modifier
                                .weight(0.4f)
                                .align(Alignment.CenterVertically)
                        )
                        {
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
                                    .size(150.dp)
                                    .padding(start = 15.dp, end = 5.dp, top = 15.dp)
                                    .clip(RoundedCornerShape(40.dp)),
                               contentScale = ContentScale.Crop
                            )


                        }
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.6f)
                                .padding(end = 5.dp)
                              ){
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp, start = 10.dp)
                                        .height(65.dp)
                                        .clip(CircleShape)
                                    ) {
                                    Text(
                                        text = "${room.roomName}",
                                        modifier = Modifier.padding(top = 10.dp, start = 10.dp),
                                        style = TextStyle(
                                            fontSize = 24.sp,
                                            color = Color.Black  // Set the text color to colorScheme.background
                                        )
                                    )

                                }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 5.dp, start = 10.dp)
                                    .height(65.dp)
                                    .clip(CircleShape)
                               )
                                {
                                Text(text = "${room.language}", modifier = Modifier
                                    .padding( start = 10.dp),
                                    style = TextStyle(fontSize = 24.sp),
                                    color = Color.Black  )
                                }

                             }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .height(65.dp)
                        .fillMaxWidth())
                    {
                            Button(
                                onClick = {
                                    if (room.id != null) {
                                        val intent = Intent(this@FindRoom, Chat::class.java)
                                        startActivity(intent)
                                    } else {
                                        // Обработка ситуации, когда идентификатор комнаты равен null
                                        // например, вы можете вывести сообщение об ошибке
                                        Toast.makeText(this@FindRoom, "Идентификатор комнаты пуст", Toast.LENGTH_SHORT).show()
                                    }

                                goToChatActivity(room.id)
                                showCircle = !showCircle


                            },
                                colors = ButtonDefaults.buttonColors(creatroom),
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(20.dp),

                                  ) {
                                Text(text = "Join in room: ${room.placeInRoom}", modifier = Modifier, style = TextStyle(fontSize = 24.sp))
                                }

                    }

                    LazyColumn(modifier = Modifier
                        .padding(start = 5.dp, end = 5.dp)
                        .fillMaxWidth()
                        .height(350.dp)
                        .clip(RoundedCornerShape(30.dp))
                    ){
                        item {  Text(text = "${room.aboutRoom}", modifier = Modifier.padding( start = 10.dp), style = TextStyle(fontSize = 24.sp), color = Color.Black  ) }
                    }

                }
            }
        Spacer(modifier = Modifier.height(10.dp))

    }



    @Preview
    @Composable
    fun Selecte() {
        var expanded by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(if (!myroom && !select) Color(0xFF0BDD27) else Color(
                    0xFF246DFF
                )
                ),
                onClick = {
                    myroom = false
                    select = false
                    expanded = false
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(5.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = stringResource(id = R.string.all))
            }

            Button(
                colors = ButtonDefaults.buttonColors(if (myroom) Color(0xFF0BDD27) else Color(
                    0xFF246DFF
                )),
                onClick = {
                    myroom = true
                    select = false
                    expanded = false
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(5.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = stringResource(id = R.string.Muroom))
            }

            Button(
                colors = ButtonDefaults.buttonColors(if (select) Color(0xFF0BDD27) else Color(
                    0xFF246DFF
                )),
                onClick = {
                    expanded = true
                    myroom = false
                    select = true
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(5.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(text = stringResource(id = R.string.Selected))
            }

            // DropdownMenu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                // Populate the menu with items from the list
                languages.forEach { language ->
                    DropdownMenuItem(
                        onClick = {
                            selectedLanguage = language
                            expanded = false
                        }
                    ) {
                        Text(text = language)
                    }
                }
            }
        }
    }

    fun goToChatActivity(roomId: String) {

        saveRoomId(this, roomId)

    }





}

