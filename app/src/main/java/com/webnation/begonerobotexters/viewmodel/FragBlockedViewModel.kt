package com.webnation.begonerobotexters.viewmodel

import android.app.Application
import android.content.ContentValues
import android.provider.BlockedNumberContract.BlockedNumbers
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.webnation.begonerobotexters.database.PhoneNumber
import com.webnation.begonerobotexters.database.PhoneNumberRepository
import com.webnation.begonerobotexters.datastructures.BlockedNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.CoroutineContext


class FragBlockedViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val repository: PhoneNumberRepository by inject()
    private val blockedNumbers  = arrayListOf<BlockedNumber>()

    val allNumbers: LiveData<List<PhoneNumber>>

    init {
        allNumbers = repository.allNumbers
    }

    suspend fun getBlockedNumbers() = scope.launch(Dispatchers.IO) {
        val list = arrayListOf<String>()
        val c = getApplication<Application>().getContentResolver().query(
            BlockedNumbers.CONTENT_URI,
            arrayOf(BlockedNumbers.COLUMN_ID, BlockedNumbers.COLUMN_ORIGINAL_NUMBER, BlockedNumbers.COLUMN_E164_NUMBER),
            null,
            null,
            null
        )
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    val columnId = c.getInt(c.getColumnIndex(BlockedNumbers.COLUMN_ID))
                    val columnOriginalNumber = c.getString(c.getColumnIndex(BlockedNumbers.COLUMN_ORIGINAL_NUMBER))
                    val columnE164Number = c.getString(c.getColumnIndex(BlockedNumbers.COLUMN_E164_NUMBER))
                    blockedNumbers.add(BlockedNumber(columnId, columnOriginalNumber, columnE164Number))
                    list.add(columnOriginalNumber)
                } while (c.moveToNext())
            }


            repository.updateAllBlockedNumberInDatabase(list)
            c.close()
        }

    }

    fun blockNumberInSystem(phoneNumber: String) = scope.launch(Dispatchers.Default) {
        val values = ContentValues()
        values.put(BlockedNumbers.COLUMN_ORIGINAL_NUMBER, phoneNumber)
        val uri = getApplication<Application>().getContentResolver().insert(BlockedNumbers.CONTENT_URI, values)
    }

    fun deleteNumberInSystem(phoneNumber: String) = scope.launch(Dispatchers.Default) {
        val values = ContentValues()
        values.put(BlockedNumbers.COLUMN_ORIGINAL_NUMBER, phoneNumber)
        val uri = getApplication<Application>().getContentResolver().insert(BlockedNumbers.CONTENT_URI, values)
        getApplication<Application>().getContentResolver().delete(uri, null, null);

    }

    suspend fun updatePhoneNumber(isBlocked: Boolean, phoneNumber: PhoneNumber) = scope.launch(Dispatchers.IO) {
        phoneNumber.numberIsBlocked = if (isBlocked) 1 else 0;
        repository.update(phoneNumber)
    }


    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }


}