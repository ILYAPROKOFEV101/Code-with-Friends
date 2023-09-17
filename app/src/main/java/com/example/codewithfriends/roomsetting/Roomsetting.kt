package com.example.codewithfriends.roomsetting

import android.content.Intent
import android.net.http.HttpResponseCache.install
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.codewithfriends.R
import com.example.codewithfriends.findroom.Room
import com.example.codewithfriends.firebase.Addtask
import com.example.codewithfriends.presentation.profile.ID
import com.example.codewithfriends.presentation.profile.IMG
import com.example.codewithfriends.presentation.profile.UID
import com.example.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.example.codewithfriends.roomsetting.ui.theme.CodeWithFriendsTheme
import com.example.codewithfriends.roomsetting.ui.theme.Participant
import com.example.reaction.logik.PreferenceHelper
import com.example.reaction.logik.PreferenceHelper.getRoomId
import com.google.android.gms.auth.api.identity.Identity
import com.google.common.reflect.TypeToken
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json


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

            // Полученные данные с сервера
            val serverData = listOf(
                TaskData(
                    gitbranch = "Master",
                    filename = "MAinActivity",
                    photo = "https://th.bing.com/th/id/OIP.FXH7Knh_9MyszJsfVnEW_wHaE5?w=272&h=180&c=7&r=0&o=5&pid=1.7",
                    mession = "Fix this bugs and change design"
                ),
                // Добавьте другие объекты TaskData по мере необходимости
            )

            val Task = remember { mutableStateOf(emptyList<TaskData>()) }

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

            Handler(Looper.getMainLooper()).postDelayed({
                getData(storedRoomId!!, rooms)
                whoinroom(storedRoomId!!, participants)
                getmession(storedRoomId!!, Task)

            }, 500) // 3000 миллисекунд (3 секунды)





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
                    userinroom(participants.value) // Передаем participants.value
                    Spacer(modifier = Modifier.height(30.dp))
                }


                item {

                    tasks(Task = serverData)  // Передаем Task.value в Composable функцию, или пустой список, если Task.value == null
                        Spacer(modifier = Modifier.height(30.dp))

                }
                item {
                    addtask()
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


    private fun getmession(roomId: String, tasksState: MutableState<List<TaskData>>) {
        val url = "https://getpost-ilya1.up.railway.app/participants/$roomId"

        val request = StringRequest(
            com.android.volley.Request.Method.GET,
            url,
            { response ->
                Log.d("Mylog", "Result: $response")
                try {
                    val gson = Gson()
                    val taskListType = object : TypeToken<List<TaskData>>() {}.type
                    val utf8Response = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
                    val newTasks: List<TaskData> = gson.fromJson(utf8Response, taskListType)

                    tasksState.value = newTasks // Обновляем состояние с полученными данными
                    Log.d("Mylog", "Received data: $newTasks") // Добавьте это
                } catch (e: JsonSyntaxException) {
                    Log.e("Mylog", "Error parsing JSON: $e")
                    Log.d("Mylog", "Server response: $response")
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
            Text(text = roomName, fontSize = 24.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(start = 10.dp, end = 10.dp))
        }
    }



    @Composable
    fun userinroom(participantsState: List<Participant>) {  // this fun mean how match users in room
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
                        Text(text = participant.username, fontSize = 24.sp, modifier = Modifier.padding(top = 30.dp))
                    }
                }
            }
        }
    }

    @Composable
    fun tasks(Task: List<TaskData>) {
        Log.d("Mylog", "Tasks: $Task") // Добавьте это
        LazyColumn( modifier = Modifier.height(700.dp)) {
            items(Task) { task ->
                TaskCard(task = task)
            }
        }
    }




    @Composable
    fun TaskCard(task: TaskData) {
        Log.d("Mylog", "TaskCard: $task") // Добавьте это
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(text = "Git Branch: ${task.gitbranch}")
                Text(text = "Filename: ${task.filename}")
                Text(text = "Mission: ${task.mession}")

                Image(
                    painter = rememberAsyncImagePainter(model = task.photo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(shape = CircleShape)
                )
            }
        }
    }




    @Composable
    fun addtask(){
        Button(
            colors = ButtonDefaults.buttonColors(Color.Blue),
            onClick = {
                val intent = Intent(this@Roomsetting, Addtask::class.java)
                startActivity(intent)

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                //.background(creatroom)

                .clip(RoundedCornerShape(1.dp))
        ) {
            Text(text = stringResource(id = R.string.Addtask),fontSize = 24.sp)
        }
    }






}