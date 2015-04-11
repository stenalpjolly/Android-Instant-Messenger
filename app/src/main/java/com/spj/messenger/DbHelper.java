package com.spj.messenger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by Stenal P Jolly on 09-Mar-15.
 */
public class DbHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "messenger.sqlite";
    private static final int DATABASE_VERSION = 1;
    private final static String TABLE_CONTACT_LIST = "contact_list";
    private final static String TABLE_CONVERSATION = "conversation";
    private final static String TABLE_MESSAGES = "messages";
    private final static String TABLE_RECENT_CONVERSATION_LIST = "recent_conversation_list";
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_NAME = "name";
    private final static String COLUMN_PHONE = "phone";
    private final static String COLUMN_EMAIL = "email";
    private final static String COLUMN_REGID = "regId";
    private final static String COLUMN_RECEP_LIST = "recep_list";
    private final static String COLUMN_PHOTO_URL = "photo_url";
    private final static String COLUMN_SINGLE = "single";
    private final static String COLUMN_TIME = "time";
    private final static String COLUMN_CONV_ID = "conv_id";
    private final static String COLUMN_MSG_ID = "msg_id";
    private final static String COLUMN_MSG_TYPE = "msg_type";
    private final static String COLUMN_MSG = "msg";
    private final static String COLUMN_FROM = "msg_from";
    private final static String COLUMN_TO = "msg_to";
    private final static String COLUMN_SEND_RECEIVE = "send_receive";
    private final static String COLUMN_DATE = "date";
    private final Context mContext;
    private SQLiteDatabase db;

    //    private static final String TABLE_
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static String getStringFromSpanned(Spanned source) {
        String msg = Html.toHtml(source);
        msg = msg.trim().replaceAll(" ", "%20");
        msg = msg.replaceAll("<", "%3C");
        msg = msg.replaceAll(">", "%3E");
        msg = msg.replaceAll("\"", "%22");
        return msg;
    }

    public void addNewContacts(ArrayList<UserDetails> userList) {
        db = getWritableDatabase();
        for (int i = 0; i < userList.size(); i++) {
            UserDetails userDetails;
            userDetails = userList.get(i);
            ContentValues data = new ContentValues();
            data.put(COLUMN_NAME, userDetails.getvUsername());
            data.put(COLUMN_PHONE, userDetails.getvPhone());
            data.put(COLUMN_EMAIL, userDetails.getvEmailId());
            data.put(COLUMN_REGID, userDetails.getvRegID());
            Cursor cursor = db.rawQuery("select * from " + TABLE_CONTACT_LIST + " where phone='" + userDetails.getvPhone() + "'", null);
            if (cursor.getCount() == 0) {
                db.insert(TABLE_CONTACT_LIST, null, data);
            }
//            Toast.makeText(mContext,"Data Saved to Database",Toast.LENGTH_SHORT).show();
            cursor.close();
        }
        db.close();
    }

    public int getContactCount() {
        return getCount(TABLE_CONTACT_LIST);
    }

    public int getRecentConvListCount() {
        return getCount(TABLE_RECENT_CONVERSATION_LIST);
    }

    public int getConversationCount() {
        return getCount(TABLE_CONVERSATION);
    }

    private int getCount(String table) {
        int count = 0;
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + table, null);
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public Boolean insertIntoMessage(Message message) {

        ContentValues contentValues = new ContentValues();
//        contentValues.put(COLUMN_CONV_ID, message.getvConvId());
        contentValues.put(COLUMN_CONV_ID, -1);
        if (message.getvMsgId() != null) {
            Log.v("MSG_ID", message.getvMsgId());
            contentValues.put(COLUMN_MSG_ID, message.getvMsgId());
        }else{
            final String time = Calendar.getInstance().getTime().toString();
            contentValues.put(COLUMN_MSG_ID, time);
        }
        contentValues.put(COLUMN_FROM,message.getvFrom());
        contentValues.put(COLUMN_TO,message.getvTo());
//        contentValues.put(COLUMN_MSG_TYPE, message.getvMsgType());
        contentValues.put(COLUMN_MSG, Html.toHtml(message.getvMsg()));
//        contentValues.put(COLUMN_DATE, message.getvDate());
//        contentValues.put(COLUMN_TIME, message.getvTime());
        if (message.getIsLeft())
            contentValues.put(COLUMN_SEND_RECEIVE, "R");
        else
            contentValues.put(COLUMN_SEND_RECEIVE, "S");

        db = getWritableDatabase();
        long flag = db.insert(TABLE_MESSAGES, null, contentValues);
        db.close();
        Log.v("INSERT VALUE :", String.valueOf(flag));
        return (flag != -1) ? true : false;
    }

    public ArrayList<Message> getAllMessage(final Bitmap[] emoticons,String from) {
        ArrayList<Message> messageArrayList = new ArrayList<>();
        db = getReadableDatabase();
        String sql_query = "select * from " + TABLE_MESSAGES + " where " + COLUMN_FROM +
                "= '" + from + "' or "+COLUMN_TO+" = '"+from+"'";
        Log.v("MSG_HISTORY", sql_query);
        Cursor cursor = db.rawQuery(sql_query, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        for (int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()) {
            int msgColumnIndex = cursor.getColumnIndex(COLUMN_MSG);
            int msgIdCoulmnIndex = cursor.getColumnIndex(COLUMN_MSG_ID);
            int send_receiveColumnIndex = cursor.getColumnIndex(COLUMN_SEND_RECEIVE);
//            int msgColumnIndex = cursor.getColumnIndex(COLUMN_MSG);
            String msg = cursor.getString(msgColumnIndex);
            String msgId = cursor.getString(msgIdCoulmnIndex);
            String send_receive = cursor.getString(send_receiveColumnIndex);

            Html.ImageGetter imageGetter = new Html.ImageGetter() {
                public Drawable getDrawable(String source) {
                    StringTokenizer st = new StringTokenizer(source, ".");
                    Drawable d = new BitmapDrawable(mContext.getResources(), emoticons[Integer.parseInt(st.nextToken()) - 1]);
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    return d;
                }
            };
            Spanned spannedMsg = Html.fromHtml(msg, imageGetter, null);
            Message message = new Message(mContext);
            message.setvMsg(spannedMsg);
            if (send_receive.equals("R")) {
                message.setIsLeft(true);
            } else {
                message.setIsLeft(false);
            }
            message.setvMsgId(msgId);
            messageArrayList.add(message);
        }
        db.close();
        return messageArrayList;
    }

    public ArrayList<UserDetails> getContactList() {
        ArrayList<UserDetails> contactList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CONTACT_LIST, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        try {
            for (int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()) {
                String username = cursor.getString(1);
                String phone = cursor.getString(2);
                String email = cursor.getString(3);
                String regID = cursor.getString(4);
                UserDetails userDetails = new UserDetails(username, email, phone, regID);
                contactList.add(userDetails);
            }
        } catch (Exception e) {
            Log.v("ContactNameList", e.toString());
        } finally {
            cursor.close();
            db.close();
        }
        return contactList;
    }

}
