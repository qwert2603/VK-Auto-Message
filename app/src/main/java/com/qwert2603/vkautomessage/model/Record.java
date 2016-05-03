package com.qwert2603.vkautomessage.model;

import java.util.Calendar;

public class Record {

    /**
     * Повтор каждый день через каждые {@link #mRepeatInfo} часов.
     */
    public static final int REPEAT_TYPE_HOURS_IN_DAY = 1;

    /**
     * Повтор в те дни недели, для которых установлен в 1 соответствующий бит {@link #mRepeatInfo}.
     */
    public static final int REPEAT_TYPE_DAYS_IN_WEEK = 2;

    /**
     * Повтор в каждый год в день номер {@link #mRepeatInfo}.
     */
    public static final int REPEAT_TYPE_DAY_IN_YEAR = 3;

    private int mId;
    private int mUserId;
    private String mMessage;
    private boolean mEnabled;

    private int mRepeatType;
    private int mRepeatInfo;
    private int mHour;
    private int mMinute;

    public Record(int userId) {
        mUserId = userId;
        mMessage = "Vk Auto Message";
        mRepeatType = REPEAT_TYPE_HOURS_IN_DAY;
        mRepeatInfo = 24;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
    }

    public Record(int id, int userId, String message, boolean enabled, int repeatType, int repeatInfo, int hour, int minute) {
        mId = id;
        mUserId = userId;
        mMessage = message;
        mEnabled = enabled;
        mRepeatType = repeatType;
        mRepeatInfo = repeatInfo;
        mHour = hour;
        mMinute = minute;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public int getRepeatType() {
        return mRepeatType;
    }

    public void setRepeatType(int repeatType) {
        mRepeatType = repeatType;
    }

    public int getRepeatInfo() {
        return mRepeatInfo;
    }

    public void setRepeatInfo(int repeatInfo) {
        mRepeatInfo = repeatInfo;
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }

}
