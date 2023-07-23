package com.example.codewithfriends



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
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.reaction.logik.PreferenceHelper
import com.example.codewithfriends.presentation.profile.ProfileScreen
import com.example.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.example.codewithfriends.presentation.sign_in.SignInScreen
import com.example.codewithfriends.presentation.sign_in.SignInState
import com.example.codewithfriends.presentation.sign_in.SignInViewModel
import com.example.codewithfriends.presentation.sign_in.UserData
import com.example.codewithfriends.ui.theme.ComposeGoogleSignInCleanArchitectureTheme
import com.example.codewithfriends.ui.theme.Main_menu

import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.sign
@Suppress("DEPRECATION", "UNREACHABLE_CODE")
class  MainActivity: ComponentActivity() {


//val intent = Intent(this@tester, Main_menu::class.java)
//startActivity(intent)

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            toshear(userData = googleAuthUiClient.getSignedInUser())

            @Composable
            fun SignInScreen(
                state: SignInState,
                onSignInClick: () -> Unit,

                ) {
                var unvisible by remember {
                    mutableStateOf(false)
                }

                var leftop by remember {
                    mutableStateOf(true)
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
                val screenHeight = configuration.screenHeightDp.dp
                val screenWidth = configuration.screenWidthDp.dp

                //  val isTablet = configuration.smallestScreenWidthDp >= 600

                //  val configuration = resources.configuration
                val smallestScreenWidthDp = configuration.smallestScreenWidthDp

                val isTablet = smallestScreenWidthDp >= 600

                val pading = if (isTablet) 800.dp else 700.dp


                //val configuration = LocalConfiguration.current
                val screenHeightDp = configuration.screenHeightDp

                val rowHeight = if (screenHeightDp <= 900) {
                    140.dp // Высота для телефона
                } else {
                    300.dp // Высота для планшета
                }



                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    if(!leftop) {
                        Box(modifier = Modifier
                            .height(400.dp)
                            .align(Alignment.Center)
                            .padding(top = 100.dp)) {
                            LoadingCircle()

                            toshear(userData = googleAuthUiClient.getSignedInUser())


                        }
                    }





                    if (user == null) {
                        //Text("Not logged in")




                        if (!unvisible) {


                            Column(modifier = Modifier
                                .wrapContentSize(),verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {






                                IconButton(onClick = {
                                    onSignInClick()
                                    val gso =
                                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestIdToken(token)
                                            .requestEmail()
                                            .build()


                                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
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
                            }
                        }

                    } else {
                        if (!unvisible) {
                            Button(onClick = {
                                Firebase.auth.signOut()
                                user = null }){}

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
                    NavHost(navController = navController, startDestination = "sign_in") {
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
                                onResult = { result ->
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
                            ProfileScreen(

                                userData = googleAuthUiClient.getSignedInUser(),

                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Goodbye",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.popBackStack()
                                    }

                                }
                            )





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

