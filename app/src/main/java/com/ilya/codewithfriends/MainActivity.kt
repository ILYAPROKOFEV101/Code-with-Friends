
package com.ilya.codewithfriends

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ilya.codewithfriends.Startmenu.Main_menu
import com.ilya.codewithfriends.presentation.profile.ProfileScreen
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.codewithfriends.presentation.sign_in.SignInState
import com.ilya.codewithfriends.presentation.sign_in.SignInViewModel
import com.ilya.codewithfriends.presentation.sign_in.UserData
import com.ilya.codewithfriends.ui.theme.ComposeGoogleSignInCleanArchitectureTheme
import com.ilya.reaction.logik.PreferenceHelper
import com.ilya.reaction.logik.PreferenceHelper.saveRoomId
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import getSettingFromServer


class  MainActivity: ComponentActivity() {


    class BottomNavigationItem(
        val title: String,
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector,
        val hasNews: Boolean,
        val badgeCount: Int? = null) {
    }


    private lateinit var auth: FirebaseAuth

    var leftop by mutableStateOf(true)


    var username by mutableStateOf("")
    var password by mutableStateOf("")



    var cloth by mutableStateOf(true)

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)



        // Инициализируйте PreferenceHelper в вашей активности
        PreferenceHelper.initialize(applicationContext)
        setContent {
            toshear(userData = googleAuthUiClient.getSignedInUser())

                ComposeGoogleSignInCleanArchitectureTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = androidx.compose.material.MaterialTheme.colors.background
                    ) {
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = "sign_in") {
                            composable("sign_in") {
                                val viewModel = viewModel<SignInViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                LaunchedEffect(key1 = Unit) {
                                    if(googleAuthUiClient.getSignedInUser() != null) {
                                        navController.navigate("profile")
                                    }
                                }

                                val launcher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                                    onResult = { result ->
                                        if(result.resultCode == RESULT_OK) {
                                            lifecycleScope.launch {
                                                val signInResult = googleAuthUiClient.signInWithIntent(
                                                    intent = result.data ?: return@launch
                                                )
                                                viewModel.onSignInResult(signInResult)
                                            }
                                        }
                                    }
                                )

                                LaunchedEffect(key1 = state.isSignInSuccessful) {
                                    if(state.isSignInSuccessful) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Регистрация прошла успешно",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.navigate("profile")
                                        viewModel.resetState()
                                        leftop = !leftop
                                    }
                                }

                                SignInScreen(
                                    state = state,
                                    onSignInClick = {
                                        lifecycleScope.launch {
                                            val signInIntentSender = googleAuthUiClient.signIn()
                                            launcher.launch(
                                                IntentSenderRequest.Builder(
                                                    signInIntentSender ?: return@launch
                                                ).build()
                                            )
                                        }
                                    },
                                    navController
                                )
                            }


                            composable("profile") {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    ProfileScreen(
                                        userData = googleAuthUiClient.getSignedInUser(),
                                        onSignOut = {
                                            lifecycleScope.launch {
                                                googleAuthUiClient.signOut()
                                                saveRoomId(this@MainActivity, "")
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Goodbye",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                navController.popBackStack()
                                            }
                                        }
                                    )
                                    backtomenu()
                                }
                            }
                            composable("login")
                            {
                                LoginUsermenu()
                            }

                        }
                    }
                }
        }
    }



    @Composable
    fun SignInScreen(
        state: SignInState,
        onSignInClick: () -> Unit,
        navController: NavController

        ) {

        var unvisible by remember {
            mutableStateOf(false)
        }


        var user by remember { mutableStateOf(Firebase.auth.currentUser) }

        val launcher = rememberFirebaseAuthLauncher(
            onAuthComplete = { result ->
                user = result.user

               // navController.navigate("profile")
            },
            onAuthError = {
                user = null
            }
        )
        val token = stringResource(id = R.string.web_client_id)
        val context = LocalContext.current

        val scope = rememberCoroutineScope()
        val serverSetting = remember { mutableStateOf(false) }

        LaunchedEffect(key1 = true) {
            serverSetting.value = getSettingFromServer()
        }


        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            if (!leftop) {

                toshear(userData = googleAuthUiClient.getSignedInUser())
                Box(
                    modifier = Modifier
                        .height(400.dp)
                        .align(Alignment.Center)
                        .padding(top = 100.dp)
                ) {
                    LoadingCircle()

                }


            }

            if (user == null) {

                if (!unvisible) {

                    Column(
                        modifier = Modifier
                            .wrapContentSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        IconButton(
                            onClick = {
                                Log.d("GoogleSignIn", "Attempting to sign in")

                                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(token)
                                    .requestEmail()
                                    .build()

                                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                launcher.launch(googleSignInClient.signInIntent)

                                leftop = !leftop
                                unvisible = !unvisible
                                PreferenceHelper.setShowElement(context, true)

                                Log.d("GoogleSignIn", "Sign in intent launched")
                            }
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = "Nothing",

                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(stringResource(id = R.string.login))

                        Spacer(modifier = Modifier.height(10.dp))


                        Spacer(modifier = Modifier.height(10.dp))

                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp))
                            {
                        ButtonAppBar(navController)
                            }

                    }
                }
            }
        }

    }

    @Composable
    fun toshear(userData: UserData?) {
        if (userData?.username != null) {
            val intent = Intent(this@MainActivity, Main_menu::class.java)
            startActivity(intent)
        }
    }

    @Composable
    fun backtomenu(){
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
            contentAlignment = Alignment.BottomEnd // Размещаем Box внизу

        ){
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 70.dp, end = 70.dp)
                    .height(50.dp)
                    .align(Alignment.Center),
                colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)),
                shape = RoundedCornerShape(20.dp),
                onClick = {
                    val intent = Intent(this@MainActivity, Main_menu::class.java)
                    startActivity(intent)
                    finish()
                }
            )
            {
                Text(stringResource(id = R.string.back))
            }
        }
    }

    @Composable
    fun LoginUsermenu() {
        auth = FirebaseAuth.getInstance()
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .clip(RoundedCornerShape(30.dp)),
                colors = CardDefaults.cardColors(Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),

                ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {

                        item {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Регистрация",
                                    fontSize = 24.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))

                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(80.dp)
                            ) {
                                Use_name()

                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(80.dp)
                            ) {
                                Password()

                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(80.dp)
                            )
                            {
                                Login(auth)
                            }
                            }
                    }
                }
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun Use_name() {



        val keyboardControllers = LocalSoftwareKeyboardController.current
        var showtext by remember {
            mutableStateOf(false) }
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
            .clip(RoundedCornerShape(30.dp))
            .height(100.dp)
            .border(
                border = BorderStroke(2.dp, SolidColor(Color.Blue)),
                shape = RoundedCornerShape(30.dp)
            ),
            shape = RoundedCornerShape(30.dp)
        ){
            TextField(
                modifier = Modifier.fillMaxSize(),
                value = username, // Текущее значение текста в поле
                onValueChange = {
                    username = it
                }, // Обработчик изменения текста, обновляющий переменную "text"
                textStyle = TextStyle(fontSize = 24.sp),


                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.White, // Цвет индикатора при фокусе на поле (прозрачный - отключает индикатор)
                    unfocusedIndicatorColor = Color.White, // Цвет индикатора при потере фокуса на поле (прозрачный - отключает индикатор)
                    disabledIndicatorColor = Color.White, // Цвет индикатора, когда поле неактивно (прозрачный - отключает индикатор)
                    containerColor = Color.White
                ),

                label = { // Метка, которая отображается над полем ввода

                    Text(
                        text = "email",
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
                        keyboardControllers?.hide() // Обработчик действия при нажатии на кнопку "Готово" на клавиатуре (скрыть клавиатуру)
                        if (username != "") {
                            showtext = !showtext
                        }

                    }
                ),
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun Password() {

        val keyboardControllers = LocalSoftwareKeyboardController.current
        var showtext by remember {
            mutableStateOf(false) }

        var passwordError by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                .clip(RoundedCornerShape(30.dp))
                .height(100.dp)
                .border(
                    border = BorderStroke(2.dp, SolidColor(Color.Blue)),
                    shape = RoundedCornerShape(30.dp)
                ),
            shape = RoundedCornerShape(30.dp)
        ) {
            TextField(
                modifier = Modifier.fillMaxSize(),
                value = password, // Текущее значение текста в поле
                onValueChange = {
                    password = it
                    passwordError = it.length < 6
                }, // Обработчик изменения текста, обновляющий переменную "password" и проверяющий длину
                textStyle = TextStyle(fontSize = 24.sp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    disabledIndicatorColor = Color.White,
                    containerColor = Color.White
                ),
                label = { // Метка, которая отображается над полем ввода
                    Text(
                        text = "Пароль",
                        fontSize = 24.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                isError = passwordError, // Показываем ошибку, если пароль слишком короткий
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardControllers?.hide() // Обработчик действия при нажатии на кнопку "Готово" на клавиатуре (скрыть клавиатуру)
                        if (password.isNotEmpty()) {
                            showtext = !showtext
                        }
                    }
                ),
            )
        }

        if (passwordError) {
            Toast.makeText(
                LocalContext.current,
                "Пароль должен содержать минимум 6 символов",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Composable
    fun Login(auth: FirebaseAuth) {
        Button(
            onClick = {
                if(username.isNotBlank() && password.isNotBlank()) {
                    registerUser(this,auth, username, password) { success ->
                        if (success) {
                            val intent = Intent(this@MainActivity, Main_menu::class.java)
                            startActivity(intent)
                        } else {
                            // Регистрация не удалась
                            // Обработка ошибки
                        }
                    }

                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                .height(100.dp),
            colors = ButtonDefaults.buttonColors(Color(0xB900CE0A)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(text = "Зайти", fontSize = 30.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonAppBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
        // .background(Color(0xFFFFFFFF)),
    ) {

        var selectedItemIndex by rememberSaveable {
            mutableStateOf(0)
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White.copy(alpha = 0.5f))
        ) {
            NavigationBar(modifier = Modifier.align(Alignment.BottomCenter)) {
                val items = listOf(
                   /* MainActivity.BottomNavigationItem(
                        title = "User",
                        selectedIcon = Icons.Filled.Person,
                        unselectedIcon = Icons.Outlined.Person,
                        hasNews = false,
                    ),*/
                    MainActivity.BottomNavigationItem(
                        title = "Зайти используя почту",
                        selectedIcon = Icons.Filled.Mail,
                        unselectedIcon = Icons.Outlined.Mail,
                        hasNews = false,
                    )
                )

                navController.addOnDestinationChangedListener { _, destination, _ ->
                    selectedItemIndex = when (destination.route) {
                        //"user_log_in" -> 0
                        "admin_fragment" -> 0
                        else -> selectedItemIndex
                    }
                }

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            when (index) {
                                0 -> navController.navigate("login")
                               // 1 -> navController.navigate("admin_fragment")
                            }
                        },
                        label = {
                            Text(text = item.title)
                        },
                        alwaysShowLabel = false,
                        icon = {
                            Icon(
                                imageVector = if (index == selectedItemIndex) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoadingCircle() {
    Box(  modifier = Modifier
        .height(100.dp)


        .wrapContentSize(Alignment.TopCenter)
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
            modifier = Modifier.size(100.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(90.dp)
                    //.rotate(rotation.value)
            )
        }
    }
}


@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d("GoogleAuth", "account $account")
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()

                onAuthComplete(authResult)

            }
        } catch (e: ApiException) {
            Log.d("GoogleAuth", e.toString())
            onAuthError(e)
        }
    }
}


private fun registerUser(
    context: Context,
    auth: FirebaseAuth,
    email: String,
    password: String,
    onResult: (Boolean) -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Registration successful
                val user = auth.currentUser
                Log.d("Registration", "User registered successfully")
                onResult(true) // Пользователь успешно зарегистрирован
            } else {
                // Registration failed
                val exception = task.exception
                if (exception is FirebaseAuthUserCollisionException) {
                    // Пользователь уже существует, попытаемся войти
                    signInUser(context, auth, email, password, onResult)
                } else {
                    // Другая ошибка, обработаем ее
                    val message = exception?.message ?: "Unknown error"
                    Log.d("Registration", "Registration failed: $message")
                    showToast(context, "Registration failed: $message")
                    onResult(false) // Регистрация не удалась
                }
            }
        }
}

private fun signInUser(
    context: Context,
    auth: FirebaseAuth,
    email: String,
    password: String,
    onResult: (Boolean) -> Unit
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Вход успешный
                Log.d("Registration", "User signed in successfully")
                onResult(true) // Пользователь успешно вошел
            } else {
                // Вход не удался
                val exception = task.exception
                val message = exception?.message ?: "Unknown error"
                Log.d("Registration", "Sign in failed: $message")
                showToast(context, "Sign in failed: $message")
                onResult(false) // Вход не удался
            }
        }
}

private fun showToast(context: Context, message: String)
{
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}