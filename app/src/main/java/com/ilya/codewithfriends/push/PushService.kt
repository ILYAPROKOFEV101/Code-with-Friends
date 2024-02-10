package com.ilya.codewithfriends.push

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

import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import com.ilya.codewithfriends.R

import com.ilya.codewithfriends.chats.Chat
import com.ilya.codewithfriends.chats.WebSocketClient
import com.ilya.codewithfriends.push.PushService.Companion.KEY_TEXT_REPLY
import com.ilya.reaction.logik.PreferenceHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso

class YourBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == "your_action") {
            val conversationId = intent.getIntExtra("conversation_id", 0)
            // Получите текст ответа
            val replyText = getMessageText(intent)
            Log.d("YourBroadcastReceiver", "Reply Text: $replyText")
            if (replyText != null) {
                // Обработайте текст ответа, например, отправьте его на сервер
                // ...


                Log.d("PushService", "Получено новое сообщение. Отображение уведомления. $replyText")

                pushmessge(context,"$replyText")

            }
        }
    }
}

fun getMessageText(intent: Intent): CharSequence? {
    return RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY)
}


class PushService : FirebaseMessagingService() {

    // Дополнительная переменная для хранения сообщений
    private val messages: MutableList<NotificationCompat.MessagingStyle.Message> = mutableListOf()

    companion object {
        const val KEY_TEXT_REPLY = "key_text_reply"
        // Ваши остальные константы здесь...
    }

  //  private val KEY_TEXT_REPLY = "key_text_reply"

    private lateinit var conversation: EditText // Или другой тип поля для ввода текста

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Проверяем, есть ли уведомление во входящем сообщении
        // Извлечение данных из уведомления
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val senderName = remoteMessage.data["senderName"]
        val senderIcon = remoteMessage.data["senderIcon"]

        // Создаем intent для использования внутри showNotification
        val intent = Intent(this, Chat::class.java)
        intent.action = "your_action" // ваше действие

        title?.let {
            showNotification(title, body ?: "", senderName, senderIcon)
        }





    }



    private fun showNotification(
        title: String,
        message: String,
        senderName: String?,
        senderIcon: String?,

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


        Log.d("Notification", "Creating PendingIntent")
        var replyPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                applicationContext,
                getConversationId(),
                getMessageReplyIntent(getConversationId(), this),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        Log.d("Notification", "PendingIntent created successfully")


        var replyLabel: String = resources.getString(R.string.reply_label)
        var remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
            setLabel(replyLabel)
            build()
        }

        // Создайте экземпляр действия для ответа и добавьте дистанционный ввод.
        val action: NotificationCompat.Action = NotificationCompat.Action.Builder(
            R.drawable.send,
            getString(R.string.label),
            replyPendingIntent
        )
            .addRemoteInput(remoteInput)
            .build()

// Создайте строитель уведомлений.
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setColor(Color.BLUE)
            .setSmallIcon(R.drawable.send)
            .addAction(action)
            .setContentText(message)

            // Получите данные отправителя из SharedPreferences.
        val nameValue = PreferenceHelper.getname(this)
        val imgValue = PreferenceHelper.getimg(this)

// Загрузите изображение отправителя с использованием Picasso.
        val senderIconBitmap = Picasso.get().load(imgValue).get()

// Создайте круглое изображение отправителя.
        val roundedSenderIconBitmap = getCircularBitmap(senderIconBitmap)

// Создайте объект MessagingStyle для отображения разговора в уведомлении.
        val messagingStyle = NotificationCompat.MessagingStyle(
            Person.Builder()
                .setName(nameValue)
                .setIcon(IconCompat.createWithBitmap(roundedSenderIconBitmap))
                .build()
        )



// Загрузите изображение отправителя из другого источника (предполагается, что это строковый URL).
        val senderIconBitmaps = Picasso.get().load(senderIcon).get()

// Создайте круглое изображение отправителя для использования в сообщении разговора.
        val roundedSenderIconBitmaps = getCircularBitmap(senderIconBitmaps)

// Создайте объект сообщения разговора.
        val userMessageBuilder = NotificationCompat.MessagingStyle.Message(
            message,
            System.currentTimeMillis(),
            Person.Builder()
                .setName(senderName)
                .setIcon(IconCompat.createWithBitmap(roundedSenderIconBitmaps))
                .build()
        )

        // Добавьте сообщение в объект MessagingStyle.
        messagingStyle.addMessage(userMessageBuilder)

        // Примените объект MessagingStyle к уведомлению.
        notificationBuilder.setStyle(messagingStyle)

// Установите текст сообщения в уведомлении.
        notificationBuilder.setContentText(message)

// Отправьте уведомление с помощью NotificationManager.
        notificationManager.notify(getConversationId(), notificationBuilder.build())


        // Обновляем уведомление
            //notificationManager.notify(getConversationId(), updatedNotification)




        val notificationId = getConversationId()  // Замените эту часть на ваш способ получения уникального идентификатора


        val currentNotification = notificationManager.activeNotifications.find {
            it.id == notificationId
        }


    }





    fun getConversationId(): Int {
        // Генерируем уникальный идентификатор, например, на основе времени
        return System.currentTimeMillis().toInt()
    }




    fun getMessageReplyIntent(conversationId: Int, context: Context): Intent {
        val intent = Intent(this, YourBroadcastReceiver::class.java)
        intent.action = "your_action" // ваше действие
        intent.putExtra("conversation_id", conversationId)
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

fun pushmessge(context: Context, message: CharSequence){
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





