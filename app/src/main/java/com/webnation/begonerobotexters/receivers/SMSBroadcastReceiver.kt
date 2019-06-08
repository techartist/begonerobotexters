package com.webnation.begonerobotexters.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.webnation.begonerobotexters.services.SMSIntentService

import timber.log.Timber

class SMSBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Timber.i("Intent received: " + intent.action!!)

        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {

            val bundle = intent.extras
            val intentServiceIntent = Intent(context, SMSIntentService::class.java)
            intentServiceIntent.putExtras(bundle!!)
            context.startService(intentServiceIntent)

        }
    }

}