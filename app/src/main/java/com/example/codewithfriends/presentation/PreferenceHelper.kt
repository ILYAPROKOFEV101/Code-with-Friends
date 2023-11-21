package com.example.reaction.logik

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.BoxScope
import androidx.core.content.edit

object PreferenceHelper {

    private const val PREFERENCE_NAME = "UserPrefs"
    private const val KEY_SHOW_ELEMENT = "showElement"
    private const val KEY_USER_TEXT = "userText"

    private const val KEY_ROOM_ID = "roomId"


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

}


