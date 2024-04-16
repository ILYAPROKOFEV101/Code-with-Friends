package com.ilya.codewithfriends.chattest.fragments



import GetUserByNameService
import addsoket
import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.ilya.codewithfriends.chats.Message
import com.ilya.codewithfriends.chattest.ChatScreen
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.codewithfriends.presentation.sign_in.UserData
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID


class Chatmenu : Fragment() {


    var text by mutableStateOf("")
    var roomid by mutableStateOf("")
    var trans by mutableStateOf(false)

    private var user = mutableStateOf(emptyList<newUserData>())

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = requireContext().applicationContext,
            oneTapClient = Identity.getSignInClient(requireContext().applicationContext)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            val token = task.result
            Log.e("Tag", "Token -> $token")
        }

        val name = UID(
            userData = googleAuthUiClient.getSignedInUser()
        )

        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val storedRoomId = arguments?.getString("STORED_ROOM_ID_KEY")


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                            finduser()
                            Spacer(modifier = Modifier.height(20.dp))
                            if (user.value != null) {
                                ShowUser(user.value, "$id")
                            }
                        }

                    }
                }
            }
        }
    }



    private fun showToast(message: String, context: Context) {
        // Вывести Toast с заданным сообщением
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    fun generateUniqueId(): String {
        val characters = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return List(15) { characters.random() }.joinToString("")
    }


    @Composable
    fun ShowUser(user: List<newUserData>, myid: String) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(40.dp))
        ) {
            items(user) { user ->
                var clicked by remember { mutableStateOf(false) }
                val uniqueId = generateUniqueId()
                if (user.userId != myid) {


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
                                        rememberImagePainter(data = user.imageUrl)
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
                                text = user.username,
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

                                        if (clicked == false) {
                                            addsoket("${user.userId}", uniqueId, "$myid")
                                        } else {
                                            // roomid = uniqueId
                                            trans = true
                                        }
                                        clicked = true // Устанавливаем флаг нажатия в true
                                    }
                                ) {
                                    Icon(
                                        painter = if (clicked) {
                                            painterResource(id = R.drawable.forum) // Показываем иконку "forum"
                                        } else {
                                            painterResource(id = R.drawable.person_add) // Показываем иконку "person_add"
                                        },
                                        contentDescription = "Cancel",
                                        tint = Color.Blue
                                    )
                                }
                            }

                        }
                    }
                    Log.d(
                        "Usernameshow",
                        "Username: ${user.username}, User ID: ${user.userId}, Image URL: ${user.imageUrl}"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun finduser() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
                .height(80.dp)
                .border(2.dp, Color.Blue, RoundedCornerShape(30.dp))

        )
        {
            val keyboardControllers = LocalSoftwareKeyboardController.current
            var showtext by remember {
                mutableStateOf(false)
            }

            TextField(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.8f),
                value = text, // Текущее значение текста в поле
                onValueChange = {
                    text = it
                }, // Обработчик изменения текста, обновляющий переменную "text"
                textStyle = TextStyle(fontSize = 24.sp),
                // textStyle = TextStyle.Default, // Стиль текста, используемый в поле ввода (используется стандартный стиль)

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
                        keyboardControllers?.hide() // Обработчик действия при нажатии на кнопку "Готово" на клавиатуре (скрыть клавиатуру)
                        if (text != "") {
                            showtext = !showtext
                        }
                    }
                ),
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(80.dp)

            ) {
                IconButton(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.CenterEnd)
                        .background(Color.White),
                    onClick = {
                        getUserByName("$text") { userDataList ->
                            // Преобразование списка в MutableList
                            user.value = userDataList.toMutableList()

                            Log.d("Usernameshow", "Username: ${user.value}")


                        }
                    }
                )
                {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cancel",
                        tint = Color.Black
                    )
                }
            }
        }
    }




}








