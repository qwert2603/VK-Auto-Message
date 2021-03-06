package com.qwert2603.vkautomessage.di;

import com.qwert2603.vkautomessage.choose_user.ChooseUserAdapter;
import com.qwert2603.vkautomessage.delete_record.DeleteRecordPresenter;
import com.qwert2603.vkautomessage.delete_user.DeleteUserPresenter;
import com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_day_in_year.EditDayInYearPresenter;
import com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_days_in_week.EditDaysInWeekPresenter;
import com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_message.EditMessagePresenter;
import com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_period.EditPeriodPresenter;
import com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_repeat_type.EditRepeatTypePresenter;
import com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_time.EditTimePresenter;
import com.qwert2603.vkautomessage.errors_show.ErrorsShowPresenter;
import com.qwert2603.vkautomessage.base.navigation.NavigationPresenter;
import com.qwert2603.vkautomessage.record_details.RecordPresenter;
import com.qwert2603.vkautomessage.record_list.RecordListAdapter;
import com.qwert2603.vkautomessage.record_list.RecordListPresenter;
import com.qwert2603.vkautomessage.choose_user.ChooseUserPresenter;
import com.qwert2603.vkautomessage.user_details.UserPresenter;
import com.qwert2603.vkautomessage.user_list.UserListAdapter;
import com.qwert2603.vkautomessage.user_list.UserListPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewModule {

    @Provides
    RecordListPresenter provideRecordListPresenter() {
        return new RecordListPresenter();
    }

    @Provides
    RecordPresenter provideRecordPresenter() {
        return new RecordPresenter();
    }

    @Provides
    ChooseUserPresenter provideChooseUserPresenter() {
        return new ChooseUserPresenter();
    }

    @Provides
    UserListPresenter provideUserListPresenter() {
        return new UserListPresenter();
    }

    @Provides
    UserPresenter provideUserPresenter() {
        return new UserPresenter();
    }

    @Provides
    NavigationPresenter provideNavigationPresenter() {
        return new NavigationPresenter();
    }

    @Provides
    DeleteRecordPresenter provideDeleteRecordPresenter() {
        return new DeleteRecordPresenter();
    }

    @Provides
    DeleteUserPresenter provideDeleteUserPresenter() {
        return new DeleteUserPresenter();
    }

    @Provides
    EditMessagePresenter provideEditMessagePresenter() {
        return new EditMessagePresenter();
    }

    @Provides
    EditTimePresenter provideEditTimePresenter() {
        return new EditTimePresenter();
    }

    @Provides
    EditPeriodPresenter provideEditPeriodPresenter() {
        return new EditPeriodPresenter();
    }

    @Provides
    EditRepeatTypePresenter provideEditRepeatTypePresenter() {
        return new EditRepeatTypePresenter();
    }

    @Provides
    EditDaysInWeekPresenter provideEditDaysInWeekPresenter() {
        return new EditDaysInWeekPresenter();
    }

    @Provides
    EditDayInYearPresenter provideEditDayInYearPresenter() {
        return new EditDayInYearPresenter();
    }

    @Provides
    UserListAdapter provideUserListAdapter() {
        return new UserListAdapter();
    }

    @Provides
    RecordListAdapter provideRecordListAdapter() {
        return new RecordListAdapter();
    }

    @Provides
    ChooseUserAdapter provideChooseUserAdapter() {
        return new ChooseUserAdapter();
    }

    @Provides
    ErrorsShowPresenter errorsShowPresenter() {
        return new ErrorsShowPresenter();
    }

}
