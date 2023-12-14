package com.example.codewithfriends.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import androidx.core.graphics.drawable.IconCompat
import coil.compose.rememberImagePainter
import com.example.codewithfriends.R
import com.example.codewithfriends.chats.Chat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso
import okio.IOException

class PushService : FirebaseMessagingService() {

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

        // Создайте NotificationBuilder
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setColor(Color.BLUE)
            .setSmallIcon(R.drawable.send)

        // Добавьте кнопку к уведомлению
        val intent = Intent(this, Chat::class.java)
        intent.putExtra("your_extra_key", "your_extra_value")
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val action = NotificationCompat.Action.Builder(
            R.drawable.send,  // Замените на свою иконку кнопки
            "reply to message",            // Текст кнопки
            pendingIntent
        ).build()

        notificationBuilder.addAction(action)

        // Проверьте, есть ли информация о отправителе и его иконке
        if (senderName != null && !senderIcon.isNullOrEmpty()) {
            // Загрузите изображение по URL с использованием Picasso
            try {
                val senderIconBitmap = Picasso.get().load(senderIcon).get()

                // Обрежьте изображение отправителя для создания круглого эффекта
                val roundedSenderIconBitmap = getCircularBitmap(senderIconBitmap)

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
