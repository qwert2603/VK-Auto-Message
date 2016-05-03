package com.qwert2603.vkautomessage.record_details;

import android.support.annotation.NonNull;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qwert2603.vkautomessage.Const;
import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.model.RecordWithUser;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.qwert2603.vkautomessage.util.StringUtils;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

import static com.qwert2603.vkautomessage.util.StringUtils.getUserName;

public class RecordPresenter extends BasePresenter<RecordWithUser, RecordView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();

    @Inject
    DataManager mDataManager;

    public RecordPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(RecordPresenter.this);
    }

    public void setRecordId(int recordId) {
        setModel(null);
        mSubscription.unsubscribe();
        mSubscription = mDataManager
                .getRecordById(recordId)
                .subscribe(
                        record -> RecordPresenter.this.setModel(record),
                        throwable -> {
                            mSubscription.unsubscribe();
                            LogUtils.e(throwable);
                        }
                );
    }

    public void setRecord(Record record) {
        setModel(new RecordWithUser(record, null));
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    @Override
    public void onViewNotReady() {
        RecordView view = getView();
        if (view != null) {
            ImageLoader.getInstance().cancelDisplayTask(view.getPhotoImageView());
        }
        super.onViewNotReady();
    }

    @Override
    protected void onUpdateView(@NonNull RecordView view) {
        RecordWithUser recordWithUser = getModel();
        if (recordWithUser == null) {
            view.showLoading();
            return;
        }
        User user = recordWithUser.mUser;
        Record record = recordWithUser.mRecord;
        if (user != null) {
            if (view.getPhotoImageView() != null) {
                ImageLoader.getInstance().displayImage(user.getPhoto(), view.getPhotoImageView());
            }
            view.showUserName(getUserName(user));
        }
        if (record != null) {
            view.showMessage(record.getMessage());
            view.showEnabled(record.isEnabled());
            view.showTime(getTimeString());
        }
    }

    public void onUserChosen(int userId) {
        if (getModel().mRecord.getUserId() != userId) {
            getModel().mRecord.setUserId(userId);
            updateView();
            mDataManager.onRecordUpdated(getModel().mRecord);
        }
    }

    public void onTimeEdited(int minuteAtDay) {
        Record record = getModel().mRecord;
        int newHour = minuteAtDay / Const.MINUTES_PER_HOUR;
        int newMinute = minuteAtDay % Const.MINUTES_PER_HOUR;
        if (newHour != record.getHour() || newMinute != record.getMinute()) {
            record.setHour(newHour);
            record.setMinute(newMinute);
            getView().showTime(getTimeString());
            mDataManager.onRecordUpdated(getModel().mRecord);
        }
    }

    public void onEnableClicked(boolean enable) {
        if (getModel().mRecord.isEnabled() != enable) {
            getModel().mRecord.setEnabled(enable);
            mDataManager.onRecordUpdated(getModel().mRecord);
        }
    }

    public void onMessageEdited(String message) {
        if (message.isEmpty()) {
            getView().showToast(R.string.empty_message_toast);
            return;
        }
        if (!getModel().mRecord.getMessage().equals(message)) {
            getModel().mRecord.setMessage(message);
            getView().showMessage(message);
            mDataManager.onRecordUpdated(getModel().mRecord);
        }
    }

    public void onEditMessageClicked() {
        getView().showEditMessage(getModel().mRecord.getMessage());
    }

    public void onChooseTimeClicked() {
        Record record = getModel().mRecord;
        getView().showEditTime(record.getHour() * Const.MINUTES_PER_HOUR + record.getMinute());
    }

    public void onChooseDayClocked() {
        // TODO: 03.05.2016
    }

    private String getTimeString() {
        Record record = getModel().mRecord;
        return StringUtils.getRecordTime(record);
    }
}
