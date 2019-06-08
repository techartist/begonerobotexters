package com.webnation.begonerobotexters.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.webnation.begonerobotexters.MainActivity
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import org.koin.core.inject

class FragHomeViewModel(application: Application) : AndroidViewModel(application),KoinComponent {
    private val sharedPreferences: SharedPreferences by inject()

    fun putString(string : String) {
        sharedPreferences.edit().putString(TEXT_STRING, string.toString()).apply()
    }

    fun getString() : String? {
        return sharedPreferences.getString(TEXT_STRING,"")
    }

    fun putBoolean(boolean : Boolean) {
        sharedPreferences.edit().putBoolean(AUTO_RESPONDER, boolean).apply()
    }

    fun getBoolean() : Boolean {
        return sharedPreferences.getBoolean(AUTO_RESPONDER, false)
    }

    fun doesSharedPrefsHaveAutoResponder() : Boolean {
        return sharedPreferences.contains(TEXT_STRING)
    }

    fun doesSharedPrefsContainAutoResponder() : Boolean {
        return sharedPreferences.contains(AUTO_RESPONDER)
    }

    companion object {
        const val TEXT_STRING = "roboTextString"
        const val AUTO_RESPONDER = "autoResponder"
    }


}