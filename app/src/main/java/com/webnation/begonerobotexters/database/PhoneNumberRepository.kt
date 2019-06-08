package com.webnation.begonerobotexters.database


import android.provider.BlockedNumberContract
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import org.koin.core.KoinComponent


class PhoneNumberRepository(private val phoneNumberDAO: PhoneNumberDAO)  {

    val allNumbers: LiveData<List<PhoneNumber>> =  phoneNumberDAO.getAllNumbers()

    val allBlockedNumber: LiveData<List<PhoneNumber>> =  phoneNumberDAO.getNumbersBlocked()

    fun insert(phoneNumber: PhoneNumber) {
        phoneNumberDAO.insert(phoneNumber)
    }

    @WorkerThread
    suspend fun deleteAllNumbers() {
        phoneNumberDAO.deleteAll()
    }

    fun countNumberOfTimesTexted(number: String) : Int {
        return phoneNumberDAO.getCountOfTimesTexted(number)
    }

    fun getPhoneNumber(number: String) : PhoneNumber? {
        return phoneNumberDAO.getPhoneNumber(number)
    }

    @WorkerThread
    suspend fun update(phoneNumber: PhoneNumber) : Int {
        return phoneNumberDAO.updatePhoneNumber(phoneNumber)
    }

    @WorkerThread
    suspend fun delete(vararg phoneNumbers: PhoneNumber) {
        phoneNumberDAO.deletePhoneNumber(*phoneNumbers)
    }

    @WorkerThread
    suspend fun doesNumberExistInDatabase(number: String) : Boolean {
        return phoneNumberDAO.doesNumberExist(number) > 0
    }

    @WorkerThread
    suspend fun isNumberBlocked(number: String) : Boolean {
        return phoneNumberDAO.isNumberBlocked(number) == 1
    }

    @WorkerThread
    suspend fun updateAllBlockedNumberInDatabase(blockedNumbers: List<String>) {
        phoneNumberDAO.updatePhoneNumbersThatAreBlockedInSystem(blockedNumbers)
    }


}