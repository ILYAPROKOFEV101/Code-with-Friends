package com.example.codewithfriends.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.widget.EditText

import androidx.compose.foundation.Image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import coil.compose.rememberImagePainter
import com.example.codewithfriends.R
import com.example.codewithfriends.chats.Chat
import com.example.codewithfriends.chats.WebSocketClient
import com.example.reaction.logik.PreferenceHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso
import okio.IOException

import androidx.compose.material.TextField

class PushService : FirebaseMessagingService() {


    private lateinit var conversation: EditText // Или другой тип поля для ввода текста
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Проверяем, есть ли уведомление во входящем сообщении
        // Извлечение данных из уведомления
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val senderName = remoteMessage.data["senderName"]
        val senderIcon = remoteMessage.data["senderIcon"]

        // Показ уведомления
        title?.let {
            showNotification(title, body ?: "", senderName, senderIcon)
        }
    }

    private fun showNotification(
        title: String,
        message: String,
        senderName: String?,
        senderIcon: String?
    ) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Конфигурация уведомления
        val channelId = "channelId"
        val channelName = "Channel Name"
        val importance = NotificationManager.IMPORTANCE_HIGH

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }


        // Key for the string that's delivered in the action's intent.

        // Key for the string that's delivered in the action's intent.
       val KEY_TEXT_REPLY = "key_text_reply"
        var replyLabel: String = resources.getString(R.string.reply_label)
        var remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
            setLabel(replyLabel)
            build()
        }


        var replyPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                applicationContext,
                getConversationId(),
                getMessageReplyIntent(getConversationId()),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE  // Используйте флаг FLAG_MUTABLE
            )


        // Create the reply action and add the remote input.
        var action: NotificationCompat.Action =
            NotificationCompat.Action.Builder(R.drawable.send,
                getString(R.string.label), replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build()



        // Создайте NotificationBuilder
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setColor(Color.BLUE)
            .setSmallIcon(R.drawable.send)

        // Добавьте поле text
        notificationBuilder.setContentText(message)
            .addAction(action)




        // Проверьте, есть ли информация о отправителе и его иконке
        if (senderName != null && !senderIcon.isNullOrEmpty()) {
            // Загрузите изображение по URL с использованием Picasso
            try {
                val senderIconBitmap = Picasso.get().load(senderIcon).get()

                // Обрежьте изображение отправителя для создания круглого эффекта
                val roundedSenderIconBitmap = getCircularBitmap(senderIconBitmap)

               // pushmessge(this, "hello")
                // Создайте MessagingStyle
                val messagingStyle =
                    NotificationCompat.MessagingStyle(Person.Builder().setName("Me").build())

                // Добавьте сообщение
                val messageBuilder = NotificationCompat.MessagingStyle.Message(
                    message,
                    System.currentTimeMillis(),
                    Person.Builder().setName(senderName)
                        .setIcon(IconCompat.createWithBitmap(roundedSenderIconBitmap)).build()
                )

                messagingStyle.addMessage(messageBuilder)

                // Примените MessagingStyle к NotificationBuilder
                notificationBuilder.setStyle(messagingStyle)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }




        // Показать уведомление
        notificationManager.notify(1, notificationBuilder.build())
    }


    private fun getConversationId(): Int {
        // Замените на вашу логику получения ID беседы
        return 1
    }

    private fun getMessageReplyIntent(conversationId: Int): Intent {
        // Замените на вашу логику создания Intent для ответа на сообщение
        val intent = Intent(this, Chat::class.java)
        // Добавьте необходимые параметры
        return intent
    }
}


private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
    val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val paint = Paint()
    val rect = Rect(0, 0, bitmap.width, bitmap.height)

    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    canvas.drawCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(), (bitmap.width / 2).toFloat(), paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)

    return output
}

fun pushmessge(context: Context, message: String){
    // Получение значения для ключа KEY_STRING_1 (id)
    val idValue = PreferenceHelper.getSid(context)

// Получение значения для ключа KEY_STRING_2 (name)
    val nameValue = PreferenceHelper.getname(context)

// Получение значения для ключа KEY_STRING_3 (img)
    val imgValue = PreferenceHelper.getimg(context)

// Получение значения для ключа KEY_STRING_4 (socket)
    val socketValue = PreferenceHelper.getSoket(context)

    val webSocketClient = WebSocketClient("$socketValue", "$nameValue", "$imgValue", "$idValue")
    webSocketClient.connect()

// Отправка сообщения
    webSocketClient.sendMessage("$message")

// Отключение после отправки сообщения
    webSocketClient.disconnect()
}




class ReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val remoteInput = intent?.let { RemoteInput.getResultsFromIntent(it) }

        if (remoteInput != null) {
            val replyText = remoteInput.getCharSequence("key_text_reply")

            // Теперь у вас есть введенный текст, который можно использовать
            if (replyText != null) {
                val notificationId = intent?.getIntExtra("notificationId", 0)
                Log.d("ReplyReceiver", "Received reply: $replyText")

                // Обработайте введенный текст по вашему усмотрению
                // например, отправьте его в чат или обновите уведомление с ответом
            }
        }
    }
}

