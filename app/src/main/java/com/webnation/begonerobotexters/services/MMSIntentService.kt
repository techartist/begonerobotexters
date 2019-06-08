package com.webnation.begonerobotexters.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.telephony.SmsManager
import android.text.TextUtils
import com.webnation.begonerobotexters.MainActivity
import com.webnation.begonerobotexters.database.PhoneNumberRepository
import com.webnation.begonerobotexters.datastructures.MMSMessage
import com.webnation.begonerobotexters.utils.MMSUtils
import com.webnation.begonerobotexters.viewmodel.FragBlockedViewModel
import com.webnation.begonerobotexters.viewmodel.FragHomeViewModel
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import timber.log.Timber

class MMSIntentService : IntentService("MMSService"), KoinComponent {
    lateinit var context: Context
    private val sharedPreferences : SharedPreferences by inject()

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    override fun onHandleIntent(intent: Intent?) {
        val textMessages = MMSUtils.getMessagesFrom(this, intent)
        sendMessages(textMessages)
    }

    /**
     * As the name implies - takes the parsed message and send it.
     * @param textMessages - List of TextMessages to send
     */
    private fun sendMessages(textMessages: List<MMSMessage>): Void? {
        val message = sharedPreferences.getString(FragHomeViewModel.TEXT_STRING,"")
        val autoResponderOn = sharedPreferences.getBoolean(FragHomeViewModel.AUTO_RESPONDER,false)
        for (textMessage in textMessages) {
            if (!SMSIntentService.contactExists(this, textMessage.from, SMSIntentService())
                && textMessage.from.length >= 10
                && !message.isEmpty()
                && autoResponderOn
                && !SMSIntentService.isNumberBlocked(textMessage.from,application,SMSIntentService())
                && !SMSIntentService.isNumberTextedInLast5Minutes(textMessage.from, SMSIntentService())) {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(textMessage.from, null, message, null, null)
                //smsManager.sendTextMessage(SMSIntentService.SPAM_MESSAGE_PHONE_NUMBER,null,textMessage.body,null,null);
                SMSIntentService.handleText(textMessage.from,application, SMSIntentService())
            }
        }
        return null

    }
}