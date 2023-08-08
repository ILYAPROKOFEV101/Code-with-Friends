package com.example.codewithfriends.ui.theme

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*


import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.codewithfriends.R
import com.example.codewithfriends.Activity.theme.Roomcreator

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
            Create_Acount()
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Preview(showBackground = true)
    @Composable
    fun Create_Acount() {
        var text by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        val joinroom: Color = colorResource(id = R.color.joinroom)
        val creatroom: Color = colorResource(id = R.color.creatroom)
        var textSize by remember { mutableStateOf(24.sp) } // Состояние для хранения размера текста
            //   val userText = getUserText(context) // userText содержит "Привет, это текстовые данные пользователя!"
        var userText = ""
        PreferenceHelper.setUserText(this, userText)

        Column(
            modifier = Modifier // общий элемент
                .fillMaxSize()
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
                        Text(
                            text = stringResource(id = R.string.age),
                            modifier = Modifier.fillMaxSize(),
                            fontSize = 24.sp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp)
                    .wrapContentHeight()
                    .wrapContentSize(align = Alignment.Center) // Выравнивание Box по центру экрана
            ) {


                TextField(
                    value = text, // Текущее значение текста в поле
                    onValueChange = { text = it }, // Обработчик изменения текста, обновляющий переменную "text"
                    textStyle = TextStyle(fontSize = textSize),
                   // textStyle = TextStyle.Default, // Стиль текста, используемый в поле ввода (используется стандартный стиль)

                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent, // Цвет индикатора при фокусе на поле (прозрачный - отключает индикатор)
                        unfocusedIndicatorColor = Color.Transparent, // Цвет индикатора при потере фокуса на поле (прозрачный - отключает индикатор)
                        disabledIndicatorColor = Color.Transparent, // Цвет индикатора, когда поле неактивно (прозрачный - отключает индикатор)
                                containerColor = Color.White
                    ),

                    label = { // Метка, которая отображается над полем ввода
                        Text(text = stringResource(id = R.string.aboutyou),fontSize = 20.sp)
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
                        .fillMaxSize() // Занимает все доступное пространство по ширине и высоте
                        .clip(RoundedCornerShape(30.dp)) // Закругление углов поля
                        .background(Color.LightGray) // Цвет фона поля
                        .focusRequester(focusRequester = focusRequester) // Позволяет управлять фокусом поля ввода
                )

            }}
            Spacer(modifier = Modifier.height(20.dp))

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


                                  },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            //.background(creatroom)

                            .clip(RoundedCornerShape(1.dp))
                    ) {
                        Text(text = stringResource(id = R.string.creatroom),fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button( colors = ButtonDefaults.buttonColors(joinroom),
                        onClick = {
                            val intent = Intent(this@Main_menu, Roomcreator::class.java)
                            startActivity(intent)
                                  },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(
                                RoundedCornerShape(10.dp),

                                )
                    ) {
                        Text(text = stringResource(id = R.string.joinroom),fontSize = 24.sp,)

                    }
                Spacer(modifier = Modifier.height(10.dp))
                }


        }
    }





