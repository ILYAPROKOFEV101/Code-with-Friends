package com.ilya.codewithfriends.Activity.CreatyActivity

import android.content.Intent
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.ilya.codewithfriends.R

import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.reaction.logik.PreferenceHelper
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.storage.FirebaseStorage
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.roomsetting.Roomsetting

import kotlinx.coroutines.launch

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    var password by mutableStateOf("")
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

    var selectedPlace = 2
    var selectedNumber = 0
  //  val uniqueAdmin = ""

    private var storedRoomId: String? = null // Объявляем на уровне класса

    private var pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedImageUri ->
            // Здесь вы можете загрузить изображение в Firebase Storage
            uploadImageToFirebaseStorage(selectedImageUri)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )


        getNumberFromServer("$id")
        storedRoomId = PreferenceHelper.getRoomId(this)

        setContent {



            LazyColumn {

                item {
                    AddImage()
                    Spacer(modifier = Modifier.height(20.dp))
                }

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
                    WriteoboutPassword()
                    Spacer(modifier = Modifier.height(30.dp))
                }


                item {
                    WriteDb("$id")
                    Spacer(modifier = Modifier.height(30.dp))
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


        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
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
                        text = if (!show) stringResource(id = R.string.name) else "",
                        fontSize = 24.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
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

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(30.dp))
                .clickable { expanded = true },
            colors = CardDefaults.cardColors(Color.White),
        ) {
            if (selectedLanguage.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.chooselenguage),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center, // Здесь задаем выравнивание по центру
                    color = Color.Black, modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = selectedLanguage,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center, // Здесь задаем выравнивание по центру
                    color = Color.Black
                    , modifier = Modifier.fillMaxWidth()
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }, modifier = Modifier

                    .background(Color.White)
            ) {
                languages.forEach { language ->
                    DropdownMenuItem(
                       // modifier = Modifier.fillMaxSize().background(Color.White),
                        onClick = {
                        selectedLanguage = language
                        expanded = false
                    }) {
                        Text(
                            text = language,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun PlaceInRoomPicker() {
        var expanded by remember { mutableStateOf(false) }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(30.dp))
                .clickable { expanded = true },
            colors = CardDefaults.cardColors(Color.White),
        ) {
            Text(
                text = if(selectedPlace < 2)stringResource(id = R.string.placeinroom) + " $selectedPlace" else "$selectedPlace",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Color.Black, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(30.dp)),
            colors = CardDefaults.cardColors(Color.White),
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
                        textAlign = TextAlign.Center , modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
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


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun WriteoboutPassword(){
        val keyboardControllers = LocalSoftwareKeyboardController.current
        var showtext by remember {
            mutableStateOf(false) }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(30.dp))
                .border(width = 2.dp, color = MaterialTheme.colorScheme.secondary, RoundedCornerShape(30.dp))
            ,
            colors = CardDefaults.cardColors(Color.White),
        ){
            TextField(modifier = Modifier.fillMaxSize(),
                value = password, // Текущее значение текста в поле
                onValueChange = { password = it }, // Обработчик изменения текста, обновляющий переменную "text"
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
                        text = if (!showtext) stringResource(id = R.string.password) else "",
                        fontSize = 30.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center , modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
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
        var username =  PreferenceHelper.getname(this)
        username = UID(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val baseUrl = "https://getpost-ilya1.up.railway.app/user"
        val url = "$baseUrl?id=$uniqueId&Lenguage=$selectedLanguage&Placeinroom=$selectedPlace&Roomname=$text&Aboutroom=$texts&Admin=$uniqueAdmin&Username=$username"

        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()

        val json = """
        {
            "url": "$photo",
            "password": "${if(password != "") {password} else {null}}"
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
    fun WriteDb(uid: String){
        val coroutineScope = rememberCoroutineScope()

        Button(

            onClick = {
               /// if(selectedNumber <= 0) {
                    if (
                        uniqueId != "" &&
                        selectedLanguage != "" &&
                        selectedPlace != null && text != "" && texts != "" && photo != ""
                    ) {
                        sendPostRequest(uid)
                        PreferenceHelper.saveRoomId(this, uniqueId)
                        coroutineScope.launch {
                            pushData()
                        }
                        intent = Intent(this@CreativyRoom, Roomsetting::class.java)
                        startActivity(intent)

                    } else {
                        showToast(getString(R.string.datainbalank))
                    }
                /*}else {
                    showToast(getString(R.string.myroom))
                }*/

        },modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(start = 5.dp, end = 5.dp)
            ,colors = ButtonDefaults.buttonColors(Color.Blue),
            shape = RoundedCornerShape(20.dp),
            ) {
            Text(text = stringResource(id = R.string.create),fontSize = 24.sp)
        }



    }
    @Composable
    fun AddImage() {

        var addimg by remember {
            mutableStateOf(false)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {

            if (photo.isNotEmpty()){
                addimg = true
            }
            if (addimg == false){
            Button(
                colors = ButtonDefaults.buttonColors(Color.White),
                onClick = {
                    // Запуск активности выбора изображения
                    pickImage.launch("image/*")
                    showCircle = !showCircle
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(bottom = 15.dp, top = 15.dp, start = 70.dp, end = 70.dp)

                    //.alpha(0f) // Устанавливаем полную прозрачность кнопке
                    .border(
                        2.dp, Color.Blue,
                        shape = RoundedCornerShape(180.dp)
                    ) // Добавляем бордер шириной 1dp и черного цвета
                    .clip(RoundedCornerShape(180.dp)),
                shape = RoundedCornerShape(180.dp), // Применяем закругленные углы к Card
            ) {
                if (showCircle) {
                    Text(
                        text = stringResource(id = R.string.Icon),
                        fontSize = 24.sp,
                        style = TextStyle(color = Color.Blue)
                    )
                } else {

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = " Дождитесь загрузки !!! ",
                            fontSize = 24.sp,
                            style = TextStyle(color = Color.Blue)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        LoadingCircle()
                    }
                }
            }
            } else {
                Image(
                    painter = if (photo.isNotEmpty()) {
                        // Load image from URL
                        rememberImagePainter(data = photo)
                    } else {
                        // Load a default image when URL is empty
                        painterResource(id = R.drawable.android) // Replace with your default image resource
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(bottom = 15.dp, top = 15.dp, start = 70.dp, end = 70.dp)

                        //.alpha(0f) // Устанавливаем полную прозрачность кнопке
                        .border(
                            2.dp, Color.Blue,
                            shape = RoundedCornerShape(180.dp)
                        ) // Добавляем бордер шириной 1dp и черного цвета
                        .clip(RoundedCornerShape(180.dp))
                        .clickable {
                            addimg = false
                            showCircle = false
                            photo = ""
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }


    fun sendPostRequest(uid : String) {
        // Создайте экземпляр Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создайте экземпляр службы API
        val apiService = retrofit.create(Post::class.java)



        // Отправьте POST-запрос с передачей roomId в качестве параметра пути
        val call = apiService.Sanduser("$uid")
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

    // Функция для выполнения запроса к серверу и обработки ответа и ошибок
        fun getNumberFromServer(uid: String) {
        // Создание объекта Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/") // Замените BASE_URL_OF_YOUR_SERVER на базовый URL вашего сервера
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Инициализация объекта ApiService
        val apiService: ApiService = retrofit.create(ApiService::class.java)

        // Выполнение запроса к серверу
        val call: Call<Int> = apiService.getNumber(uid)

        call.enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    val number: Int = response.body() ?: 0 // Получаем число с сервера (0, 1)
                    // Данные успешно получены с сервера. Вызовите функцию onSuccess и передайте значение number.
                    selectedNumber = number
                } else {
                    // Ошибка: сервер вернул неуспешный статус код. Вызовите функцию onError с сообщением об ошибке.

                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                // Ошибка при выполнении запроса к серверу. Вызовите функцию onError и передайте сообщение об ошибке.

            }
        })
    }


    private fun uploadImageToFirebaseStorage(selectedImageUri: Uri) {


        
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        // Create a unique name for the image to avoid overwriting
        val imageName = UUID.randomUUID().toString()

        // Path to store the image: "images/{roomid}/{imageName}"
        val imageRef = storageRef.child("images/$uniqueId/$imageName")

        var uploadTask = imageRef.putFile(selectedImageUri)

        // Add a listener to handle successful or unsuccessful upload
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Get the download URL from the task result
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    photo = uri.toString()
                    // Do something with the URL, such as save it to Firestore

                    // Покажите Toast об успешной загрузке
                    showToast(getString(R.string.addPhoto))
                    showCircle = true

                }
            } else {
                // Handle unsuccessful upload

                // Покажите Toast об ошибке загрузки
                showToast(getString(R.string.upload_error_message))            }
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
            .background(Color.Blue)

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
                        .background(Color.White)

                )
            }
        }
    }

}