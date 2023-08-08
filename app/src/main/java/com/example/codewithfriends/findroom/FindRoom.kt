package com.example.codewithfriends.findroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.codewithfriends.findroom.ui.theme.CodeWithFriendsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.serialization.json.Json
import java.net.URL


class FindRoom : ComponentActivity() {

    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

                    MainScreen()

        }
    }


    suspend fun fetchRooms(): List<Room> {
        return withContext(Dispatchers.IO) {
            val url = "https://getpost-ilya1.up.railway.app/data"
            val json = URL(url).readText()
            Json.decodeFromString(json)
        }
    }

    @Composable
    fun RoomItem(room: Room) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "ID: ${room.id}, Название: ${room.Roomname}, Язык: ${room.Lenguage}, Место в комнате: ${room.Placeinroom}",
                modifier = Modifier.padding(8.dp)
            )
        }
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
    fun MainScreen() {
        val rooms = remember { mutableStateOf(emptyList<Room>()) }

        LaunchedEffect(Unit) {
            rooms.value = fetchRooms()
        }

        RoomList(rooms = rooms.value)
    }


}



