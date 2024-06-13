package com.ilya.codewithfriends.Aboutusers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ilya.codewithfriends.R
import com.ilya.codewithfriends.Aboutusers.ui.theme.CodeWithFriendsTheme
import com.ilya.codewithfriends.Sendban.Sendban

import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class Aboutuser : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )

        val userId = intent.getStringExtra("userId")
        setContent {
            CodeWithFriendsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val user = remember { mutableStateOf(emptyList<userr>()) }
                    getData(user, "$userId")
                    LazyColumn(modifier = Modifier.fillMaxSize()){

                        item {
                            icon(user.value)
                        }
                        item {
                            aboutuser(user.value)
                        }

                        if(id != userId){
                            item {
                                Complaint("$userId")
                                }
                        }


                    }

                }
            }

        }




    }


    @Composable
    fun icon(user: List<userr>) {
        val firstUser = user.firstOrNull() // Получаем первый элемент из списка или null, если список пуст
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)

        ) {
            Image(
                painter = if (firstUser?.url?.isNotEmpty() == true) {
                    // Load image from URL if URL is not empty
                    rememberImagePainter(data = firstUser.url)
                } else {
                    // Load a default image when URL is empty
                    painterResource(id = R.drawable.android) // Replace with your default image resource
                },
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .align(Alignment.Center) ,
                contentScale = ContentScale.Crop
            )
        }
    }



    @Composable
        fun aboutuser(user: List<userr>){
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()

                    .height(700.dp)
            ) {
                items(user) { user ->
                   // age(user)
                   // Spacer(modifier = Modifier.height(10.dp))
                    aboutme(user)
                }
            }
        }


    @Composable
    fun age(user: userr) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            colors = CardDefaults.cardColors(Color.White),

            ) {
            Box(modifier = Modifier
                .fillMaxSize(),){
                Text(text = "Age: ${user.age}", fontSize = 24.sp, modifier = Modifier.align(Alignment.Center))
            }

        }
    }


    @Composable
    fun aboutme(user: userr) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(700.dp)
                .clip(RoundedCornerShape(30.dp))
                .padding(start = 8.dp, end = 8.dp, top = 20.dp),
            shape = RoundedCornerShape(30.dp),
            border = BorderStroke(8.dp, SolidColor(Color.Blue)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(Color.White),

        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp, top = 20.dp),){
                Text(text = "${user.aboutme}", fontSize = 24.sp)
            }

        }
    }




    private fun getData(user: MutableState<List<userr>>, uid: String) {
        val url = "https://getpost-ilya1.up.railway.app/getuser/$uid"

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                Log.d("Mylog", "Result: $response")
                val gson = Gson()
                val roomListType = object : TypeToken<List<userr>>() {}.type
                val utf8Response = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
                val newRooms: List<userr> = gson.fromJson(utf8Response, roomListType)

                user.value = newRooms
            },
            { error ->
                Log.d("Mylog", "Error: $error")
            }
        )

        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }


    @Composable
    fun Complaint(uid: String){
        Button(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(5.dp), shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFF44336)),
            onClick = {
                val intent = Intent(this@Aboutuser, Sendban::class.java)
                intent.putExtra(
                    "userId",
                    uid
                ) // Здесь вы добавляете данные в Intent
                startActivity(intent)
            }) {
            Text(
                text = stringResource(id = R.string.Complaint),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }



}




