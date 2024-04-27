package com.ilya.reaction.logik

import android.content.Context
import android.content.SharedPreferences
import android.os.Message
import android.util.Log
import androidx.core.content.edit
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

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
    // Константы для работы с SharedPreferences
    private const val SHARED_PREFERENCES_NAME = "YourSharedPreferencesName"


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
        return getSharedPreferences(context).getString(KEY_ROOM_ID, "")
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


    fun getMessageList(context: Context): List<Message> {
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








    // Ваша функция для загрузки сообщений из памяти
    fun loadMessagesFromMemory(context: Context): List<com.ilya.codewithfriends.chats.Message> {
        val jsonString = getSharedPreferences(context).getString(KEY_MESSAGE_LIST, "")
        Log.d("Debug", "Loaded JSON: $jsonString") // Добавьте эту строку для отладки
        return if (jsonString.isNullOrBlank()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<com.ilya.codewithfriends.chats.Message>>() {}.type
            val loadedMessages = Gson().fromJson<List<com.ilya.codewithfriends.chats.Message>>(jsonString, type)
            Log.d("Debug", "Loaded messages: $loadedMessages") // Добавьте эту строку для отладки
            loadedMessages
        }
    }





    fun saveMessages(context: Context, messages: List<com.ilya.codewithfriends.chats.Message>) {
        val currentMessagesSet: MutableSet<com.ilya.codewithfriends.chats.Message> = getMessageList(context)
            .mapNotNull { it as? com.ilya.codewithfriends.chats.Message }
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



    // Функция для сохранения строки по уникальному ключу в SharedPreferences
    fun saveDataToSharedPreferences(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    // Функция для получения строки из SharedPreferences по уникальному ключу
    fun getDataFromSharedPreferences(context: Context, key: String): String? {
        val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }



}


