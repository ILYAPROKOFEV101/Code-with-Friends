package com.example.codewithfriends.createamspeck

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.RadioButton
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codewithfriends.R
import com.example.codewithfriends.createamspeck.ui.theme.CodeWithFriendsTheme
import com.example.codewithfriends.presentation.profile.ID
import com.example.codewithfriends.roomsetting.TaskData
import com.example.reaction.logik.PreferenceHelper
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.Date


data class SeatOption(val name: String, val seatCount: Int)
class TeamSpeak : ComponentActivity() {


    var selectedDate by mutableStateOf<String?>(null)


    var selectedTime by mutableStateOf<Pair<Int, Int>?>(null)


    var selectedSeatCount by mutableStateOf<Int?>(null)

    val locar = 3

    var nameteamspeck by mutableStateOf("")

    var password by mutableStateOf("")

    private var storedRoomId: String? = null // Объявляем на уровне класса

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storedRoomId = PreferenceHelper.getRoomId(this)

        setContent {
            CodeWithFriendsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            pikdata()
                        }

                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            Greeting()
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            RadioButtonGroupExample()
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            whatineedtodo()
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            Password()
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            Pushdata()
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun pikdata() {
        val calendarState = rememberSheetState()

        // Создайте состояние для отслеживания выбранной даты
        val selectedDateState = remember { mutableStateOf<String?>(null) }

        CalendarDialog(
            state = calendarState,
            config = CalendarConfig(
                monthSelection = true,
                yearSelection = true
            ),
            selection = CalendarSelection.Date { date ->
                // Сохраните выбранную дату в глобальной переменной
                selectedDate = date.toString()
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(5.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(Color.Blue),
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(30.dp),
                onClick = {
                    calendarState.show()
                }
            ) {
                // Отобразите выбранную дату внутри кнопки
                Text(text = selectedDate ?: stringResource(id = R.string.chosedata), fontSize = 24.sp)
            }
        }
    }






    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @Preview(showBackground = true)
    fun Greeting() {
        val clockState = rememberSheetState()

        ClockDialog(
            state = clockState,
            selection = ClockSelection.HoursMinutes { hours, minutes ->
                // Сохраните выбранное время в глобальной переменной
                selectedTime = Pair(hours, minutes)
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(5.dp)
                .background(MaterialTheme.colorScheme.background),
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(Color.Green),
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(30.dp),
                onClick = {
                    clockState.show()
                }
            ) {
                Text(
                    text = selectedTime?.let { (hours, minutes) ->
                        stringResource(id = R.string.time).format(hours, minutes)
                    } ?: stringResource(id = R.string.chosetime),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }



    @Composable
    fun RadioButtonGroupExample() {
        val selectedSeatCount = remember { mutableStateOf(selectedSeatCount) }


        val chose = stringResource(id = R.string.Selected)
        val nothingchose = stringResource(id = R.string.Nothinkchose)
        val place = stringResource(id = R.string.Nothinkchose)

        val seatOptions = listOf(
            SeatOption("2 seats", 2),
            SeatOption("3 seats", 3),
            SeatOption("4 seats", 4),
            SeatOption("5 seats", 5),
            SeatOption("unlimited number of seats", 100)
        )

        Column {
            seatOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedSeatCount.value = option.seatCount
                        }
                ) {
                    RadioButton(
                        selected = selectedSeatCount.value == option.seatCount,
                        onClick = { selectedSeatCount.value = option.seatCount },
                        modifier = Modifier.padding(16.dp)
                    )

                    Text(
                        text = option.name,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Text(
                text = " $chose: ${selectedSeatCount.value ?: "$nothingchose"} $place",
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    @Preview(showBackground = true)
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun whatineedtodo(){
        val keyboardControllers = LocalSoftwareKeyboardController.current
        var showtext by remember {
            mutableStateOf(false) }
     Card(modifier = Modifier
         .fillMaxWidth()
         .padding(start = 10.dp, end = 10.dp)
         .clip(RoundedCornerShape(30.dp))
         .height(100.dp)
         .border(
             border = BorderStroke(8.dp, SolidColor(Color.Blue)),
             shape = RoundedCornerShape(30.dp)
         ),
            shape = RoundedCornerShape(30.dp)
        ){
            TextField(modifier = Modifier.fillMaxSize(),
                value = nameteamspeck, // Текущее значение текста в поле
                onValueChange = { nameteamspeck = it }, // Обработчик изменения текста, обновляющий переменную "text"
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
                        text = if (!showtext) stringResource(id = R.string.nameTeamspek) else "",
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
                        if (nameteamspeck != "") {
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
    fun Password(){
        val keyboardControllers = LocalSoftwareKeyboardController.current
        var showtext by remember {
            mutableStateOf(false) }
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
            .clip(RoundedCornerShape(30.dp))
            .height(100.dp)
            .border(
                border = BorderStroke(8.dp, SolidColor(Color.Blue)),
                shape = RoundedCornerShape(30.dp)
            ),
            shape = RoundedCornerShape(30.dp)
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
                        if (password != "") {
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
        fun Pushdata(){
            val pushColor = colorResource(id = R.color.push) // Получите цвет из ресурсов
            
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(pushColor)
                .height(100.dp),
                shape = RoundedCornerShape(30.dp)
            ){
                Button(
                    colors = ButtonDefaults.buttonColors(pushColor),
                    modifier = Modifier.fillMaxSize()
                    ,
                    shape = RoundedCornerShape(30.dp),
                    onClick = {

                        Create()
                    }) {
                    Text(text = "Create Teamspeak", fontSize = 24.sp)
                }
            }
        }

    private fun Create() {
        val baseUrl = "https://getpost-ilya1.up.railway.app/teamspeack/kdniovirgoi"
        val url = "$baseUrl"

        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()

        val json = """
        {
            "data": "$selectedDate"
            "time": "$selectedTime"
            "name": "$nameteamspeck"
            "password": "$password"
            "localtime": $locar
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



}