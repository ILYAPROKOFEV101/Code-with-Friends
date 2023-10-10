package com.example.codewithfriends.Startmenu

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*


import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.codewithfriends.Activity.theme.CreativyRoom
import com.example.codewithfriends.R
import com.example.codewithfriends.findroom.FindRoom

import com.example.codewithfriends.presentation.profile.ProfileIcon
import com.example.codewithfriends.presentation.profile.ProfileName
import com.example.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.example.reaction.logik.PreferenceHelper

import com.google.android.gms.auth.api.identity.Identity


class Main_menu : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item{
                    Create_Acount()
                }
                item{
                    Edit()
                }
                item {
                    Button()
                }

            }

        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Preview(showBackground = true)
    @Composable
    fun Create_Acount() {

        //   val userText = getUserText(context) // userText содержит "Привет, это текстовые данные пользователя!"
        val userText = ""
        PreferenceHelper.setUserText(this, userText)

        Column(
            modifier = Modifier // общий элемент
                .fillMaxWidth()
                .height(150.dp)
        )
        {
            Row(
                modifier = Modifier // верхний элемент
                    .fillMaxWidth()
                    .height(150.dp),
                verticalAlignment = Alignment.Top
            ) {

                Box(
                    modifier = Modifier // аватарка
                        .padding(start = 5.dp, top = 8.dp)
                        .height(110.dp)
                        .width(110.dp)
                        .padding(2.dp)
                ) {
                    ProfileIcon(
                        userData = googleAuthUiClient.getSignedInUser()
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .padding(5.dp)
                    ) {
                        ProfileName(
                            userData = googleAuthUiClient.getSignedInUser()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)

                    ) {
                       /* Text(
                            text = stringResource(id = R.string.age),
                            modifier = Modifier.fillMaxSize(),
                            fontSize = 24.sp
                        )*/
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun Edit()
    {
        var text by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        val textSize by remember { mutableStateOf(24.sp) } // Состояние для хранения размера текста
        //   val userText = getUserText(context) // userText содержит "Привет, это текстовые данные пользователя!"
        val userText = ""
        PreferenceHelper.setUserText(this, userText)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp)
                .height(780.dp)
                .wrapContentSize(align = Alignment.Center) // Выравнивание Box по центру экрана
        ) {

    item {

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
                    text = stringResource(id = R.string.aboutyou),
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

            item {

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(Color.Magenta),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(20.dp),
                    onClick = {}
                )
                {
                    Text(text = "Сохранить даные", fontSize = 24.sp)
                }
            }


        }
        Spacer(modifier = Modifier.height(20.dp))
    }




            @OptIn(ExperimentalComposeUiApi::class)
            @Composable
            fun Button(){

                val joinroom: Color = colorResource(id = R.color.joinroom)
                val creatroom: Color = colorResource(id = R.color.creatroom)
                //   val userText = getUserText(context) // userText содержит "Привет, это текстовые данные пользователя!"
                val userText = ""
                PreferenceHelper.setUserText(this, userText)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 5.dp, end = 5.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    Button(
                        colors = ButtonDefaults.buttonColors(creatroom),
                        onClick = {
                            val intent = Intent(this@Main_menu, FindRoom::class.java)
                            startActivity(intent)

                                  },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                        ,
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Text(text = stringResource(id = R.string.creatroom),fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button( colors = ButtonDefaults.buttonColors(joinroom),
                        onClick = {
                            val intent = Intent(this@Main_menu, CreativyRoom::class.java)
                            startActivity(intent)
                                  },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                                shape = RoundedCornerShape(20.dp),
                    ) {
                        Text(text = stringResource(id = R.string.joinroom),fontSize = 24.sp,)

                    }
                Spacer(modifier = Modifier.height(10.dp))
                }


        }
    }





