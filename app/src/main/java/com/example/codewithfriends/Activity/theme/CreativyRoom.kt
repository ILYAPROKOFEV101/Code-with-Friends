package com.example.codewithfriends.Activity.theme

import android.net.Uri
import android.os.Bundle
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
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
import com.example.codewithfriends.R
import com.example.codewithfriends.presentation.profile.ID
import com.example.codewithfriends.presentation.profile.UID
import com.example.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.example.reaction.logik.PreferenceHelper
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.net.URL
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.UUID
data class JsonRequest(val url: String)



class CreativyRoom : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    var text by mutableStateOf("")
    var texts by mutableStateOf("")
    var photo by mutableStateOf("")
    var showCircle by mutableStateOf(true)




    val languages = listOf(
        "android development",
        "ios development",
        "Web development",
        "Game development",
        "C++ Software",
        "Machine learning "
    )
    var selectedLanguage = ""

    val places = (2..5).toList()

    var selectedPlace = 1
    val selectedNumber = 1
  //  val uniqueAdmin = ""

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



            LazyColumn {
                item {
                    Creator()
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }

                item {
                    LanguagePicker()
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }

                item {
                    PlaceInRoomPicker()
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }

                item {
                    WriteoboutRoom()
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }


                item {
                    WriteDb()
                    Spacer(modifier = Modifier.height(30.dp))
                }
                item {
                    AddImage()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }


        }

    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Preview(showBackground = true)
    @Composable
    fun Creator() {
        val keyboardController = LocalSoftwareKeyboardController.current
        var textSize by remember { mutableStateOf(24.sp) } // Состояние для хранения размера текста
        var show by remember {
            mutableStateOf(false) }

        var  name by remember {
            mutableStateOf("")
        }



        Box(
            Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(top = 30.dp, start = 10.dp, end = 10.dp)
                .clip(RoundedCornerShape(30.dp))
        ) {
            TextField(modifier = Modifier.fillMaxSize(),
                value = text, // Текущее значение текста в поле
                onValueChange = {
                    text = it
                }, // Обработчик изменения текста, обновляющий переменную "text"
                textStyle = TextStyle(fontSize = textSize),


                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.White, // Цвет индикатора при фокусе на поле (прозрачный - отключает индикатор)
                    unfocusedIndicatorColor = Color.White, // Цвет индикатора при потере фокуса на поле (прозрачный - отключает индикатор)
                    disabledIndicatorColor = Color.White, // Цвет индикатора, когда поле неактивно (прозрачный - отключает индикатор)
                    containerColor = Color.White
                ),

                label = { // Метка, которая отображается над полем ввода

                    Text(
                        text = if (!show) stringResource(id = R.string.Nameofroom) else "",
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
                        keyboardController?.hide() // Обработчик действия при нажатии на кнопку "Готово" на клавиатуре (скрыть клавиатуру)

                        if (text != "") {
                            show = !show
                        }

                    }
                ),
            )

        }



    }
@Preview(showBackground = true)
    @Composable
    fun LanguagePicker() {
        var expanded by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                .clip(RoundedCornerShape(30.dp))
                .clickable { expanded = true }
        ) {
            if (selectedLanguage.isEmpty()) {
                Text(
                    text = "Выберите язык программирования",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center, // Здесь задаем выравнивание по центру
                    color = Color.Black
                )
            } else {
                Text(
                    text = selectedLanguage,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center, // Здесь задаем выравнивание по центру
                    color = Color.Black, modifier = Modifier.align(Alignment.Center)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }, modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.White)
            ) {
                languages.forEach { language ->
                    DropdownMenuItem(onClick = {
                        selectedLanguage = language
                        expanded = false
                    }) {
                        Text(
                            text = language,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun PlaceInRoomPicker() {
        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                .clip(RoundedCornerShape(30.dp))
                .clickable { expanded = true }
        ) {
            Text(
                text = if(selectedPlace < 2)stringResource(id = R.string.placeinroom) + " $selectedPlace" else "$selectedPlace",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Color.Black, modifier = Modifier.align(Alignment.Center)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.White)
            ) {
                places.forEach { place ->
                    DropdownMenuItem(onClick = {
                        selectedPlace = place
                        selectedPlace = place
                        expanded = false
                        val selectedNumber = selectedPlace.toString()
                    }) {
                        Text(
                            text = place.toString(),
                            fontSize = 24.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun WriteoboutRoom(){
        val keyboardControllers = LocalSoftwareKeyboardController.current
        var showtext by remember {
            mutableStateOf(false) }
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 10.dp, end = 10.dp)
            .clip(RoundedCornerShape(30.dp))
            .height(300.dp)
        ){
            TextField(modifier = Modifier.fillMaxSize(),
                value = texts, // Текущее значение текста в поле
                onValueChange = { texts = it }, // Обработчик изменения текста, обновляющий переменную "text"
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
                        text = if (!showtext) stringResource(id = R.string.aboutroom) else "",
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
                        if (texts != "") {
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



    private fun pushData(
        uniqueAdmin: String? = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )
    ) {
        val baseUrl = "https://getpost-ilya1.up.railway.app/user"
        val url = "$baseUrl?id=$uniqueId&Lenguage=$selectedLanguage&Placeinroom=$selectedPlace&Roomname=$text&Aboutroom=$texts&Admin=$uniqueAdmin"

        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()

        val json = """
        {
            "url": "$photo"
        }
    """.trimIndent()

        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    // Обработка успешного ответа сервера
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }






    @Composable
    fun WriteDb(){
        val coroutineScope = rememberCoroutineScope()
        Button(onClick = {
            coroutineScope.launch {
                pushData()
            }

        },modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            ,colors = ButtonDefaults.buttonColors(Color.Yellow),) {
            Text(text = "загрузить на сервер",fontSize = 24.sp)
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
                    Text(text = stringResource(id = R.string.Icon), fontSize = 24.sp)
                } else {

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(text = " Дождитесь загрузки !!! ", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        LoadingCircle()
                    }
                }



            }

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
                    photo = uri.toString()
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
        Toast.makeText(this@CreativyRoom, message, Toast.LENGTH_SHORT).show()
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

}