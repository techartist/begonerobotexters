package com.webnation.begonerobotexters.services

import android.app.Application
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.telephony.SmsMessage
import com.webnation.begonerobotexters.MainActivity
import com.webnation.begonerobotexters.database.PhoneNumber
import com.webnation.begonerobotexters.database.PhoneNumberDAO
import com.webnation.begonerobotexters.database.PhoneNumberDatabase
import com.webnation.begonerobotexters.database.PhoneNumberRepository
import com.webnation.begonerobotexters.receivers.MMSBroadcastReceiver.context
import com.webnation.begonerobotexters.viewmodel.FragHomeViewModel.Companion.AUTO_RESPONDER
import com.webnation.begonerobotexters.viewmodel.FragHomeViewModel.Companion.TEXT_STRING
import kotlinx.coroutines.*
import org.joda.time.DateTime
import org.joda.time.Interval
import org.joda.time.format.DateTimeFormat
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import timber.log.Timber
import java.time.format.DateTimeFormatter

class SMSIntentService : IntentService("SMSService"), KoinComponent {

    private val repository: PhoneNumberRepository by inject()
    private val sharedPreferences : SharedPreferences by inject()
    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

    override fun onHandleIntent(intent: Intent?) {
        sendMessages(intent)
    }

    private fun sendMessages(intent : Intent?) {
        val message = sharedPreferences.getString(TEXT_STRING,"")
        val autoResponderOn = sharedPreferences.getBoolean(AUTO_RESPONDER,false)

        var sender: String?
        var bundle = Bundle()
        if (intent != null) {
            bundle = intent.extras as Bundle
        }

        try {
            //parses out the message
            val pdus = bundle.get("pdus") as Array<Any>
            val msgs = Array<SmsMessage?>(pdus.size, {null})
            for (i in msgs.indices) {

                val format = bundle.getString("format")
                msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)

                // Here you have the sender(phone number)
                sender = msgs[i]?.originatingAddress

                if (sender != null) {
                     if (!contactExists(this,sender) &&
                         sender.length >= 10 &&
                         !message.isNullOrEmpty() &&
                         autoResponderOn &&
                         !isNumberBlocked(sender)   &&
                         !isNumberTextedInLast5Minutes(sender)) {
                             val smsManager = SmsManager.getDefault()
                             smsManager.sendTextMessage(sender, null, message, null, null)
                             handleText(sender)

                     }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e)
        }
    }

    fun isNumberTextedInLast5Minutes(number : String) : Boolean {

        var isTextedInLast5Minutes = false

        val phoneNumber = repository.getPhoneNumber(number)
        if (phoneNumber != null) {
            val dateTime = DateTime.parse(phoneNumber.dateLastSeen, formatter)
            val dateTimePlus5Minutes = dateTime.plusMinutes(5)
            val now = DateTime.now()
            val interval = Interval(dateTime, dateTimePlus5Minutes)
            if (interval.contains(now)) {
                isTextedInLast5Minutes = true
            }
        }

        return isTextedInLast5Minutes
    }

    fun isNumberBlocked(number: String) : Boolean {
        val phoneNumber = repository.getPhoneNumber(number)
        return phoneNumber != null && phoneNumber.numberIsBlocked == 1
    }

    fun handleText(number: String) {

        val now = DateTime.now().toLocalDateTime().toString(formatter)
        var phoneNumber = repository.getPhoneNumber(number)
        if (phoneNumber != null) {

            val numberOfTimesTexted = repository.countNumberOfTimesTexted(number)
            phoneNumber.numberOfTimesSeen = numberOfTimesTexted + 1
            phoneNumber.dateLastSeen = now
            GlobalScope.launch(Dispatchers.IO, CoroutineStart.DEFAULT) {
                repository.update(phoneNumber)
            }

        } else {

            val phoneNumberNotNull = PhoneNumber(number)
            phoneNumberNotNull.numberOfTimesSeen = 1
            phoneNumberNotNull.dateLastSeen = now
            phoneNumberNotNull.numberIsBlocked = 0
            repository.insert(phoneNumberNotNull)
        }
    }

    fun contactExists(context: Context,number: String?): Boolean {
        /// number is the phone number
        val lookupUri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )
        val mPhoneNumberProjection = arrayOf(ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME)
        val cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null)
        if (cur != null) {
            try {
                if (cur.moveToFirst()) {
                    return true
                }
            } finally {
                cur.close()
            }
        }
        return false
    }

    companion object {

        fun contactExists(context: Context,number: String?, intent : SMSIntentService): Boolean =
               intent.contactExists(context,number);

        fun handleText(number: String, app: Application, intent : SMSIntentService)  =
            intent.handleText(number)

        fun isNumberBlocked(number: String, app: Application, intent : SMSIntentService) : Boolean =
            intent.isNumberBlocked(number)

        fun isNumberTextedInLast5Minutes(number : String, intent : SMSIntentService) : Boolean =
            intent.isNumberTextedInLast5Minutes(number)

    }

}