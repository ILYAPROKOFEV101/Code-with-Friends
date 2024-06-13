package com.ilya.codewithfriends.Startmenu.Menu_Fragment

import LoadingComponent
import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import coil.size.Precision
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.ilya.codewithfriends.Activity.CreatyActivity.ApiService
import com.ilya.codewithfriends.Activity.CreatyActivity.CreativyRoom
import com.ilya.codewithfriends.MainViewModel
import com.ilya.codewithfriends.R
import com.ilya.codewithfriends.Startmenu.ADD_PROfile
import com.ilya.codewithfriends.Startmenu.Adduser
import com.ilya.codewithfriends.Startmenu.Apiuser
import com.ilya.codewithfriends.Startmenu.FindRoom
import com.ilya.codewithfriends.Startmenu.Friends
import com.ilya.codewithfriends.Startmenu.Main_menu_fragment
import com.ilya.codewithfriends.Startmenu.User
import com.ilya.codewithfriends.Startmenu.changeUserName
import com.ilya.codewithfriends.Startmenu.changeUserURL
import com.ilya.codewithfriends.chattest.fragments.newUserData
import com.ilya.codewithfriends.findroom.FindRoom
import com.ilya.codewithfriends.findroom.FindRoom_Fragment.FindRoom_fragment
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.reaction.logik.PreferenceHelper
import com.ilya.reaction.logik.PreferenceHelper.getDataFromSharedPreferences
import com.ilya.reaction.logik.PreferenceHelper.saveimg
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

class Mainmenufragment : Fragment() {
    private lateinit var googleAuthUiClient: GoogleAuthUiClient
    private var selectedImageUri: Uri? by mutableStateOf(null)
    private var proces by mutableStateOf(false)
    private val NOTIFICATION_LISTENER_PERMISSION_CODE = 1001
    var selectedNumber = 0
    var aboutme by mutableStateOf("")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        googleAuthUiClient = GoogleAuthUiClient(
            context = requireContext(),
            oneTapClient = Identity.getSignInClient(requireContext())
        )



    }

    var photo by mutableStateOf("")




    private var pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { selectedImageUri = it }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val name = UID(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val img =  PreferenceHelper.getimg(requireContext())
        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val loadingComponent = LoadingComponent()
        loadingComponent.userexsist("$id", requireContext())




        getNumberFromServer("$id")


        Adduser("$name", "$id", "$img")



// Проверяем, есть ли уже разрешение
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            // Запрашиваем разрешение, если его нет
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE), NOTIFICATION_LISTENER_PERMISSION_CODE)
        }





    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Создаем ComposeView и устанавливаем контент
        return ComposeView(requireContext()).apply {
             setContent {
                 val imgValue = PreferenceHelper.getimg(requireContext())
            val viewModel = viewModel<MainViewModel>()
            val isLoading by viewModel.isLoading.collectAsState()
            val swipeRefresh = rememberSwipeRefreshState(isRefreshing = isLoading)


            val retrievedValue = getDataFromSharedPreferences(requireContext(), "my_key")
            Log.d("Resultmydata", "$retrievedValue" )


            getKeyFromServer( "$id", requireContext(),
                onSuccess = { key ->
                    Log.d("Resultmydata", "$key" )
                },
                onError = { error ->
                    Log.e("Errormy", "Failed to get key: ${error.message}")
                }
            )

                 val navController = rememberNavController()
                 NavHost(
                     navController = navController,
                     startDestination = "Main_Menu",
                     modifier = Modifier.fillMaxSize()
                 ) {
                     composable("Main_Menu") {
                         Column(
                             modifier = Modifier
                                 .fillMaxSize()
                         ) {
                             SwipeRefresh(
                                 state = swipeRefresh,
                                 onRefresh = {
                                     requireActivity()
                                 }
                             ) {


                                 Column(
                                     modifier = Modifier
                                         .fillMaxSize()
                                 ) {
                                     LazyColumn(
                                         modifier = Modifier
                                             .fillMaxWidth()
                                             .weight(1f)
                                     ) {
                                         val name = UID(
                                             userData = googleAuthUiClient.getSignedInUser()
                                         )
                                         val img = IMG(
                                             userData = googleAuthUiClient.getSignedInUser()
                                         )
                                         val id = ID(
                                             userData = googleAuthUiClient.getSignedInUser()
                                         )
                                         if(imgValue == ""){
                                             saveimg( requireContext(), "$img")
                                         }
                                         item {
                                             Create_Acount(if(imgValue == ""){"$img"} else {"$imgValue"}, LocalContext.current)
                                         }
                                         item {
                                             Edit("$id", "$img", "$name", navController)
                                         }
                                         item {
                                             Button("$id", navController)
                                         }
                                     }

                                 }
                             }
                         }
                     }
                     composable("FindRoom") {
                         FindRoom()
                     }
                      composable("MYSELF"){
                          ADD_PROfile()
                      }



                 }


        }
        }
    }


    @Composable
    fun FindRoom() {
        AndroidView(
            factory = { context ->
                // Создаем FragmentContainerView
                FragmentContainerView(context).apply {
                    id = View.generateViewId()
                }
            },
            update = { view ->
                // Получаем FragmentManager
                val fragmentManager = (view.context as FragmentActivity).supportFragmentManager
                // Создаем и добавляем Chatmenu фрагмент
                val fragmentTransaction = fragmentManager.beginTransaction()
                val FindRoom_fragment = FindRoom_fragment()
                fragmentTransaction.replace(view.id, FindRoom_fragment())
                fragmentTransaction.commit()
            }
        )


    }


    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun Create_Acount(imgValue: String, context: Context) {

        var showimg by remember { mutableStateOf(false) }
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

        val img =  PreferenceHelper.getimg(requireContext())
        var name =  PreferenceHelper.getname(requireContext())

        if (name == ""){
            name = name2
        }
        var text by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        val textSize by remember { mutableStateOf(24.sp) } // Состояние для хранения размера текста

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
                            .size(90.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .clickable {
                                showimg = !showimg

                                pickImage.launch("image/*")
                            },
                        contentScale = ContentScale.Crop
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
                            .height(100.dp)
                            .padding(5.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start

                        ) {
                            if (!edit) {
                                Row(Modifier.fillMaxSize()) {
                                    Text(
                                        text = "$name",
                                        textAlign = TextAlign.Center,
                                        fontSize = 30.sp,
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
                                Row(Modifier.fillMaxSize().fillMaxWidth()) {
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
                                                PreferenceHelper.savename(context, text)
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
                                            PreferenceHelper.savename(context, text)
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                    ) {

                    }
                }
            }
        }

        if(showimg == true) {
            Box(modifier = Modifier
                .width(400.dp)
                .height(400.dp)
                .zIndex(1f), // Устанавливает z-индекс, чтобы поместиться наверху других элементов
                contentAlignment = Alignment.BottomCenter // Выравнивание по нижнему кра
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberImagePainter(selectedImageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(bottom = 15.dp, top = 15.dp, start = 70.dp, end = 70.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {},
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 100.dp, end = 100.dp)
                .clip(RoundedCornerShape(20.dp))
                .height(50.dp)
                .alpha(0.9f) // Пример половинной прозрачности
                .background(Color(0xFF4795CA)),
                horizontalArrangement = Arrangement.Center,
                //verticalArrangement = Arrangement.Center
            ){
                IconButton(
                    modifier = Modifier
                        .size(100.dp), // Выравнивание по центру вертикально
                    onClick = {
                        showimg = false
                        photo = ""
                    }
                ){
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Cancel"
                    )
                }
                IconButton(
                    modifier = Modifier
                        .size(100.dp), // Выравнивание по центру вертикально
                    onClick = {
                        //showimg = false
                        proces = true
                        selectedImageUri?.let {
                            uploadImageToFirebaseStorage("$id")
                        }
                    }
                ){
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Check",
                        tint = Color.Black
                    )

                }
            }
        }
    }

    private fun showToast(message: String, context: Context) {
        // Вывести Toast с заданным сообщением
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun uploadImageToFirebaseStorage(uid: String) {

        if (selectedImageUri == null) {
            // Обработка случая, когда изображение не выбрано
            showToast("Выберите изображение перед загрузкой.", requireContext())
            return
        }

        proces = true

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        // Create a unique name for the image to avoid overwriting
        val imageName = UUID.randomUUID().toString()

        // Path to store the image: "images/{roomid}/{imageName}"
        val imageRef = storageRef.child("images/$uid/$imageName")

        val metadata = StorageMetadata.Builder()
            .setContentType("image/png") // Указываем тип контента как PNG
            .build()

        val uploadTask = imageRef.putFile(selectedImageUri!!, metadata)

        // Add a listener to handle successful or unsuccessful upload
        uploadTask.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                // Get the download URL from the task result
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    photo = uri.toString()
                    // Do something with the URL, such as save it to Firestore
                    PreferenceHelper.saveimg(requireContext(), "$photo")
                    changeUserURL(uid, photo)

                    proces = false
                    // Покажите Toast об успешной загрузке
                    showToast(getString(R.string.addPhoto), requireContext())

                    // Обнулить selectedImageUri после успешной загрузки
                    selectedImageUri = null
                }
            } else {
                // Handle unsuccessful upload
                proces = false
                // Покажите Toast об ошибке загрузки
                showToast(getString(R.string.upload_error_message), requireContext())
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun Edit(uid : String, img : String, name: String, navController: NavController)
    {
        var text by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        val textSize by remember { mutableStateOf(24.sp) } // Состояние для хранения размера текста
        val userText = ""
        PreferenceHelper.setUserText(requireContext(), userText)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp)
                .height(500.dp)
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
                            aboutme = text
                        }
                    ),

                    modifier = Modifier
                        .fillMaxWidth() // Занимает все доступное пространство по ширине и высоте
                        .height(400.dp)
                        .clip(RoundedCornerShape(30.dp)) // Закругление углов поля
                        .background(Color.LightGray) // Цвет фона поля
                        .focusRequester(focusRequester = focusRequester) // Позволяет управлять фокусом поля ввода
                )
            }

            item {

                Spacer(modifier = Modifier.height(20.dp))
                androidx.compose.material3.Button(
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(20.dp),
                    onClick = {

                        // wratehere
                        /*if(aboutme != ""){
                            sendPostRequest("$uid", "$img", "$name")
                        }*/
                        navController.navigate("MYSELF")
                    }
                )
                {
                    Text(text = stringResource(id = R.string.savedata), fontSize = 20.sp)
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
        Spacer(modifier = Modifier.height(20.dp))
    }




    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun Button(uid: String, navController: NavController){


        getNumberFromServer(uid)
        val joinroom: Color = colorResource(id = R.color.joinroom)
        val creatroom: Color = colorResource(id = R.color.creatroom)
        //   val userText = getUserText(context) // userText содержит "Привет, это текстовые данные пользователя!"
        val userText = ""
        PreferenceHelper.setUserText(requireContext(), userText)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 5.dp, end = 5.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            androidx.compose.material3.Button(
                colors = ButtonDefaults.buttonColors(creatroom),
                onClick = {

                    navController.navigate("FindRoom")
                    /*val intent = Intent(requireContext(), FindRoom::class.java)
                    startActivity(intent)*/
                    //finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(20.dp),
            )
            {
                Text(text = stringResource(id = R.string.creatroom), fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            androidx.compose.material3.Button(
                colors = ButtonDefaults.buttonColors(Color.Blue),
                onClick = {
                    val intent = Intent(requireContext(), CreativyRoom::class.java)//CreativyRoom
                    startActivity(intent)

                    if (selectedNumber <= 0) {

                        // finish()
                    } else {
                        showToast(getString(R.string.myroom))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(20.dp),
            ) {
                Text(text = stringResource(id = R.string.joinroom), fontSize = 24.sp,)

            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }


    private fun showToast(message: String) {
        // Вывести Toast с заданным сообщением
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }







    fun sendPostRequest(uid : String, img : String, name: String) {
        // Создайте экземпляр Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://getpost-ilya1.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создайте экземпляр службы API
        val apiService = retrofit.create(Apiuser::class.java)

        // Создайте объект TaskRequest
        val request = User("$aboutme", 17, "$uid", "$img" )

        // Отправьте POST-запрос с передачей roomId в качестве параметра пути
        val call = apiService.Sanduser("$uid", request)
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

}

fun areNotificationsEnabled(context: Context): Boolean {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    return notificationManager.areNotificationsEnabled()
}
