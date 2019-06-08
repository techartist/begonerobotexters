package com.webnation.begonerobotexters.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.telephony.TelephonyManager
import com.webnation.begonerobotexters.datastructures.MMSMessage
import timber.log.Timber

import java.io.*
import java.util.ArrayList

/**
 * Created by kristywelsh on 12/16/16.
 */

class MMSUtils {


    /**
     * http://stackoverflow.com/questions/3012287/how-to-read-mms-data-in-android
     * @param mmsId
     * @param context
     * @return
     */
    private fun getTextContent(mmsId: Int, context: Context): String {
        val selectionPart = "mid=$mmsId"
        val uri = Uri.parse("content://mms/part")
        var body = ""
        val cursor = context.contentResolver.query(
            uri, null,
            selectionPart, null, null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val partId = cursor.getString(cursor.getColumnIndex(_ID))
                    val type = cursor.getString(cursor.getColumnIndex(CONTENT_TYPE))
                    if ("text/plain" == type) {
                        val data = cursor.getString(cursor.getColumnIndex(_DATA))

                        if (data != null) {
                            // implementation of this method below
                            body = getMmsText(partId, context)
                        } else {
                            body = cursor.getString(cursor.getColumnIndex(TEXT))
                        }
                    }
                } while (cursor.moveToNext())
            }
        }

        return body

    }

    companion object {
        val _ID = "_id"
        val CONTENT_TYPE = "ct"
        val _DATA = "_data"
        val TEXT = "text"
        val TAG = "MMSUtils"
        val CONTENT_URI = Uri.parse("content://mms/")
        val THREAD_ID = "thread_id"
        val CONTENT_LOCATION = "cl"
        val MESSAGE_TYPE = "m_type"
        val MESSAGE_ID = "m_id"
        /**
         * gets the phone number or email address of the sender of the MMS.
         * @param context
         * @param id
         * @return
         */
        private fun getMmsAddr(context: Context, id: Int): String {
            var address = ""
            var `val`: String?

            val cur = context.contentResolver.query(
                Uri.parse("content://mms/$id/addr"),
                arrayOf("address"),
                "type=137 AND msg_id=$id",
                null,
                null
            ) ?: return address

            try {
                if (cur.moveToFirst()) {
                    do {
                        `val` = cur.getString(cur.getColumnIndex("address"))
                        if (`val` != null) {
                            address = `val`
                            break
                        }
                    } while (cur.moveToNext())
                }
            } finally {
                cur.close()
            }

            // return address.replaceAll("[^0-9]", "");
            return address
        }


        fun getMmsText(context: Context, id: String): String {
            var `is`: InputStream? = null
            val sb = StringBuilder()

            try {
                `is` = context.contentResolver.openInputStream(Uri.parse("content://mms/part/$id"))
                if (`is` == null) {
                    return sb.toString()
                }

                val isr = InputStreamReader(`is`, "UTF-8")
                val reader = BufferedReader(isr)
                var temp: String? = reader.readLine()
                while (temp != null) {
                    sb.append(temp)
                    temp = reader.readLine()
                }
            } catch (e: IOException) {
            } finally {
                if (`is` != null) {
                    try {
                        `is`.close()
                    } catch (e: IOException) {
                    }

                }
            }

            return sb.toString()
        }

        /**
         * http://stackoverflow.com/questions/3012287/how-to-read-mms-data-in-android
         * @param id
         * @param context
         * @return
         */
        fun getMmsText(id: String, context: Context): String {
            val partURI = Uri.parse("content://mms/part/$id")
            var `is`: InputStream? = null
            val sb = StringBuilder()
            try {
                `is` = context.contentResolver.openInputStream(partURI)
                if (`is` != null) {
                    val isr = InputStreamReader(`is`, "UTF-8")
                    val reader = BufferedReader(isr)
                    var temp: String? = reader.readLine()
                    while (temp != null) {
                        sb.append(temp)
                        temp = reader.readLine()
                    }
                }
            } catch (e: IOException) {
            } finally {
                if (`is` != null) {
                    try {
                        `is`.close()
                    } catch (e: IOException) {
                        Timber.e(e)
                    }

                }
            }
            return sb.toString()
        }

        /**
         * http://stackoverflow.com/questions/3012287/how-to-read-mms-data-in-android
         * @param _id
         * @param context
         * @return
         */
        fun getMmsImage(_id: String, context: Context): Bitmap? {
            val partURI = Uri.parse("content://mms/part/$_id")
            var `is`: InputStream? = null
            var bitmap: Bitmap? = null
            try {
                `is` = context.contentResolver.openInputStream(partURI)
                bitmap = BitmapFactory.decodeStream(`is`)
            } catch (e: IOException) {
            } finally {
                if (`is` != null) {
                    try {
                        `is`.close()
                    } catch (e: IOException) {
                    }

                }
            }
            return bitmap
        }


        //https://github.com/RoliSoft/RS-Text-Forwarder-Android-Client/
        fun getMessagesFrom(context: Context, intent: Intent?): List<MMSMessage> {
            val bundle = intent?.extras
            val messages = ArrayList<MMSMessage>()

            if (bundle == null || !bundle.containsKey("data")) {
                return messages
            }

            val data = bundle.getByteArray("data")

            if (data == null || data.size == 0) {
                return messages
            }

            try {

                try {
                    Thread.sleep(10000)
                } catch (ex: Exception) {
                    Timber.e(ex, "Interrupted exception")
                }

                val projection = arrayOf("_id", "sub", "m_id", "date")

                //http://susemi99.kr/664
                val cur = context.contentResolver.query(
                    Uri.parse("content://mms/inbox"),
                    projection,
                    "m_type in (132,128)",
                    null,
                    "_id DESC Limit 1"
                )
                if (cur != null) {


                    if (cur.moveToFirst()) {


                        val bitmaps = ArrayList<Bitmap>()
                        var bitmap: Bitmap? = null
                        val id = cur.getInt(cur.getColumnIndex("_id"))


                        val subj = cur.getString(cur.getColumnIndex("sub"))
                        var body = ""
                        var from = getMmsAddr(context, id)

                        val tm =
                            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                        val countryCode = tm.networkCountryIso.toUpperCase()
                        try {
                            val prefix = CountryToPhonePrefix.prefixFor(countryCode).replace("+", "")
                            var indexOfBaseNumber = from.indexOf(prefix)
                            if (indexOfBaseNumber > -1) {

                                if (indexOfBaseNumber == 0) {
                                    val lengthPrefix = prefix.length
                                    indexOfBaseNumber = indexOfBaseNumber + lengthPrefix
                                }
                                from = from.substring(indexOfBaseNumber)
                            }

                        } catch (ie: IllegalArgumentException) {
                            Timber.e(ie.toString())
                            ie.printStackTrace()
                        } catch (ie: IndexOutOfBoundsException) {
                            Timber.e(ie.toString())
                            ie.printStackTrace()
                        }


                        val date = java.lang.Long.parseLong(cur.getString(cur.getColumnIndex("date"))) * 1000

                        val cursor = context.contentResolver.query(
                            Uri.parse("content://mms/part"),
                            null,
                            "mid = $id",
                            null,
                            null
                        )
                        if (cursor != null) {
                            try {
                                if (cursor.moveToFirst()) {
                                    do {
                                        val pid = cursor.getString(cursor.getColumnIndex("_id"))
                                        val type = cursor.getString(cursor.getColumnIndex("ct"))
                                        if ("text/plain" == type) {
                                            val dat = cursor.getString(cursor.getColumnIndex("_data"))
                                            if (dat != null) {
                                                body += getMmsText(context, pid)
                                            } else {
                                                body += cursor.getString(cursor.getColumnIndex("text"))
                                            }
                                        } else if ("image/jpeg" == type || "image/bmp" == type || "image/gif" == type || "image/jpg" == type || "image/png" == type) {

                                            bitmap = getMmsImage(pid, context)
                                            if (bitmap != null) {
                                                bitmaps.add(bitmap)
                                            }

                                        }
                                    } while (cursor.moveToNext())
                                }
                            } finally {
                                cursor.close()
                            }
                        }

                        var messageBody : String
                        if (subj == null) {
                            messageBody = body
                        } else {
                            messageBody = subj + if (body.length != 0) "\n" + body else ""
                        }
                        if (bitmap == null) {
                            messages.add(MMSMessage(from, date, messageBody))
                        } else {
                            messages.add(MMSMessage(from, date, messageBody, bitmap))
                        }
                        return messages
                    }
                    cur.close()
                }



            } catch (ex: Exception) {
                Timber.d(ex.toString())
                ex.printStackTrace()
                return messages
            }

            return messages
        }
    }


}
