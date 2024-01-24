package com.example.reaction.logik

import android.content.Context
import android.content.SharedPreferences
import android.os.Message
import android.util.Log
import androidx.core.content.edit
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

object PreferenceHelper {

    private const val PREFERENCE_NAME = "UserPrefs"
    private const val KEY_SHOW_ELEMENT = "showElement"
    private const val KEY_USER_TEXT = "userText"

    private const val KEY_ROOM_ID = "roomId"

    private const val KEY_STRING_1 = "key_string_1"
    private const val KEY_STRING_2 = "key_string_2"
    private const val KEY_STRING_3 = "key_string_3"
    private const val KEY_STRING_4 = "key_string_4"
    private const val KEY_MESSAGE_LIST = "messageList"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }


    private lateinit var appContext: Context
    private val sharedPreferences: SharedPreferences by lazy {
        appContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    fun setShowElement(context: Context, show: Boolean) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(KEY_SHOW_ELEMENT, show)
        editor.apply()
    }
    fun setUserText(context: Context, text: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_USER_TEXT, text)
        editor.apply()
    }

    fun getUserText(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USER_TEXT, "")
    }


    fun saveRoomId(context: Context, roomId: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_ROOM_ID, roomId)
        editor.apply()
    }

    fun getRoomId(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_ROOM_ID, null)
    }




    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun getValue(key: String): Boolean {
        return getSharedPreferences(appContext).getBoolean(key, false)
    }

    fun saveValue(key: String, value: Boolean) {
        getSharedPreferences(appContext).edit {
            putBoolean(key, value)
        }
    }

    fun saveid(context: Context, value: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_STRING_1, value)
        editor.apply()
    }

    fun getSid(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_STRING_1, "")
    }

    fun savename(context: Context, value: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_STRING_2, value)
        editor.apply()
    }

    fun getname(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_STRING_2, "")
    }

    fun saveimg(context: Context, value: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_STRING_3, value)
        editor.apply()
    }

    fun getimg(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_STRING_3, "")
    }

    fun saveSoket(context: Context, value: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_STRING_4, value)
        editor.apply()
    }

    fun getSoket(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_STRING_4, "")
    }



    private fun getMessageList(context: Context): List<Message> {
        val jsonString = getSharedPreferences(context).getString(KEY_MESSAGE_LIST, "")
        return if (jsonString.isNullOrBlank()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Message>>() {}.type
            Gson().fromJson(jsonString, type)
        }
    }

    fun clearAllMessages(context: Context) {
        getSharedPreferences(context).edit().clear().apply()
        Log.d("PreferenceHelper", "Cleared all messages")
    }


    fun getAllMessages(context: Context): List<com.example.codewithfriends.chats.Message> {
        val jsonString = getSharedPreferences(context).getString(KEY_MESSAGE_LIST, null)
        return Gson().fromJson(jsonString, object : TypeToken<List<com.example.codewithfriends.chats.Message>>() {}.type)
            ?: emptyList()
    }


    fun extractTimeFromString(input: String): String? {
        val pattern = "<time>([^<]+)</time>".toRegex()
        val matchResult = pattern.find(input)
        return matchResult?.groups?.get(1)?.value
    }


   /* fun findLatestTime(messages: List<com.example.codewithfriends.chats.Message>): Long? {
        var latestTime: Long? = null

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        for (message in messages) {
            val time = extractTimeFromString(message.content)
            if (time != null) {
                val date = dateFormat.parse(time)
                val timeMillis = date?.time
                if (timeMillis != null && (latestTime == null || timeMillis > latestTime)) {
                    latestTime = timeMillis
                }
            }
        }

        // Вывести лог с самым поздним временем
        Log.d("LatestTime", "Latest time: $latestTime")

        return latestTime
    }*/





    fun saveMessages(context: Context, messages: List<com.example.codewithfriends.chats.Message>) {
        val currentMessagesSet = getMessageList(context)
            .mapNotNull { it as? com.example.codewithfriends.chats.Message }
            .toMutableSet()

        // Добавление новых сообщений в множество
        currentMessagesSet.addAll(messages)

        // Вывод логов для каждого сообщения
        for (message in currentMessagesSet) {
            Log.d("PreferenceHelper", "Saved message: $message")
        }

        val jsonString = Gson().toJson(currentMessagesSet.toList())
        getSharedPreferences(context).edit().putString(KEY_MESSAGE_LIST, jsonString).apply()

        // Вывести лог с сохраненными сообщениями для проверки
        Log.d("PreferenceHelper", "Saved messages: $jsonString")
    }








}


