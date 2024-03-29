package com.ilya.codewithfriends.chattest.fragments



import Find_frends
import GetUserByNameService
import addsoket
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.ilya.codewithfriends.MainViewModel
import com.ilya.codewithfriends.R
import com.ilya.codewithfriends.chats.Chat
import com.ilya.codewithfriends.chats.Message
import com.ilya.codewithfriends.chattest.ChatRoomm
import com.ilya.codewithfriends.chattest.ChatScreen
import com.ilya.codewithfriends.chattest.ChatmenuContent
import com.ilya.codewithfriends.chattest.Freands
import com.ilya.codewithfriends.findroom.Getmyroom
import com.ilya.codewithfriends.findroom.Room
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.codewithfriends.presentation.sign_in.UserData
import com.ilya.codewithfriends.test.TestActivity
import com.ilya.reaction.logik.PreferenceHelper
import getUserByName

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import okhttp3.*
import org.java_websocket.client.WebSocketClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID


class FreandsFragments : Fragment() {


    var text by mutableStateOf("")

    var trans by mutableStateOf(false)

    private var user = mutableStateOf(emptyList<MyFrends>())

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = requireContext().applicationContext,
            oneTapClient = Identity.getSignInClient(requireContext().applicationContext)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )

        Find_frends("$id") { userDataList ->
            // Преобразование списка в MutableList
            user.value = userDataList.toMutableList()

            Log.d("Usernameshow", "Username: ${user.value}")


        }


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result
            Log.e("Tag", "Token -> $token")
        }




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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context = requireContext()

        // Создаем ComposeView и устанавливаем контент
        return ComposeView(requireContext()).apply {
            setContent {

                val navController = rememberNavController()
                val viewModel = viewModel<MainViewModel>()
                val isLoading by viewModel.isLoading.collectAsState()
                val swipeRefresh = rememberSwipeRefreshState(isRefreshing = isLoading)
                val id = ID(
                    userData = googleAuthUiClient.getSignedInUser()
                )
                val data_from_myroom = remember { mutableStateOf(emptyList<Room>()) }
                // Вызывайте getData только после установки ContentView
                GET_MYROOM("$id", data_from_myroom)


                NavHost(
                    navController = navController,
                    startDestination = "friends",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("friends") {
                        SwipeRefresh(
                            state = swipeRefresh,
                            onRefresh = {
                                recreate(requireActivity())
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0x3920A6FF))
                            ) {
                                if(trans == false) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    if (user.value != null) {
                                        ShowUser(user.value,  data_from_myroom.value, context,navController)
                                    }
                                }
                              //  val testActivity = TestActivity()
                               // testActivity.ButtonBar(context)
                            }
                        }
                    }
                    composable("chatmenu") {
                        // Ваш фрагмент Chatmenu
                        ChatmenuContent(navController)
                    }
                    composable("chat/{roomId}") { backStackEntry ->
                        // Получаем roomId из аргументов
                        val roomId = backStackEntry.arguments?.getString("roomId")
                        // Ваш фрагмент ChatScreen с передачей roomId
                        ChatScreen(navController, roomId ?: "")
                    }
                    composable("RoomChat/{soketId}") { backStackEntry ->
                        // Получаем roomId из аргументов
                        val soketId = backStackEntry.arguments?.getString("soketId")
                        // Ваш фрагмент ChatScreen с передачей roomId
                        ChatRoomm(navController, soketId)
                    }

                }






            }
        }
    }




    @Composable
    fun findfriends(navController: NavController){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(end = 5.dp, start = 5.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
            , elevation = 40.dp
        ) {
            IconButton(
                modifier = Modifier
                    .size(80.dp)
                    .fillMaxSize()
                    .background(Color.White),
                onClick = {
                    navController.navigate("chatmenu")
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.person_add), // Показываем иконку "forum"
                    contentDescription = "Cancel",
                    tint = Color.Blue
                )
            }
        }
    }


    @Composable
    fun ShowUser(user: List<MyFrends>, Myroom: List<Room>, context: Context, navController: NavController) {

        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )


        Box(modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)){
            findfriends(navController)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(40.dp))
        ) {

            items(Myroom) { Myroom ->
                RoomItem(Myroom, context, navController)
                Spacer(modifier = Modifier.height(10.dp))
            }
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    ){
                    Text(text = "Friends", textAlign = TextAlign.Center, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    }

            }

            items(user) { user ->
                if (user.user_id != id) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(end = 5.dp, start = 5.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.White), elevation = 40.dp
                    ) {

                        Row(
                            Modifier
                                .fillMaxSize()
                        ) {
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .width(80.dp)
                            ) {
                                Image(
                                    painter = if (!user.username.isNullOrEmpty()) {
                                        // Load image from URL
                                        rememberImagePainter(data = user.image_url)
                                    } else {
                                        // Load a default image when URL is empty
                                        painterResource(id = R.drawable.android) // Replace with your default image resource
                                    },
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(30.dp))

                                )
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text =  user.username,
                                fontSize = 24.sp,
                                modifier = Modifier.weight(0.5f),
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(0.3f)
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .align(Alignment.CenterEnd)
                                        .background(Color.White),
                                    onClick = {

                                        //trans = true
                                        navController.navigate("chat/${user.sokets}")
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.forum), // Показываем иконку "forum"
                                        contentDescription = "Cancel",
                                        tint = Color.Blue
                                    )
                                }
                            }

                        }
                    }
                    Log.d(
                        "Usernameshow",
                        "Username: ${user.username}, User ID: ${user.user_id}, Image URL: ${user.image_url}"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
    @Composable
    fun RoomItem(room: Room, context: Context, navController: NavController){
        val joinroom: Color = colorResource(id = R.color.joinroom)
        val creatroom: Color = colorResource(id = R.color.creatroom)

        androidx.compose.material3.Card(
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
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
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp, start = 10.dp)
                                .height(65.dp)
                                .clip(CircleShape)
                        ) {
                            Text(
                                text = room.roomName,
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
                            Text(
                                text = room.language, modifier = Modifier
                                    .padding(start = 10.dp),
                                style = TextStyle(fontSize = 24.sp),
                                color = Color.Black
                            )
                        }

                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .height(65.dp)
                        .fillMaxWidth()
                )
                {
                    Button(
                        onClick = {

                            navController.navigate("RoomChat/${room.id}")
                        },
                        colors = ButtonDefaults.buttonColors(creatroom),
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(20.dp),

                        ) {
                        Text(
                            text = "Join in room: ${room.placeInRoom}",
                            modifier = Modifier,
                            style = TextStyle(fontSize = 24.sp)
                        )
                    }

                }

                LazyColumn(
                    modifier = Modifier
                        .padding(start = 5.dp, end = 5.dp)
                        .fillMaxWidth()
                        .height(350.dp)
                        .clip(RoundedCornerShape(30.dp))
                ) {
                    item {
                        Text(
                            text = room.aboutRoom,
                            modifier = Modifier.padding(start = 10.dp),
                            style = TextStyle(fontSize = 24.sp),
                            color = Color.Black
                        )
                    }
                }

            }
        }
        Spacer(modifier = Modifier.height(10.dp))

    }




    fun goToChatActivity(roomId: String, context: Context) {

        PreferenceHelper.saveRoomId(context, roomId)

    }


}








