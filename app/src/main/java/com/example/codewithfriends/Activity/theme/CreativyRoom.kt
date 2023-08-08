package com.example.codewithfriends.Activity.theme

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codewithfriends.R
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

class CreativyRoom : ComponentActivity() {

    var text by mutableStateOf("")

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


    data class UserData(
        val selectedLanguage: String,
        val places: List<Int>,
        val text: String,
        val uniqueId: String
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Column {

                Creator()

                Spacer(modifier = Modifier.height(30.dp))

                LanguagePicker()

                Spacer(modifier = Modifier.height(30.dp))

                PlaceInRoomPicker()

                Spacer(modifier = Modifier.height(60.dp))

                WriteDb()
            }

        }

    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
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
                .background(Color.White)
                .padding(top = 30.dp)

        ) {

            TextField(


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

    @Composable
    fun LanguagePicker() {
        var expanded by remember { mutableStateOf(false) }




        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .padding()
                .background(Color.White)
                .padding(16.dp)
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
                .clip(RoundedCornerShape(30.dp))
                .padding()
                .background(Color.White)
                .padding(16.dp)
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


    fun generateUniqueId(): String {
        val characters = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return List(10) { characters.random() }.joinToString("")
    }

    val uniqueId = generateUniqueId()



    private fun pushData() {
        val baseUrl = "https://getpost-ilya1.up.railway.app/user"
        val uriBuilder = Uri.parse(baseUrl).buildUpon()
            .appendQueryParameter("id", uniqueId)
            .appendQueryParameter("Lenguage", selectedLanguage)
            .appendQueryParameter("Placeinroom", selectedPlace.toString()) // Преобразуем число в строку
            .appendQueryParameter("Roomname", text)
            .build()


        val url = uriBuilder.toString()

        val client = OkHttpClient()
        val mediaType = "application/x-www-form-urlencoded".toMediaType()

        val requestBody = "".toRequestBody(mediaType)

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

}