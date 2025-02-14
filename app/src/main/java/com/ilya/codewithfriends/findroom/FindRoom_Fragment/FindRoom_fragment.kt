package com.ilya.codewithfriends.findroom.FindRoom_Fragment

import LoadingComponent
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.setContent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.Arrangement


import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilya.codewithfriends.APIclass.JoinDataManager

import com.ilya.codewithfriends.MainViewModel
import com.ilya.codewithfriends.Startmenu.Main_menu
import com.ilya.codewithfriends.findroom.GET_MYROOM
import com.ilya.codewithfriends.findroom.Room
import com.ilya.codewithfriends.findroom.getData

import com.ilya.codewithfriends.findroom.ui.theme.CodeWithFriendsTheme
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.test.TestActivity
import com.ilya.reaction.logik.PreferenceHelper
import com.ilya.reaction.logik.PreferenceHelper.getRoomId


class FindRoom_fragment : Fragment() {


    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = requireContext(),
            oneTapClient = Identity.getSignInClient(requireContext())
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
        super.onCreate(savedInstanceState)




        storedRoomId = PreferenceHelper.getRoomId(requireContext())

    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Создаем ComposeView и устанавливаем контент
        return ComposeView(requireContext()).apply {

            setContent {
                val id = ID(
                    userData = googleAuthUiClient.getSignedInUser()
                )
                val rooms = remember { mutableStateOf(emptyList<Room>()) }
                val data_from_myroom = remember { mutableStateOf(emptyList<Room>()) }

                // Задержка перехода на новую страницу через 3 секунды
                Handler(Looper.getMainLooper()).postDelayed({
                    getData("$id", rooms)
                }, 500) // 3000 миллисекунд (3 секунды)

                // Вызывайте getData только после установки ContentView
                GET_MYROOM("$id", data_from_myroom)
                val viewModel = viewModel<MainViewModel>()
                val isLoading by viewModel.isLoading.collectAsState()
                val swipeRefresh = rememberSwipeRefreshState(isRefreshing = isLoading)

                CodeWithFriendsTheme{
                    SwipeRefresh(
                        state = swipeRefresh,
                        onRefresh = {
                            recreate(requireActivity())

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

                            }


                    }
                }
            }
        }
    }

    val joinDataManager = JoinDataManager()




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
            delay(20000)
            notFoundVisible = true
        }

        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )


        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()) {

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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RoomItem(room: Room) {
        var show by remember {
            mutableStateOf(false)
        }
        var password by remember {
            mutableStateOf("")
        }
        var username =  PreferenceHelper.getname(requireContext())
        username = UID(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val uid = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )
        var name =  PreferenceHelper.getname(requireContext())
        val img =  PreferenceHelper.getimg(requireContext())


        Spacer(modifier = Modifier.height(30.dp))
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .height(350.dp)
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black),
            colors = CardDefaults.cardColors(
                colorScheme.background,
            ),
            ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.6f)
                    .padding(top = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
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
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f)
                            .padding(start = 20.dp, end = 5.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(Modifier.fillMaxWidth().fillMaxHeight()) {

                        Button(
                            onClick = {
                                if (room.id != null) {
                                    // Вызов функции Writepassword
                                    if (room.hasPassword) {
                                        show = !show
                                    } else {
                                        val intent = Intent(requireContext(), Chat::class.java)
                                        intent.putExtra("roomid", room.id)
                                        startActivity(intent)
                                    }
                                } else {
                                    // Обработка ситуации, когда идентификатор комнаты равен null
                                    // например, вы можете вывести сообщение об ошибке
                                    Toast.makeText(
                                        requireContext(),
                                        "Идентификатор комнаты пуст",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                                showCircle = !showCircle
                            },

                            colors = ButtonDefaults.buttonColors(
                                Color(
                                    0xFF23A3FF
                                )
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(start = 30.dp, end = 20.dp),
                            shape = RoundedCornerShape(11.dp),
                        )
                        {
                            Text(
                                text = "Зайти",
                                modifier = Modifier,
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        if (room.hasPassword) {
                            Button(
                                onClick = {
                                    joinDataManager.post_invite("$uid", room.id, "$name", "$img")
                                    Log.d("igetdatt", "$uid , ${room.id}, $name $img")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    Color(
                                        0xFF23A3FF
                                    )
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(top = 10.dp, start = 30.dp, end = 20.dp),
                                shape = RoundedCornerShape(11.dp),
                            )
                            {
                                Text(
                                    text = "Подать заявку",
                                    modifier = Modifier,
                                    style = TextStyle(fontSize = 12.sp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f)
                ) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .align(Alignment.CenterHorizontally)
                    ) {
                        showluck(show = room.hasPassword)

                    }
                    Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = room.roomName,
                            modifier = Modifier,
                            style = TextStyle(fontSize = 24.sp),
                            color = colorScheme.onBackground
                        )

                    Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Мест в команде: ${room.placeInRoom}",
                            modifier = Modifier,
                            style = TextStyle(fontSize = 20.sp),
                            color = colorScheme.onBackground
                        )

                    Spacer(modifier = Modifier.height(2.dp))
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                    )
                        {
                            Text(
                                text = room.language,
                                modifier = Modifier,
                                style = TextStyle(fontSize = 24.sp),
                                color = colorScheme.onBackground
                            )
                        }
                    Spacer(modifier = Modifier.height(2.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(300.dp)) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(30.dp))
                    ) {
                        item {
                            Text(
                                text = room.aboutRoom,
                                modifier = Modifier.padding(start = 10.dp),
                                style = TextStyle(fontSize = 24.sp),
                                color = colorScheme.onBackground
                            )
                        }
                    }
                }

                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        if(show) {
            AlertDialog(
                modifier = Modifier.clip(RoundedCornerShape(30.dp)),
                onDismissRequest = { /* ... */ },
                buttons = {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                    ) {
                        TextField(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .padding(5.dp)
                                .border(
                                    2.dp, Color.Blue,
                                    shape = RoundedCornerShape(30.dp)
                                )
                                .clip(RoundedCornerShape(30.dp)),
                            value = password, // Текущее значение текста в поле
                            onValueChange = {
                                password = it
                            }, // Обработчик изменения текста, обновляющий переменную "text"
                            textStyle = TextStyle(fontSize = 24.sp),

                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent, // Цвет индикатора при фокусе на поле (прозрачный - отключает индикатор)
                                unfocusedIndicatorColor = Color.Transparent, // Цвет индикатора при потере фокуса на поле (прозрачный - отключает индикатор)
                                disabledIndicatorColor = Color.Transparent, // Цвет индикатора, когда поле неактивно (прозрачный - отключает индикатор)
                                containerColor = Color.White
                            ),


                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done, // Действие на кнопке "Готово" на клавиатуре (закрытие клавиатуры)
                                keyboardType = KeyboardType.Text // Тип клавиатуры (обычный текст)
                            ),

                            keyboardActions = KeyboardActions(
                                onDone = {
                                    joinDataManager.pushData_join(room.id, "$uid", "$username", password){ success ->

                                    }
                                    show = !show
                                }
                            ),
                        )
                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .padding(5.dp)
                            .height(50.dp),
                            colors = ButtonDefaults.buttonColors(Color.Green),
                            onClick = {
                                joinDataManager.pushData_join(room.id,"$uid", "$username",password){ success ->

                                }
                                val intent = Intent(requireContext(), Chat::class.java)
                                intent.putExtra("roomid", room.id)
                                startActivity(intent)
                                show = !show

                            }
                        ) {
                            Text(text = "Join in room", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .padding(5.dp)
                            .height(50.dp),
                            colors = ButtonDefaults.buttonColors(Color.Red),
                            onClick = {
                                show = !show
                            }
                        ) {
                            Text(text = "No", fontSize = 24.sp)
                        }

                    }
                })

        }

    }

    @Composable
    fun showluck(show: Boolean){
            if (show) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .size(20.dp),
                    painter = painterResource(id = R.drawable.lock),
                    contentDescription = "lock",
                    tint = colorScheme.onBackground,
                    // Цвет иконки
                )
            } else {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .size(20.dp),
                    painter = painterResource(id = R.drawable.lock24px),
                    contentDescription = "open",
                    tint = colorScheme.onBackground,

                )
            }
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





}