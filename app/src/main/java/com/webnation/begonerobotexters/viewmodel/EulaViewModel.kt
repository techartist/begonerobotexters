package com.webnation.begonerobotexters.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.webnation.begonerobotexters.utils.AndroidText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class EulaViewModel(application: Application) : AndroidViewModel(application) {

    suspend fun getText(fileName: String?) : String =
        viewModelScope.async(Dispatchers.IO) { AndroidText(fileName,getApplication()).androidText }.await()

}