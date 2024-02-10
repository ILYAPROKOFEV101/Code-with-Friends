package com.ilya.codewithfriends.chats.ui.theme


/*
class TestActivity : ComponentActivity() {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var isConnected by mutableStateOf(false)

    private val messages = mutableStateOf(listOf<com.example.codewithfriends.chats.Message>()) // Хранение

    private var storedRoomId: String? = null // Объявляем на уровне класса
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storedRoomId = PreferenceHelper.getRoomId(this)

        setContent {



            WebSocketChatScreen(
                 messages.value,
            )


        }
    }

    override fun onResume() {
        super.onResume()

        val name = UID(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val img = IMG(
            userData = googleAuthUiClient.getSignedInUser()
        )
        val ids = ID(
            userData = googleAuthUiClient.getSignedInUser()
        )

        // Автоматическое подключение при входе в активность
        setupWebSocket(storedRoomId!!, "$ids", "$img", "$name")
    }

    override fun onPause() {
        super.onPause()

        // Автоматическое отключение при выходе из активности
        webSocket?.close(1000, "User initiated disconnect")
    }

    private fun setupWebSocket(roomId: String, username: String, url: String, id: String) {
        if (!isConnected) {
            val request: Request = Request.Builder()
                .url("https://getpost-ilya1.up.railway.app/chat/$roomId?username=$username&avatarUrl=$url&uid=$id")
                .build()

            val listener = object : WebSocketListener() {

                override fun onMessage(webSocket: WebSocket, text: String) {
                    val newMessage = com.example.codewithfriends.chats.Message(
                        sender = "",
                        content = text
                    )
                    messages.value = messages.value + newMessage // Add message to the list

                    // Добавьте лог для отслеживания прихода новых сообщений
                    Log.d("WebSocket", "Received message: $text")
                }

                override fun onMessage(webSocket: WebSocket, bytes: okio.ByteString) {
                    // Handle binary messages if needed
                }

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    isConnected = true
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    isConnected = false
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    isConnected = false

                    // Добавьте лог для отслеживания ошибок WebSocket
                    Log.e("WebSocket", "WebSocket failure: ${t.message}")
                }

            }
            webSocket = client.newWebSocket(request, listener)
        }
    }
}

@Composable
fun WebSocketChatScreen(
    messages: List<com.example.codewithfriends.chats.Message>?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // UI for displaying messages using LazyColumn
        LazyColumn {
            items(messages ?: emptyList()) { message ->
                MessageItem(message = message)
            }
        }
    }
}


@Composable
fun MessageItem(message: com.example.codewithfriends.chats.Message) {
    Text(
        text = "${message.sender}: ${message.content}",
        textAlign = TextAlign.Start,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black,
        overflow = TextOverflow.Ellipsis
    )
}
*/
