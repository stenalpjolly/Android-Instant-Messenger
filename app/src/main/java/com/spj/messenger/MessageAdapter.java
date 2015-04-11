package com.spj.messenger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Stenal P Jolly on 24-Mar-15.
 */
public class MessageAdapter extends ArrayAdapter<Message> {

    private ArrayList<Message> messageArrayList;

    public MessageAdapter(Context context, ArrayList<Message> messageArrayList) {
        super(context, 0);
        this.messageArrayList = messageArrayList;
    }

    @Override
    public int getCount() {
        return messageArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_chat_singlemessage, parent, false);
        }
        LinearLayout singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        RelativeLayout singleMessageLayout = (RelativeLayout) row.findViewById(R.id.singleMessageLayout);
        Message chatMessageObj = messageArrayList.get(position);
        TextView chatText = (TextView) row.findViewById(R.id.singleMessage);
        TextView chatTime = (TextView) row.findViewById(R.id.txtMsgTime);
        chatText.setTextColor(chatMessageObj.getIsLeft() ? Color.BLACK : Color.WHITE);
        chatTime.setText(chatMessageObj.getvTime());
        chatText.setText(trimTrailingWhitespace(chatMessageObj.getvMsg()));
        singleMessageLayout.setBackgroundResource(chatMessageObj.getIsLeft() ? R.drawable.receive_bubble : chatMessageObj.getvMsgState().equals("S") ? R.drawable.from_bubble : R.drawable.notsent_bubble);
        singleMessageContainer.setGravity(chatMessageObj.getIsLeft() ? Gravity.LEFT : Gravity.RIGHT);
        return row;
    }

    public static CharSequence trimTrailingWhitespace(CharSequence source) {

        if(source == null)
            return "";

        int i = source.length();

        // loop back to the first non-whitespace character
        while(--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }

        return source.subSequence(0, i+1);
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
