package com.example.codewithfriends.roomsetting

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.codewithfriends.R
import com.example.codewithfriends.findroom.Room
import com.example.codewithfriends.presentation.profile.ID
import com.example.codewithfriends.presentation.profile.IMG
import com.example.codewithfriends.presentation.profile.UID
import com.example.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.example.codewithfriends.roomsetting.ui.theme.CodeWithFriendsTheme
import com.example.reaction.logik.PreferenceHelper
import com.example.reaction.logik.PreferenceHelper.getRoomId
import com.google.android.gms.auth.api.identity.Identity
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class Roomsetting : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private var storedRoomId: String? = null // Объявляем на уровне класса

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storedRoomId = getRoomId(this)


        setContent {
            val rooms = remember { mutableStateOf(emptyList<Room>()) }
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

            getData(storedRoomId!!, rooms)

            LazyColumn {
                item {
                    icon()
                    Spacer(modifier = Modifier.height(30.dp))
                }

                // Вызываем roomname() только если есть данные комнаты

                    item {
                        firstRoom?.let { roomname(roomName = it.roomName) }
                        Spacer(modifier = Modifier.height(30.dp))
                    }


                item {
                    userinroom()
                    Spacer(modifier = Modifier.height(30.dp))
                }

                item {
                    tasks()
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }

    }

    @Composable
    fun icon() {
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
                painter = painterResource(id = R.drawable.android),
                contentDescription = null,
                contentScale = ContentScale.Fit, // Здесь задается contentScale
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp)
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





    @Composable
    fun roomname(roomName: String) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(start = 10.dp, end = 10.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            Text(text = roomName, fontSize = 24.sp, textAlign = TextAlign.Center)
        }
    }



    @Composable
    fun userinroom() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(
                    start = 10.dp,
                    end = 10.dp
                )
                .clip(RoundedCornerShape(10.dp))
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(10) { index ->
                    // Здесь вы можете создать содержимое для каждого элемента списка
                    Text(text = "Item $index")
                }
            }
        }
    }

    @Composable
    fun tasks() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(
                    start = 10.dp,
                    end = 10.dp
                )
                .clip(RoundedCornerShape(10.dp))
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(5) { index ->
                    // Здесь вы можете создать содержимое для каждого элемента списка
                    Text(text = "Task $index")
                }
            }
        }
    }


}