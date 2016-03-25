package com.qwert2603.vkautomessage.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class PreferenceHelper {

    private static final String userNameKey = "userName";
    private static final String userPhotoKey = "userPhoto";
    private static final String sendMissedMessagesKey = "sendMissedMessages";
    private static final String lastNotificationIdKey = "lastNotificationId";

    private SharedPreferences mSharedPreferences;

    private String mUserName;
    private String mUserPhoto;

    /**
     * Отправять ли сообщения при появлении интернета,
     * если их не удалось отправить ранее из-за того, что интернета не было.
     */
    private Boolean mSendMissedMessages;

    private Integer mLastNotificationId;

    public PreferenceHelper(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getUserName() {
        if (mUserName == null) {
            mUserName = mSharedPreferences.getString(userNameKey, "");
        }
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
        mSharedPreferences.edit().putString(userNameKey, mUserName).apply();
    }

    public String getUserPhoto() {
        if (mUserPhoto == null) {
            mUserPhoto = mSharedPreferences.getString(userPhotoKey, "");
        }
        return mUserPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        mUserPhoto = userPhoto;
        mSharedPreferences.edit().putString(userPhotoKey, mUserPhoto).apply();
    }

    public boolean isSendMissedMessages() {
        if (mSendMissedMessages == null) {
            mSendMissedMessages = mSharedPreferences.getBoolean(sendMissedMessagesKey, true);
        }
        return mSendMissedMessages;
    }

    public void setSendMissedMessages(boolean sendMissedMessages) {
        mSendMissedMessages = sendMissedMessages;
        mSharedPreferences.edit().putBoolean(sendMissedMessagesKey, mSendMissedMessages).apply();
    }

    public int getLastNotificationId() {
        if (mLastNotificationId == null) {
            mLastNotificationId = mSharedPreferences.getInt(lastNotificationIdKey, 0);
        }
        return mLastNotificationId;
    }

    public void setLastNotificationId(int lastNotificationId) {
        mLastNotificationId = lastNotificationId;
        mSharedPreferences.edit().putInt(lastNotificationIdKey, lastNotificationId).apply();
    }

    public void clear() {
        mSharedPreferences.edit()
                .clear()
                .apply();
    }
}