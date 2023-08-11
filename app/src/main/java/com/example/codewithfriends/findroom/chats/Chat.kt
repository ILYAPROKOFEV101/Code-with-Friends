package com.example.codewithfriends.findroom.chats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codewithfriends.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket


class Chat : ComponentActivity() {

    private val roomId by lazy { intent.getStringExtra("roomId") }
    private val pieSocketListener = PieSocketListener()
    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket
    private var text by mutableStateOf("")
    private val messages = mutableStateListOf<String>()
    private var submittedText by mutableStateOf("")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {


            Creator()
        }
        setupWebSocket()
    }



     fun setupWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder().url("https://getpost-ilya1.up.railway.app/chat/t9iSQ1cSEp").build()
        webSocket = client.newWebSocket(request, pieSocketListener)
    }







    @Preview(showBackground = true)
    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun Creator() {
        val keyboardController = LocalSoftwareKeyboardController.current
        var textSize by remember { mutableStateOf(20.sp) }
        var text by remember { mutableStateOf("") }
        var submittedText by remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue),
            contentAlignment = Alignment.BottomCenter
        ) {


            Row(
                modifier = Modifier
                    .background(Color.White)
                    .padding(start = 8.dp, end = 25.dp)


                ) {
                TextField(
                    modifier = Modifier.weight(0.7f),
                    value = text,
                    onValueChange = { text = it },
                    textStyle = TextStyle(fontSize = textSize),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White,
                        disabledIndicatorColor = Color.White,
                        containerColor = Color.White
                    ),

                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    maxLines = 10 // Устанавливаем максимальное количество строк, чтобы TextField мог увеличиваться по высоте
                )
            }
            Box(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                IconButton(
                    modifier = Modifier.padding(bottom = 7.dp,end = 5.dp),
                    onClick = {
                        submittedText = text
                        text = ""

                        val messageToSend = "$submittedText"
                        pieSocketListener.sendMessage(webSocket ,messageToSend)

                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.send),
                        contentDescription = "Send"
                    )
                }
            }
        }
    }


        }






