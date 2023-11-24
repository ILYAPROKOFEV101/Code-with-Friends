package com.example.codewithfriends.Complaint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
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
import com.example.codewithfriends.Complaint.ui.theme.CodeWithFriendsTheme
import com.example.codewithfriends.R
import com.example.reaction.logik.PreferenceHelper
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Sendban : ComponentActivity() {
    var text by mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra("userId")

        setContent {
            CodeWithFriendsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Edit()
                        }
                        item {
                            Complaint( "$userId")
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun Complaint(userId: String) {
        Button(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(5.dp), shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFF44336)),
            onClick = {
                sendPostRequest("$userId", "$text")
            }) {
            Text(
                text = stringResource(id = R.string.Complaint),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun Edit() {
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        val textSize by remember { mutableStateOf(24.sp) } // Состояние для хранения размера текста



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp)
                .height(700.dp)
        ) {


            TextField(
                value = text, // Текущее значение текста в поле
                onValueChange = {
                    text = it
                }, // Обработчик изменения текста, обновляющий переменную "text"
                textStyle = TextStyle(fontSize = textSize),
                // textStyle = TextStyle.Default, // Стиль текста, используемый в поле ввода (используется стандартный стиль)

                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent, // Цвет индикатора при фокусе на поле (прозрачный - отключает индикатор)
                    unfocusedIndicatorColor = Color.Transparent, // Цвет индикатора при потере фокуса на поле (прозрачный - отключает индикатор)
                    disabledIndicatorColor = Color.Transparent, // Цвет индикатора, когда поле неактивно (прозрачный - отключает индикатор)
                    containerColor = Color.White
                ),

                label = { // Метка, которая отображается над полем ввода
                    Text(
                        text = stringResource(id = R.string.writecomplaint),
                        fontSize = 20.sp,
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

                    }
                ),

                modifier = Modifier
                    .fillMaxWidth() // Занимает все доступное пространство по ширине и высоте
                    .height(700.dp)
                    .clip(RoundedCornerShape(30.dp)) // Закругление углов поля
                    .background(Color.LightGray) // Цвет фона поля
                    .focusRequester(focusRequester = focusRequester) // Позволяет управлять фокусом поля ввода
            )
        }


    }

}
fun sendPostRequest(complaint: String, name: String ) {
    // Создайте экземпляр Retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("https://getpost-ilya1.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Создайте экземпляр службы API
    val apiService = retrofit.create(Complainttouser::class.java)

    // Создайте объект TaskRequest
    val request = Complaint("$complaint", name )

    // Отправьте POST-запрос с передачей roomId в качестве параметра пути
    val call = apiService.Sanduser(request)
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