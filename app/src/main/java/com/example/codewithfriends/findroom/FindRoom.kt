package com.example.codewithfriends.findroom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.codewithfriends.R
import com.example.codewithfriends.findroom.chats.Chat

import com.example.codewithfriends.findroom.chats.PieSocketListener
import okhttp3.WebSocket


class FindRoom : ComponentActivity() {

    private val client = OkHttpClient()
   // val client: OkHttpClient =  OkHttpClient()
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
    fun RoomItem(room: Room) {

        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Blue)
        )

        {
        Row(modifier = Modifier.fillMaxSize()){

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.code),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        // .padding(10.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.Yellow)
                        .align(Alignment.Center)
                )
            }

            Column(modifier = Modifier
                .fillMaxHeight()
                .weight(0.8f)) {

                Text(text = "${room.Roomname}", modifier = Modifier, style = TextStyle(fontSize = 24.sp))

                    Text(text = "${room.Lenguage}", modifier = Modifier, style = TextStyle(fontSize = 24.sp))
                    Text(text = "Place in room: ${room.Placeinroom}", modifier = Modifier, style = TextStyle(fontSize = 24.sp))

                Button(onClick = {

                    openWebSocket(room.id)
                    val intent = Intent(this@FindRoom, Chat::class.java)
                    startActivity(intent)

                }) {
                    Text("Join Room")
                }

            }
           }
          }

        }

    private fun openWebSocket(roomId: String) {

        val request: Request = Request
        .Builder()

      //  val apiKey = "VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV"

            .url("https://getpost-ilya1.up.railway.app/chat/$roomId")
            .build()
               val listener = PieSocketListener()
        val ws: WebSocket = client.newWebSocket(request, listener)
    }


}

