package com.webnation.begonerobotexters.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import com.webnation.begonerobotexters.services.MMSIntentService;


/**
 * Created by kristywelsh on 11/16/16.
 * https://maximbogatov.wordpress.com/2011/08/13/mms-in-android/
 */

public class MMSBroadcastReceiver extends BroadcastReceiver {
    private final String DEBUG_TAG = getClass().getSimpleName().toString();
    private static final String ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";
    private static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";
    public static final Uri CONTENT_URI =
            Uri.parse("content://mms/");
    public static final String CONTENT_LOCATION = "cl";
    public static final String MESSAGE_TYPE = "m_type";
    public static final String MESSAGE_ID = "m_id";
    public static final String _ID = "_id";
    public static final String CONTENT_TYPE = "ct";
    public static final String TEXT = "text";
    public static Context context = null;

    // Retrieve MMS
    //http://stackoverflow.com/questions/14452808/sending-and-receiving-sms-and-mms-in-android-pre-kit-kat-android-4-4
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        String action = intent.getAction();
        String type = intent.getType();
        Toast.makeText(context,"MMS received",Toast.LENGTH_LONG);

        if (action.equals(ACTION_MMS_RECEIVED) && type.equals(MMS_DATA_TYPE)) {

            Bundle bundle = intent.getExtras();
            Intent intentServiceIntent = new Intent(context, MMSIntentService.class);
            intentServiceIntent.putExtras(bundle);
            context.startService(intentServiceIntent);
        }

    }




}
