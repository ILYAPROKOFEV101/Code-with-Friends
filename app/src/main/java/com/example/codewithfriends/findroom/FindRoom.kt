package com.example.codewithfriends.findroom

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

import okhttp3.OkHttpClient
import okhttp3.Request

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation


import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.swipeable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.example.codewithfriends.Aboutusers.Aboutuser
import com.example.codewithfriends.R
import com.example.codewithfriends.chats.Chat




import com.example.codewithfriends.presentation.profile.ID
import com.example.codewithfriends.presentation.profile.IMG
import com.example.codewithfriends.presentation.profile.UID
import com.example.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.example.codewithfriends.presentation.sign_in.UserData
import com.example.codewithfriends.roomsetting.Roomsetting
import com.example.codewithfriends.roomsetting.TaskData
import com.example.reaction.logik.PreferenceHelper
import com.example.reaction.logik.PreferenceHelper.saveRoomId
import com.google.android.gms.auth.api.identity.Identity
import okhttp3.WebSocket
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codewithfriends.MainViewModel


class FindRoom : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    var showCircle by mutableStateOf(false)

    private val client = OkHttpClient()

   private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            val viewModel = viewModel<MainViewModel>()
            val isLoading by viewModel.isLoading.collectAsState()
            val swipeRefresh = rememberSwipeRefreshState(isRefreshing = isLoading)

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {




                val rooms = remember { mutableStateOf(emptyList<Room>()) }


                // Задержка перехода на новую страницу через 3 секунды
                Handler(Looper.getMainLooper()).postDelayed({
                    getData(rooms)
                }, 500) // 3000 миллисекунд (3 секунды)
                // Вызывайте getData только после установки ContentView

                // Ваш код Composable
                SwipeRefresh(
                    state = swipeRefresh,
                    onRefresh = {
                        recreate()
                    }
                ) {

                        Box(modifier = Modifier.fillMaxSize()) {
                            RoomList(rooms.value)
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







    @Composable
    fun RoomList(rooms: List<Room>) {
            LazyColumn {
                items(rooms) { room ->
                    RoomItem(room)
                }
            }

    }

    @Composable
   fun RoomItem(room: Room){
    val joinroom: Color = colorResource(id = R.color.joinroom)
    val creatroom: Color = colorResource(id = R.color.creatroom)

        

        Spacer(modifier = Modifier.height(20.dp))

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
                                Text(text = "${room.roomName}", modifier = Modifier.padding(top = 10.dp , start = 10.dp), style = TextStyle(fontSize = 24.sp))
                                       }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 5.dp, start = 10.dp)
                                    .height(65.dp)
                                    .clip(CircleShape)
                               )
                                {
                                Text(text = "${room.language}", modifier = Modifier.padding( start = 10.dp), style = TextStyle(fontSize = 24.sp))
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
                                    val intent = Intent(this@FindRoom, Chat::class.java)
                                    intent.putExtra(
                                        "url",
                                       room.url,
                                    )
                                    intent.putExtra(
                                        "Admin",
                                        room.Admin
                                    ) // Здесь вы добавляете данные в Intent
                                    startActivity(intent)

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
                        item {  Text(text = "${room.aboutRoom}", modifier = Modifier.padding( start = 10.dp), style = TextStyle(fontSize = 24.sp)) }
                    }

                }

            }
        Spacer(modifier = Modifier.height(10.dp))

    }


    fun goToChatActivity(roomId: String) {

        saveRoomId(this, roomId)

    }





}

