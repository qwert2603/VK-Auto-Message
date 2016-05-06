package com.qwert2603.vkautomessage.record_list;

import android.support.annotation.NonNull;

import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BasePresenter;
import com.qwert2603.vkautomessage.model.DataManager;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.model.RecordListWithUser;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.Subscriptions;

public class RecordListPresenter extends BasePresenter<RecordListWithUser, RecordListView> {

    private Subscription mSubscription = Subscriptions.unsubscribed();

    @Inject
    DataManager mDataManager;

    private int mUserId;

    public RecordListPresenter() {
        VkAutoMessageApplication.getAppComponent().inject(RecordListPresenter.this);
    }

    public void setUserId(int userId) {
        setModel(null);
        mSubscription.unsubscribe();
        mUserId = userId;
        loadRecordList();
    }

    @Override
    public void unbindView() {
        mSubscription.unsubscribe();
        super.unbindView();
    }

    @Override
    protected void onUpdateView(@NonNull RecordListView view) {
        RecordListWithUser recordListWithUser = getModel();
        if (recordListWithUser == null) {
            if (mSubscription.isUnsubscribed()) {
                view.showError();
            } else {
                view.showLoading();
            }
        } else {
            List<Record> recordList = recordListWithUser.mRecordList;
            if (recordList.isEmpty()) {
                view.showEmpty();
            } else {
                view.showList(recordList);
            }
        }
    }

    public void onReload() {
        loadRecordList();
        updateView();
    }

    public void onResume() {
        loadRecordList();
    }

    public void onNewRecordClicked() {
        Record record = new Record(mUserId);
        mDataManager.addRecord(record)
                .subscribe(aVoid -> {
                    RecordListWithUser model = getModel();
                    if (model == null || model.mRecordList == null) {
                        return;
                    }
                    model.mRecordList.add(record);
                    // TODO: 04.05.2016 use notifyItemInserted
                    updateView();
                    RecordListView view = getView();
                    if (view != null) {
                        view.moveToRecordDetails(record.getId());
                    }
                }, LogUtils::e);
    }

    public void onRecordAtPositionClicked(int position) {
        getView().moveToRecordDetails(getModel().mRecordList.get(position).getId());
    }

    public void onRecordAtPositionLongClicked(int position) {
        getView().showDeleteRecord(getModel().mRecordList.get(position).getId());
    }

    public void onRecordDeleteClicked(int recordId) {
        int position = getRecordPosition(recordId);
        mDataManager.removeRecord(recordId)
                .subscribe(aLong -> {
                    RecordListWithUser model = getModel();
                    if (model == null || model.mRecordList == null) {
                        return;
                    }
                    model.mRecordList.remove(position);
                    if (model.mRecordList.size() > 1) {
                        RecordListView view = getView();
                        if (view != null) {
                            view.notifyItemRemoved(position);
                        }
                    } else {
                        updateView();
                    }
                }, LogUtils::e);
    }

    private void loadRecordList() {
        mSubscription.unsubscribe();
        mSubscription = mDataManager.getRecordsForUser(mUserId)
                .subscribe(
                        recordListWithUser -> RecordListPresenter.this.setModel(recordListWithUser),
                        throwable -> {
                            mSubscription.unsubscribe();
                            updateView();
                            LogUtils.e(throwable);
                        }
                );
    }

    private int getRecordPosition(int recordId) {
        List<Record> recordList = getModel().mRecordList;
        for (int i = 0, size = recordList.size(); i < size; ++i) {
            if (recordList.get(i).getId() == recordId) {
                return i;
            }
        }
        return -1;
    }

}
