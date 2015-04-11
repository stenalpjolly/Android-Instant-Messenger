package com.spj.messenger;

import android.content.Context;
import android.text.Spanned;

/**
 * Created by Stenal P Jolly on 24-Mar-15.
 */
public class Message {
    private String vFrom, vTo, vDate, vTime,  vMsgType, vMsgId, vMsgState, vConvId = "-1";
    private Spanned vMsg;
    private Boolean isLeft = false;
    private Context mContext;

    public Message(Context context) {
        vFrom = vTo = vDate = vTime = null;
        vMsg = null;
        vMsgType = "text/plain";
        vMsgState = "N";
        this.mContext = context;
    }

    public Message(Context mContext, Spanned msg, String msgType, String from, String to, String date, String time, Boolean isLeft) {
        this.mContext = mContext;
        this.vMsg = msg;
        this.vFrom = from;
        this.vTo = to;
        this.vDate = date;
        this.vTime = time;
        this.isLeft = isLeft;
        this.vMsgType = msgType;
    }

    public void setIsLeft(Boolean isLeft) {
        this.isLeft = isLeft;
    }

    public void setvConvId(String vConvId) {
        this.vConvId = vConvId;
    }

    public void setvMsgId(String vMsgId) {
        this.vMsgId = vMsgId;
    }

    public void setvMsgType(String vMsgType) {
        this.vMsgType = vMsgType;
    }

    public void setvDate(String vDate) {
        this.vDate = vDate;
    }

    public void setvFrom(String vFrom) {
        this.vFrom = vFrom;
    }

    public void setvMsg(Spanned vMsg) {
        this.vMsg = vMsg;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void setvTime(String vTime) {
        this.vTime = vTime;
    }

    public void setvTo(String vTo) {
        this.vTo = vTo;
    }

    public void setvMsgState(String vMsgState) {
        this.vMsgState = vMsgState;
    }

    public Boolean getIsLeft() {
        return isLeft;
    }

    public String getvConvId() {
        return vConvId;
    }

    public Context getmContext() {
        return mContext;
    }

    public String getvDate() {
        return vDate;
    }

    public String getvFrom() {
        return vFrom;
    }

    public String getvMsgId() {
        return vMsgId;
    }

    public String getvMsgType() {
        return vMsgType;
    }

    public Spanned getvMsg() {
        return vMsg;
    }

    public String getvTime() {
        return vTime;
    }

    public String getvTo() {
        return vTo;
    }

    public String getvMsgState() {
        return vMsgState;
    }
}
