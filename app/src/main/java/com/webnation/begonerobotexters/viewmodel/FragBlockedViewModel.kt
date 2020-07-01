package com.webnation.begonerobotexters.viewmodel

import android.app.Application
import android.content.ContentValues
import android.provider.BlockedNumberContract.BlockedNumbers
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.webnation.begonerobotexters.database.PhoneNumber
import com.webnation.begonerobotexters.database.PhoneNumberRepository
import com.webnation.begonerobotexters.datastructures.BlockedNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragBlockedViewModel(application: Application, val repository: PhoneNumberRepository) : AndroidViewModel(application) {
    private val blockedNumbers  = arrayListOf<BlockedNumber>()

    val allNumbers: LiveData<List<PhoneNumber>>

    init {
        allNumbers = repository.allNumbers
    }

    suspend fun getBlockedNumbers() = viewModelScope.launch(Dispatchers.IO) {
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

    fun blockNumberInSystem(phoneNumber: String) = viewModelScope.launch(Dispatchers.Default) {
        val values = ContentValues()
        values.put(BlockedNumbers.COLUMN_ORIGINAL_NUMBER, phoneNumber)
        val uri = getApplication<Application>().getContentResolver().insert(BlockedNumbers.CONTENT_URI, values)
    }

    fun deleteNumberInSystem(phoneNumber: String) = viewModelScope.launch(Dispatchers.Default) {
        val values = ContentValues()
        values.put(BlockedNumbers.COLUMN_ORIGINAL_NUMBER, phoneNumber)
        val uri = getApplication<Application>().getContentResolver().insert(BlockedNumbers.CONTENT_URI, values)
        getApplication<Application>().getContentResolver().delete(uri, null, null);
    }

    fun updatePhoneNumber(isBlocked: Boolean, phoneNumber: PhoneNumber) = viewModelScope.launch(Dispatchers.IO) {
        phoneNumber.numberIsBlocked = if (isBlocked) 1 else 0;
        repository.update(phoneNumber)
    }
}