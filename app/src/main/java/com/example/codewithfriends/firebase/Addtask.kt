package com.example.codewithfriends.firebase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.Request
import com.android.volley.toolbox.HttpResponse
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.codewithfriends.R
import com.example.codewithfriends.findroom.FindRoom
import com.example.codewithfriends.findroom.Room

import com.example.codewithfriends.firebase.ui.theme.CodeWithFriendsTheme
import com.example.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.example.reaction.logik.PreferenceHelper
import com.google.android.gms.auth.api.identity.Identity
import com.google.common.reflect.TypeToken
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.ContentType
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import java.util.UUID


import kotlinx.coroutines.withContext

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Addtask : ComponentActivity() {

    private val client = OkHttpClient()


    var gitbreanch by mutableStateOf("")
    var filename by mutableStateOf("")
    var mession by mutableStateOf("")
    var photo by mutableStateOf("")
    var h = 80.dp
    var showCircle by mutableStateOf(true)

    // Глобальная переменная для хранения URL
    private var imageUrl: Uri? = null

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private var storedRoomId: String? = null // Объявляем на уровне класса

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedImageUri ->
            // Здесь вы можете загрузить изображение в Firebase Storage
            uploadImageToFirebaseStorage(selectedImageUri, storedRoomId!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        storedRoomId = PreferenceHelper.getRoomId(this)

        setContent {
            CodeWithFriendsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()){
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            addgitbreanch()
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            filename()
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            AddImage()

                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            whatineedtodo()
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            addtask(storedRoomId!!)
                        }

                    }

                }
            }
        }
    }
    @Preview(showBackground = true)
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun addgitbreanch(){
        val keyboardControllers = LocalSoftwareKeyboardController.current
        var showtext by remember {
            mutableStateOf(false) }

        Spacer(modifier = Modifier.height(50.dp))

        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 10.dp, end = 10.dp)
            .clip(RoundedCornerShape(30.dp))
            .height(200.dp)
        ){
            TextField(modifier = Modifier.fillMaxSize(),
                value = gitbreanch, // Текущее значение текста в поле
                onValueChange = { gitbreanch = it }, // Обработчик изменения текста, обновляющий переменную "text"
                textStyle = TextStyle(fontSize = 24.sp),
                // textStyle = TextStyle.Default, // Стиль текста, используемый в поле ввода (используется стандартный стиль)

                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // Цвет индикатора при фокусе на поле (прозрачный - отключает индикатор)
                    unfocusedIndicatorColor = Color.Transparent, // Цвет индикатора при потере фокуса на поле (прозрачный - отключает индикатор)
                    disabledIndicatorColor = Color.Transparent, // Цвет индикатора, когда поле неактивно (прозрачный - отключает индикатор)
                    containerColor = Color.White
                ),
                label = { // Метка, которая отображается над полем ввода
                    Text(
                        text = if (!showtext) stringResource(id = R.string.breanch) else "",
                        fontSize = 30.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                },

                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, // Действие на кнопке "Готово" на клавиатуре (закрытие клавиатуры)
                    keyboardType = KeyboardType.Text // Тип клавиатуры (обычный текст)
                ),

                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardControllers?.hide() // Обработчик действия при нажатии на кнопку "Готово" на клавиатуре (скрыть клавиатуру)
                        if (gitbreanch != "") {
                            showtext = !showtext
                        }

                    }
                ),

                )
        }

    }

    @Preview(showBackground = true)
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun filename(){
        val keyboardControllers = LocalSoftwareKeyboardController.current
        var showtext by remember {
            mutableStateOf(false) }

        Spacer(modifier = Modifier.height(50.dp))

        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 10.dp, end = 10.dp)
            .clip(RoundedCornerShape(30.dp))
            .height(200.dp)
        ){
            TextField(modifier = Modifier.fillMaxSize(),
                value = filename, // Текущее значение текста в поле
                onValueChange = { filename = it }, // Обработчик изменения текста, обновляющий переменную "text"
                textStyle = TextStyle(fontSize = 24.sp),
                // textStyle = TextStyle.Default, // Стиль текста, используемый в поле ввода (используется стандартный стиль)

                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // Цвет индикатора при фокусе на поле (прозрачный - отключает индикатор)
                    unfocusedIndicatorColor = Color.Transparent, // Цвет индикатора при потере фокуса на поле (прозрачный - отключает индикатор)
                    disabledIndicatorColor = Color.Transparent, // Цвет индикатора, когда поле неактивно (прозрачный - отключает индикатор)
                    containerColor = Color.White
                ),
                label = { // Метка, которая отображается над полем ввода
                    Text(
                        text = if (!showtext) stringResource(id = R.string.filename) else "",
                        fontSize = 30.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                },

                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, // Действие на кнопке "Готово" на клавиатуре (закрытие клавиатуры)
                    keyboardType = KeyboardType.Text // Тип клавиатуры (обычный текст)
                ),

                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardControllers?.hide() // Обработчик действия при нажатии на кнопку "Готово" на клавиатуре (скрыть клавиатуру)
                        if (filename != "") {
                            showtext = !showtext
                        }

                    }
                ),
            )
        }

    }


    @Composable
    fun AddImage() {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
        Button(
            colors = ButtonDefaults.buttonColors(Color.Blue),
            onClick = {
                // Запуск активности выбора изображения
                pickImage.launch("image/*")
                showCircle = !showCircle
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(1.dp))
        ) {
            if(showCircle){
                Text(text = stringResource(id = R.string.Photo), fontSize = 24.sp)
            } else {

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = " Дождитесь загрузки !!! ", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    LoadingCircle()
                }
            }



        }
            /*if(!showCircle){
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)){
                    h = 40.dp


                }


            }
*/
    }

    }

    @Preview(showBackground = true)
    @Composable
    fun LoadingCircle() {
        Box(  modifier = Modifier
            .height(40.dp)


            .wrapContentSize(Alignment.Center)
        ) {


            val rotation = rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(40.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Blue)
                    //.rotate(rotation.value)
                )
            }
        }
    }

    @Preview(showBackground = true)
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun whatineedtodo(){
        val keyboardControllers = LocalSoftwareKeyboardController.current
        var showtext by remember {
            mutableStateOf(false) }

        Spacer(modifier = Modifier.height(50.dp))

        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 10.dp, end = 10.dp)
            .clip(RoundedCornerShape(30.dp))
            .height(200.dp)
        ){
            TextField(modifier = Modifier.fillMaxSize(),
                value = mession, // Текущее значение текста в поле
                onValueChange = { mession = it }, // Обработчик изменения текста, обновляющий переменную "text"
                textStyle = TextStyle(fontSize = 24.sp),
                // textStyle = TextStyle.Default, // Стиль текста, используемый в поле ввода (используется стандартный стиль)

                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // Цвет индикатора при фокусе на поле (прозрачный - отключает индикатор)
                    unfocusedIndicatorColor = Color.Transparent, // Цвет индикатора при потере фокуса на поле (прозрачный - отключает индикатор)
                    disabledIndicatorColor = Color.Transparent, // Цвет индикатора, когда поле неактивно (прозрачный - отключает индикатор)
                    containerColor = Color.White
                ),
                label = { // Метка, которая отображается над полем ввода
                    Text(
                        text = if (!showtext) stringResource(id = R.string.mission) else "",
                        fontSize = 30.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                },

                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done, // Действие на кнопке "Готово" на клавиатуре (закрытие клавиатуры)
                    keyboardType = KeyboardType.Text // Тип клавиатуры (обычный текст)
                ),

                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardControllers?.hide() // Обработчик действия при нажатии на кнопку "Готово" на клавиатуре (скрыть клавиатуру)
                        if (mession != "") {
                            showtext = !showtext
                        }

                    }
                ),

                )
        }



    }
    fun generateUniqueId(): String {
        val characters = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return List(10) { characters.random() }.joinToString("")
    }

    val uniqueId = generateUniqueId()

    @Composable
    fun addtask(roomid: String){
        Button(
            colors = ButtonDefaults.buttonColors(Color.Blue),
            onClick = {
                val database = Firebase.database("https://code-with-friends-73cde-default-rtdb.europe-west1.firebasedatabase.app/")
                // val database = Firebase.database(stringResource(id = R.string.DataBase))
                val myRef = database.getReference("$roomid")

                val values = mapOf(
                    "gitbranch" to gitbreanch,
                    "filename" to filename,
                    "photo" to photo,
                    "mession" to mession,
                    "id" to uniqueId
                )

                    sendPostRequest("$roomid", photo, gitbreanch, filename, mession, uniqueId)
                
                myRef.setValue(values)
            }
            ,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)


                .clip(RoundedCornerShape(1.dp))
        ) {
            Text(text = stringResource(id = R.string.Addtask),fontSize = 24.sp)
        }
    }



    private fun uploadImageToFirebaseStorage(selectedImageUri: Uri, roomid: String) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        // Create a unique name for the image to avoid overwriting
        val imageName = UUID.randomUUID().toString()

        // Path to store the image: "images/{roomid}/{imageName}"
        val imageRef = storageRef.child("images/$roomid/$imageName")

        val uploadTask = imageRef.putFile(selectedImageUri)

        // Add a listener to handle successful or unsuccessful upload
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Get the download URL from the task result
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    // Сохраняем URL изображения в глобальной переменной photo
                    photo = downloadUrl

                    // Do something with the URL, such as save it to Firestore

                    // Покажите Toast об успешной загрузке
                    showToast("Фотография успешно загружена!")
                    showCircle = true
                }
            } else {
                // Handle unsuccessful upload

                // Покажите Toast об ошибке загрузки
                showToast("Ошибка при загрузке фотографии. Пожалуйста, попробуйте еще раз.")
            }
        }
    }


    private fun showToast(message: String) {
        // Вывести Toast с заданным сообщением
        Toast.makeText(this@Addtask, message, Toast.LENGTH_SHORT).show()
    }




    fun sendPostRequest(roomId: String, imageUrl: String, gitbranch: String, filename: String, mession: String, id: String) {
        // Создайте экземпляр Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создайте экземпляр службы API
        val apiService = retrofit.create(ApiService::class.java)

        // Создайте объект TaskRequest
        val request = TaskRequest(gitbranch, filename, imageUrl, mession, id)

        // Отправьте POST-запрос с передачей roomId в качестве параметра пути
        val call = apiService.sendTaskRequest(roomId, request)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    // Запрос успешно отправлен
                    // Можете выполнить какие-либо дополнительные действия здесь
                } else {
                    // Обработайте ошибку, если есть
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Обработайте ошибку при отправке запроса
            }
        })
    }








}