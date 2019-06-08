package com.webnation.begonerobotexters.database


import android.provider.BlockedNumberContract
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PhoneNumberDAO {

    @Query("SELECT * from blocked_numbers ORDER BY datetime(last_date_seen) ASC")
    fun getAllNumbers(): LiveData<List<PhoneNumber>>

    @Insert
    fun insert(phoneNumber: PhoneNumber)

    @Delete
    fun deletePhoneNumber(vararg phoneNumber: PhoneNumber) : Int

    @Update
    fun updatePhoneNumber(vararg phoneNumber: PhoneNumber) : Int

    @Query("DELETE FROM blocked_numbers")
    fun deleteAll()

    @Query("SELECT Count(*) From blocked_numbers")
    fun getCountOfNumbers() : Int

    @Query("SELECT number_of_times_seen From blocked_numbers WHERE blocked_number = :number")
    fun getCountOfTimesTexted(number: String) : Int

    @Query("SELECT * From blocked_numbers WHERE number_is_blocked = 1")
    fun getNumbersBlocked() : LiveData<List<PhoneNumber>>

    @Query("SELECT Count(*) From blocked_numbers WHERE blocked_number = :number")
    fun doesNumberExist(number: String) : Int

    @Query("SELECT * From blocked_numbers WHERE blocked_number = :number")
    fun getPhoneNumber(number: String) : PhoneNumber?

    @Query("SELECT number_is_blocked From blocked_numbers WHERE blocked_number = :number")
    fun isNumberBlocked(number: String) : Int

    @Query("UPDATE blocked_numbers SET number_is_blocked = 1 WHERE blocked_number IN (:listBlockedNumbers)")
    fun updatePhoneNumbersThatAreBlockedInSystem(listBlockedNumbers: List<String>) 




}