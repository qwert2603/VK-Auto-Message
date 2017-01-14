package com.qwert2603.vkautomessage.record_list;

import android.widget.ImageView;

import com.qwert2603.vkautomessage.base.list.ListView;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.model.User;

public interface RecordListView extends ListView<Record> {

    /**
     * For adapter items.
     * @param user user-receiver of records in record listFromModel.
     */
    void setUser(User user);

    void showUserName(String name);
    ImageView getUserPhotoImageView();
    void showRecordsCount(int recordsCount, int enabledRecordsCount);
    void showDontWriteToDeveloper();
}
