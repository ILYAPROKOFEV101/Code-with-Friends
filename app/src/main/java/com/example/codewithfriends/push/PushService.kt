package com.example.codewithfriends.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.codewithfriends.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Проверяем, есть ли уведомление во входящем сообщении
        remoteMessage.notification?.let {
            val title = it.title
            val body = it.body

            // Показываем уведомление
            title?.let {
                showNotification(title, body ?: "")
            }
        }
    }

    private fun showNotification(title: String, message: String) {
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

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.android) // Укажите свою иконку уведомления

        notificationManager.notify(1, notificationBuilder.build())
    }
}
