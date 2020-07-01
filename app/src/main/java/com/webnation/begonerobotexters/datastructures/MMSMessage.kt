package com.webnation.begonerobotexters.datastructures

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import java.io.ByteArrayOutputStream

class MMSMessage : Message, Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    var bitmap: Bitmap? = null

    constructor(
        from: String?,
        date: Long,
        body: String?,
        bitmap: Bitmap?
    ) : super(from, date, body) {
        this.bitmap = bitmap
    }

    fun setImg(bm: Bitmap?) {
        bitmap = bm
        bData = if (bm != null) true else false
    }

    fun append(body: String?) {
        this.body += body
    }

    override fun getDate(): Long {
        return date
    }

    val from: String
        get() = addr

    public override fun getContact(): String {
        return super.getContact()
    }

    override fun getBody(): String {
        return body
    }

    constructor(`in`: Parcel) {
        var count = 0
        id = `in`.readString()
        t_id = `in`.readString()
        date = `in`.readLong()
        addr = `in`.readString().trim { it <= ' ' }
        body = `in`.readString()
        contact = `in`.readString()
        count = `in`.readInt()
        if (count > 0) {
            val _byte = ByteArray(`in`.readInt())
            bitmap = BitmapFactory.decodeByteArray(_byte, 0, _byte.size)
        }
    }

    override fun writeToParcel(dest: Parcel, i: Int) {
        dest.writeString(id)
        dest.writeString(t_id)
        dest.writeLong(date)
        dest.writeString(addr)
        dest.writeString(body)
        dest.writeString(contact)
        if (bitmap != null) {
            val stream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            val count = byteArray.size
            dest.writeInt(count)
            dest.writeByteArray(byteArray)
        } else {
            dest.writeInt(0)
        }
    }

    companion object {
        val CREATOR: Parcelable.Creator<MMSMessage?> = object : Parcelable.Creator<MMSMessage?> {
            override fun createFromParcel(source: Parcel): MMSMessage? {
                return MMSMessage(source)
            }

            override fun newArray(size: Int): Array<MMSMessage?> {
                return arrayOfNulls(size)
            }
        }
    }
}
