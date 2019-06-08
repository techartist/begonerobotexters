package com.webnation.begonerobotexters.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.webnation.begonerobotexters.utils.AndroidText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

class EulaViewModel(application: Application) : AndroidViewModel(application) {
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    suspend fun getText(fileName: String?) : String =
        scope.async(Dispatchers.IO) { AndroidText(fileName,getApplication()).androidText }.await()

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }


}