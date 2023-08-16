package com.example.codewithfriends.findroom

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

import okhttp3.OkHttpClient
import okhttp3.Request

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

import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.codewithfriends.R
import com.example.codewithfriends.findroom.chats.Chat


import com.example.codewithfriends.findroom.chats.PieSocketListener
import com.example.codewithfriends.presentation.sign_in.UserData
import com.example.reaction.logik.PreferenceHelper
import com.example.reaction.logik.PreferenceHelper.saveRoomId
import okhttp3.WebSocket


class FindRoom : ComponentActivity() {

    private val client = OkHttpClient()

   private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            val rooms = remember { mutableStateOf(emptyList<Room>()) }
            Box(modifier = Modifier.fillMaxSize()) {

                RoomList(rooms.value)
            }
            getData(rooms)


        }
    }

    private fun getData(rooms: MutableState<List<Room>>) {
        val url = "https://getpost-ilya1.up.railway.app/data"

        val request = StringRequest(
            com.android.volley.Request.Method.GET,
            url,
            { response ->
                Log.d("Mylog", "Result: $response")
                val gson = Gson()
                val roomListType = object : TypeToken<List<Room>>() {}.type
                val utf8Response = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
                val newRooms: List<Room> = gson.fromJson(utf8Response, roomListType)

                rooms.value = newRooms
            },
            { error ->
                Log.d("Mylog", "Error: $error")
            }
        )

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
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

val gg = ""
        Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .height(500.dp)
                .border(
                    border = BorderStroke(5.dp, SolidColor(joinroom)),
                    shape = RoundedCornerShape(30.dp)
                )
            ){
                Column(modifier = Modifier.fillMaxSize())
                {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp))
                    {
                        Box(
                            modifier = Modifier
                                .weight(0.3f)
                                .align(Alignment.CenterVertically)
                        )
                        {
                            Image(
                                painter = painterResource(id = R.drawable.android),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 5.dp, end = 5.dp)
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(50.dp))
                                    .align(Alignment.Center)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.7f)
                                .padding(end = 5.dp)
                              ){
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .height(65.dp)
                                .clip(CircleShape)
                                ) {
                                Text(text = "${room.roomName}", modifier = Modifier.padding(top = 10.dp , start = 10.dp), style = TextStyle(fontSize = 24.sp))
                            }
                            
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .height(65.dp)
                                .clip(CircleShape)
                            ) {
                                Text(text = "${room.language}", modifier = Modifier.padding( start = 10.dp), style = TextStyle(fontSize = 24.sp))
                            }

                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier
                        .height(65.dp)
                        .fillMaxWidth())
                    {
                        Box(modifier = Modifier
                            .padding(start = 5.dp, end = 5.dp)
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .fillMaxHeight(1f))
                        {
                            Button(onClick = {
                                goToChatActivity(room.id)
                                openWebSocket(room.id)
                                val intent = Intent(this@FindRoom, Chat::class.java)
                                startActivity(intent)
                            },
                                colors = ButtonDefaults.buttonColors(creatroom),
                                modifier = Modifier.fillMaxSize()
                                  ) {
                                Text(text = "Join in room: ${room.placeInRoom}", modifier = Modifier, style = TextStyle(fontSize = 24.sp))

                            }
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
    }



    private fun openWebSocket(roomId: String) {
        val request: Request = Request.Builder()
            .url("https://getpost-ilya1.up.railway.app/chat/$roomId")
            .build()
        val listener = PieSocketListener()
        val ws: WebSocket = client.newWebSocket(request, listener)

    }
    fun goToChatActivity(roomId: String) {

        saveRoomId(this, roomId)

    }





}

