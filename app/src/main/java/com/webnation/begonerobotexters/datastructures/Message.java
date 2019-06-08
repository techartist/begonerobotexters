package com.webnation.begonerobotexters.datastructures;


import java.util.Comparator;


/**
 * Created by rbenedict on 3/16/2016.
 * http://stackoverflow.com/questions/36001339/find-and-interate-all-sms-mms-messages-in-android
 */


public class Message implements Comparable<Message> {
    protected String id;
    protected String t_id;
    protected long date;
    protected String dispDate;
    protected String addr;
    protected String contact;
    protected String direction;
    protected String body;
    protected boolean bData;
    //Date vdat;

    public Message() {

    }

    public Message(String ID) {
        id = ID;
        body = "";
    }

    public Message(String ID, String body) {
        id = ID;
        this.body = body;
    }

    public Message(String from, long date, String body)
    {
        this.addr = from;
        this.date = date;
        this.body = body;
    }

    public Message(String id, String t_id, long date, String addr, String body) {
        this.id = id;
        this.t_id = t_id;
        this.date = date;
        this.addr = addr;
        this.body = body;
    }



    public void setThread(String d) { t_id = d; }

    public void setAddr(String a) {
        addr = a;
    }
    public void setContact(String c) {
        if (c==null) {
            contact = "Unknown";
        } else {
            contact = c;
        }
    }
    public void setDirection(String d) {
        if ("1".equals(d))
            direction = "FROM: ";
        else
            direction = "TO: ";

    }
    public void setBody(String b) {
        body = b;
    }


    public long getDate() {
        return date;
    }

    public String getThread() { return t_id; }
    public String getID() { return id; }
    public String getBody() { return body; }

    public boolean hasData() { return bData; }

    public String toString() {

        String s = id + ". " + dispDate + " - " + direction + " " + contact + " " + addr + ": "  + body;
        return s;
    }


    @Override
    public int compareTo(Message another) {
        long compareQuantity = ((Message) another).getDate();

        //ascending order
         long value = this.date - compareQuantity;
         if (value == 0) {
             return 0;
         } else if (value < 0) {
             return -1;
         } else {
             return 1;
         }


    }

    protected String getContact() {
        return contact;
    }

    public static Comparator<Message> MessageComparator
            = new Comparator<Message>() {

        public int compare(Message message1, Message message2) {

            long date1 = message1.getDate();
            long date2 = message2.getDate();

            //ascending order
            return message1.compareTo(message2);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };
}
