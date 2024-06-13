package com.ilya.codewithfriends.Startmenu.Menu_Fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.auth.api.identity.Identity
import com.ilya.codewithfriends.MainViewModel
import com.ilya.codewithfriends.R
import com.ilya.codewithfriends.Startmenu.Adduser
import com.ilya.codewithfriends.Startmenu.changeUserName
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.reaction.logik.PreferenceHelper


class Myprofile_fragment : Fragment() {

    private lateinit var googleAuthUiClient: GoogleAuthUiClient
    override fun onAttach(context: Context) {
        super.onAttach(context)
        googleAuthUiClient = GoogleAuthUiClient(
            context = requireContext(),
            oneTapClient = Identity.getSignInClient(requireContext())
        )
    }

    var aboutme by mutableStateOf("")
    var gcontact by mutableStateOf("")
    private var pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedImageUri = it }
        }

    private var selectedImageUri: Uri? by mutableStateOf(null)
    var photo by mutableStateOf("")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Создаем ComposeView и устанавливаем контент
        val img = PreferenceHelper.getimg(requireContext())
        return ComposeView(requireContext()).apply {
            setContent {

                val imgValue = PreferenceHelper.getimg(requireContext())
                Heighteliment("$imgValue", "$img")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Heighteliment(imgValue: String, img: String) {
        var showimg = remember {
            mutableStateOf(false)
        }
        var edit by remember { mutableStateOf(false) }

        //   val userText = getUserText(context) // userText содержит "Привет, это текстовые данные пользователя!"
        val userText = ""
        PreferenceHelper.setUserText(requireContext(), userText)

        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val name2 = UID(
            userData = googleAuthUiClient.getSignedInUser()
        )

        val img = PreferenceHelper.getimg(requireContext())
        var name = PreferenceHelper.getname(requireContext())

        if (name == "") {
            name = name2
        }
        var bio by remember { mutableStateOf(false) }
        var contaxtshow by remember { mutableStateOf(false) }
        var text by remember { mutableStateOf("") }
        var contact by remember { mutableStateOf("") }

        val focusRequester = remember { FocusRequester() }
        val textSize by remember { mutableStateOf(24.sp) } // Состояние для хранения размера текста
        val keyboardController = LocalSoftwareKeyboardController.current

        LazyColumn(Modifier.fillMaxSize()) {

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    {
                        Text(
                            text = "Профель",
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Box(
                            modifier = Modifier // аватарка
                                .padding(start = 5.dp, top = 8.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(30.dp))
                                .fillMaxWidth(0.4f)
                                .padding(2.dp)
                        ) {
                            val painter = rememberImagePainter(
                                data = if (imgValue.isNullOrEmpty()) {
                                    img
                                } else {
                                    imgValue
                                },
                                builder = {
                                    crossfade(true)
                                }
                            )

                            Image(
                                painter = painter,
                                contentDescription = "Profile picture",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    // .align(Alignment.Center)
                                    .align(Alignment.TopCenter)
                                    .clip(RoundedCornerShape(30.dp))
                                    .clickable {
                                        showimg.value = !showimg.value
                                        pickImage.launch("image/*")
                                    },

                                contentScale = ContentScale.Fit,

                                )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        )
                        {
                            if (!edit) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                ) {
                                    Text(
                                        text = "$name",
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    IconButton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        onClick = {
                                            edit = !edit
                                        }
                                    )
                                    {
                                        Icon(
                                            modifier = Modifier
                                                .width(60.dp),
                                            painter = painterResource(id = R.drawable.edit),
                                            contentDescription = "Edit",
                                            // Цвет иконки
                                        )
                                    }
                                }
                            } else {
                                Row(
                                    Modifier
                                        .fillMaxSize()
                                        .fillMaxWidth()
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

                                        /*label = { // Метка, которая отображается над полем ввода
                                    Text(
                                        text = stringResource(id = R.string.Your_name),
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center
                                    )
                                },*/

                                        keyboardOptions = KeyboardOptions(
                                            imeAction = ImeAction.Done, // Действие на кнопке "Готово" на клавиатуре (закрытие клавиатуры)

                                            keyboardType = KeyboardType.Text // Тип клавиатуры (обычный текст)
                                        ),

                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                edit = !edit
                                                PreferenceHelper.savename(requireContext(), text)
                                                changeUserName("$id", text)
                                            }
                                        ),

                                        modifier = Modifier
                                            .weight(0.8f)
                                            .height(55.dp)
                                            .clip(RoundedCornerShape(30.dp)) // Закругление углов поля
                                            .background(Color.LightGray) // Цвет фона поля
                                            .focusRequester(focusRequester = focusRequester) // Позволяет управлять фокусом поля ввода
                                    )

                                    IconButton(
                                        modifier = Modifier
                                            .weight(0.2f)
                                            .height(50.dp),
                                        onClick = {
                                            edit = !edit
                                            PreferenceHelper.savename(requireContext(), text)
                                            changeUserName("$id", text)
                                        }
                                    )
                                    {
                                        Icon(
                                            modifier = Modifier
                                                .width(60.dp),
                                            imageVector = Icons.Default.Save,
                                            contentDescription = "Save",
                                            // Цвет иконки
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }
            // next bio

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                    ) {
                        Text(
                            text = "bio",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            modifier = Modifier
                                .size(20.dp),
                            painter = painterResource(id = R.drawable.edit),
                            contentDescription = "Edit",

                            // Цвет иконки
                        )
                    }

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
                            disabledIndicatorColor = MaterialTheme.colorScheme.background, // Цвет индикатора, когда поле неактивно (прозрачный - отключает индикатор)
                            containerColor = Color.Transparent
                        ),


                        label = { // Метка, которая отображается над полем ввода
                            if(text  == ""){
                                Text(
                                    text = stringResource(id = R.string.aboutyou),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.background
                                )
                            }
                        },

                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done, // Действие на кнопке "Готово" на клавиатуре (закрытие клавиатуры)

                            keyboardType = KeyboardType.Text // Тип клавиатуры (обычный текст)
                        ),

                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide() // Обработчик действия при нажатии на кнопку "Готово" на клавиатуре (скрыть клавиатуру)
                                aboutme = text
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth() // Занимает все доступное пространство по ширине и высоте
                            .height(150.dp)
                            .padding(start = 10.dp, end = 10.dp)
                            .clip(RoundedCornerShape(30.dp)) // Закругление углов поля
                            .background(Color.LightGray) // Цвет фона поля
                            .focusRequester(focusRequester = focusRequester) // Позволяет управлять фокусом поля ввода
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Text(text = "Контакты", fontSize = 20.sp, modifier = Modifier.padding(start = 15.dp))
                    Spacer(modifier = Modifier.fillMaxWidth())
                    // Text(text = "Контакты", fontSize = 20.sp)
                    Box(
                        Modifier
                            .fillMaxSize()
                    ) {
                        TextField(
                            value = contact, // Текущее значение текста в поле
                            onValueChange = {
                                contact = it
                            }, // Обработчик изменения текста, обновляющий переменную "text"
                            textStyle = TextStyle(fontSize = textSize),
                            // textStyle = TextStyle.Default, // Стиль текста, используемый в поле ввода (используется стандартный стиль)

                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent, // Цвет индикатора при фокусе на поле (прозрачный - отключает индикатор)
                                unfocusedIndicatorColor = Color.Transparent, // Цвет индикатора при потере фокуса на поле (прозрачный - отключает индикатор)
                                disabledIndicatorColor = Color.Transparent, // Цвет индикатора, когда поле неактивно (прозрачный - отключает индикатор)
                                containerColor = Color.Transparent
                            ),

                            label = { // Метка, которая отображается над полем ввода
                                if(contact == "") {
                                    Text(
                                        text = "Дайте контакты на ваш github и социальны сетий ",
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.background
                                    )
                                }
                            },

                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done, // Действие на кнопке "Готово" на клавиатуре (закрытие клавиатуры)

                                keyboardType = KeyboardType.Text // Тип клавиатуры (обычный текст)
                            ),

                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide() // Обработчик действия при нажатии на кнопку "Готово" на клавиатуре (скрыть клавиатуру)
                                    gcontact = contact
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth() // Занимает все доступное пространство по ширине и высоте
                                .height(100.dp)
                                .padding(start = 10.dp, end = 10.dp)
                                .clip(RoundedCornerShape(30.dp)) // Закругление углов поля
                                .background(Color.LightGray) // Цвет фона поля
                                .focusRequester(focusRequester = focusRequester) // Позволяет управлять фокусом поля ввода
                        )
                    }

                }
            }
        }
    }
}





