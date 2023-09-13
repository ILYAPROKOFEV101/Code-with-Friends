package com.example.codewithfriends.firebase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import java.util.UUID

class Addtask : ComponentActivity() {

    private val client = OkHttpClient()


    var gitbreanch by mutableStateOf("")
    var filename by mutableStateOf("")
    var mession by mutableStateOf("")

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
                            addgitbreanch()
                        }
                        item {
                            filename()
                        }
                        item {
                            AddImage()
                        }
                        item {
                            whatineedtodo()
                        }
                        item {
                            addtask(storedRoomId!!, "$imageUrl")
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
        Button(
            colors = ButtonDefaults.buttonColors(Color.Blue),
            onClick = {
                // Запуск активности выбора изображения
                pickImage.launch("image/*")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(1.dp))
        ) {
            Text(text = stringResource(id = R.string.Addtask), fontSize = 24.sp)
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


    @Composable
    fun addtask(roomid: String, imageUrl: String){
        Button(
            colors = ButtonDefaults.buttonColors(Color.Blue),
            onClick = {
                 val database = Firebase.database("https://code-with-friends-73cde-default-rtdb.europe-west1.firebasedatabase.app/")
               // val database = Firebase.database(stringResource(id = R.string.DataBase))
                val myRef = database.getReference("$roomid")

                val values = mapOf(
                    "gitbranch" to gitbreanch,
                    "filename" to filename,
                    "photo" to imageUrl,
                    "mession" to mession
                )

                myRef.setValue(values)
                      }
            ,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                //.background(creatroom)

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
                    imageUrl = uri
                    // Do something with the URL, such as save it to Firestore
                }
            } else {
                // Handle unsuccessful upload
            }
        }
    }





}
