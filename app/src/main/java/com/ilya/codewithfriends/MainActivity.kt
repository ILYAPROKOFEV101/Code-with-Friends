
package com.ilya.codewithfriends

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.profile.IMG
import com.ilya.codewithfriends.presentation.profile.UID






class  MainActivity: ComponentActivity() {
    var leftop by mutableStateOf(true)


    var cloth by mutableStateOf(false)

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
        val myValue = PreferenceHelper.getValue("myKey")
        setContent {


            if (myValue == false){
                if (cloth == false) {
                    DeleteRoom()
                }
            }




            //val intent = Intent(this@MainActivity, Main_menu::class.java)
            // startActivity(intent)
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {

            toshear(userData = googleAuthUiClient.getSignedInUser())



                @Composable
                fun SignInScreen(
                    state: SignInState,
                    onSignInClick: () -> Unit,

                    ) {



                    var unvisible by remember {
                        mutableStateOf(false)
                    }



                    var user by remember { mutableStateOf(Firebase.auth.currentUser) }

                    val launcher = rememberFirebaseAuthLauncher(
                        onAuthComplete = { result ->
                            user = result.user
                        },
                        onAuthError = {
                            user = null
                        }
                    )
                    val token = stringResource(id = R.string.web_client_id)
                    val context = LocalContext.current

                    val configuration = LocalConfiguration.current
                    val screenWidthDp = configuration.screenWidthDp
                    val isTablet = screenWidthDp >= 600 // Примерно, для планшета

                    if (isTablet) {
                        Modifier
                            .fillMaxWidth(0.5f) // На планшетах занимает половину ширины
                            .height(60.dp) // Большая высота для планшетов
                    } else {
                        Modifier
                            .fillMaxWidth() // На телефонах занимает всю ширину
                            .height(48.dp) // Стандартная высота для телефонов
                    }




                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        if (!leftop) {
                            Box(
                                modifier = Modifier
                                    .height(400.dp)
                                    .align(Alignment.Center)
                                    .padding(top = 100.dp)
                            ) {
                                LoadingCircle()

                                toshear(userData = googleAuthUiClient.getSignedInUser())


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
                                        onSignInClick()
                                        val gso =
                                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                .requestIdToken(token)
                                                .requestEmail()
                                                .build()
                                        val googleSignInClient =
                                            GoogleSignIn.getClient(context, gso)
                                        launcher.launch(googleSignInClient.signInIntent)

                                        leftop = !leftop

                                        unvisible = !unvisible// не ведимый

                                        PreferenceHelper.setShowElement(
                                            context,
                                            true
                                        ) // !!!!!важный элемент
                                    }) {

                                        Image(
                                            painter = painterResource(id = R.drawable.google),
                                            contentDescription = "Nothing",

                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                        )

                                    }
                                    Spacer(modifier = Modifier.height(30.dp))

                                    Text(stringResource(id = R.string.login))

                                    Spacer(modifier = Modifier.height(30.dp))

                                    /*Button(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .padding(start = 20.dp, end = 20.dp),

                                        onClick = {
                                            val intent = Intent(this@MainActivity, Main_menu::class.java)
                                            startActivity(intent)
                                    }) {
                                        Text(text = "If you are a developer or tester and you don’t have an account, click here")
                                    }*/
                                }
                            }

                        } else {
                            if (!unvisible) {
                                Button(onClick = {
                                    Firebase.auth.signOut()
                                    user = null
                                }) {}
                            }
                        }

                    }
                }
                ComposeGoogleSignInCleanArchitectureTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = androidx.compose.material.MaterialTheme.colors.background
                    ) {


                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = "sign_in") {

                            composable("sign_in") {
                                val viewModel = viewModel<SignInViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                LaunchedEffect(key1 = Unit) {
                                    if (googleAuthUiClient.getSignedInUser() != null) {
                                        navController.navigate("profile")
                                    }
                                }
                                val launcher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                                    onResult = {
                                            result ->
                                        if (result.resultCode == RESULT_OK) {
                                            lifecycleScope.launch {
                                                val signInResult =
                                                    googleAuthUiClient.signInWithIntent(
                                                        intent = result.data ?: return@launch
                                                    )
                                                viewModel.onSignInResult(signInResult)
                                            }
                                        }
                                    }
                                )

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

                                    }

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

                        }

                    }
                }

            }
        }
    }
@OptIn(ExperimentalMaterial3Api::class)
@Preview
    @Composable
    fun DeleteRoom() {
        var show by remember {
            mutableStateOf(true)
        }
                if(show == true) {
                    AlertDialog(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 30.dp, bottom = 30.dp)
                            .clip(
                                RoundedCornerShape(20.dp)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        onDismissRequest = { /* ... */ },
                        title = { Text(text = stringResource(id = R.string.Privacy_Policy),
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()) },

                        buttons = {
                            Column(modifier = Modifier.height(600.dp)) {

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(500.dp)
                                        .padding(5.dp)
                                ) {

                                    item {
                                        Text(text = stringResource(id = R.string.policy))
                                    }
                                }
                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(bottom = 10.dp),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Button(
                                        onClick = {
                                            cloth = true
                                            PreferenceHelper.saveValue("myKey", true)
                                        },
                                        colors = ButtonDefaults.buttonColors(Color(0xFF29B630)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(20.dp))
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.I_agree),
                                            color = Color.White
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            finish()
                                        },
                                        colors = ButtonDefaults.buttonColors(Color(0xFFFA0505)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(15.dp))
                                    ) {
                                        Text(
                                            stringResource(id = R.string.I_dont_agree),
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                        }
                    )
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

