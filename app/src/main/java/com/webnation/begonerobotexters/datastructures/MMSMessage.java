package com.webnation.begonerobotexters.datastructures;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;

/**
 * Created by kristywelsh on 11/27/16.
 */

public class MMSMessage extends Message implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    private Bitmap bitmap = null;


    public MMSMessage(String id)
    {
        super(id);

    }

    public MMSMessage(String from, long date, String body)
    {
        super(from, date, body);

    }

    public MMSMessage(String from, long date, String body, Bitmap bitmap)
    {
        super(from, date, body);
        this.bitmap = bitmap;

    }

    public MMSMessage(String id, String t_id, long date, String addr, String body) {
        super(id,t_id,date,addr,body);
    }

    public void setImg(Bitmap bm) {
        this.bitmap = bm;
        if (bm != null)
            bData = true;
        else
            bData = false;
    }

    public void append(String body)
    {
        this.body += body;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public long getDate() {
        return date;
    }

    public String getFrom() {
        return addr;
    }

    public String getContact() {
        return super.getContact();
    }

    public String getBody() {
        return body;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static final Creator<MMSMessage> CREATOR =
            new Creator<MMSMessage>() {

                @Override
                public MMSMessage createFromParcel(Parcel source) {
                    return new MMSMessage(source);
                }

                @Override
                public MMSMessage[] newArray(int size) {
                    return new MMSMessage[size];
                }
            };

    public MMSMessage(Parcel in) {
        int count = 0;

        this.id = in.readString();
        this.t_id = in.readString();
        this.date = in.readLong();
        this.addr = in.readString().trim();
        this.body = in.readString();
        this.contact = in.readString();
        count = in.readInt();
        if (count > 0) {
            byte[] _byte = new byte[in.readInt()];
            this.bitmap = BitmapFactory.decodeByteArray(_byte, 0, _byte.length);
        }

    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(t_id);
        dest.writeLong(date);
        dest.writeString(addr);
        dest.writeString(body);
        dest.writeString(contact);
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            int count = byteArray.length;
            dest.writeInt(count);
            dest.writeByteArray(byteArray);
        } else {
            dest.writeInt(0);
        }


    }
}