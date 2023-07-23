package com.example.reaction.logik

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.BoxScope

object PreferenceHelper {

    private const val PREFERENCE_NAME = "UserPrefs"
    private const val KEY_SHOW_ELEMENT = "showElement"
    private const val KEY_USER_TEXT = "userText"



    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
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



}


