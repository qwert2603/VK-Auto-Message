package com.qwert2603.vkautomessage.record_details;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.qwert2603.vkautomessage.avatar_view.AvatarView;
import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseActivity;
import com.qwert2603.vkautomessage.base.navigation.NavigationFragment;
import com.qwert2603.vkautomessage.model.Record;
import com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_day_in_year.EditDayInYearDialog;
import com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_days_in_week.EditDaysInWeekDialog;
import com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_message.EditMessageDialog;
import com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_period.EditPeriodDialog;
import com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_repeat_type.EditRepeatTypeDialog;
import com.qwert2603.vkautomessage.record_details.edit_dialogs.edit_time.EditTimeDialog;
import com.qwert2603.vkautomessage.util.AndroidUtils;
import com.qwert2603.vkautomessage.util.LogUtils;
import com.qwert2603.vkautomessage.avatar_view.RoundedTransformation;
import com.qwert2603.vkautomessage.util.TransitionUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordFragment extends NavigationFragment<RecordPresenter> implements RecordView {

    private static final String recordIdKey = "recordId";

    private static final int REQUEST_EDIT_MESSAGE = 1;
    private static final int REQUEST_EDIT_TIME = 2;
    private static final int REQUEST_EDIT_REPEAT_TYPE = 3;
    private static final int REQUEST_EDIT_PERIOD = 4;
    private static final int REQUEST_EDIT_DAYS_IN_WEEK = 5;
    private static final int REQUEST_EDIT_DAY_IN_YEAR = 6;

    public static RecordFragment newInstance(int recordId) {
        RecordFragment recordFragment = new RecordFragment();
        Bundle args = new Bundle();
        args.putInt(recordIdKey, recordId);
        recordFragment.setArguments(args);
        return recordFragment;
    }

    @BindView(R.id.content_root_view)
    View mRootView;

    @BindView(R.id.toolbar_title_text_view)
    protected TextView mToolbarTitleTextView;

    @BindView(R.id.content_view)
    View mContentView;

    @BindView(R.id.avatar_view)
    AvatarView mAvatarView;

    @BindView(R.id.user_name_text_view)
    TextView mUsernameTextView;

    @BindView(R.id.enable_switch)
    SwitchCompat mEnableSwitch;

    @BindView(R.id.message_text_view)
    TextView mMessageTextView;

    @BindView(R.id.repeat_type_text_view)
    TextView mRepeatTypeTextView;

    @BindView(R.id.time_text_view)
    TextView mTimeTextView;

    @BindView(R.id.repeat_info_text_view)
    TextView mRepeatInfoTextView;

    @BindView(R.id.user_card)
    CardView mUserCardView;

    @BindView(R.id.message_card)
    CardView mMessageCardView;

    @BindView(R.id.repeat_type_card)
    CardView mRepeatTypeCardView;

    @BindView(R.id.time_card)
    CardView mTimeCardView;

    @BindView(R.id.repeat_info_card)
    CardView mRepeatInfoCardView;

    @Inject
    RecordPresenter mRecordPresenter;

    private Target mPicassoTarget;

    @NonNull
    @Override
    protected RecordPresenter getPresenter() {
        return mRecordPresenter;
    }

    @Override
    protected boolean isNavigationButtonVisible() {
        return false;
    }

    @Override
    protected int getToolbarContentRes() {
        return R.layout.toolbar_title;
    }

    @Override
    protected int getScreenContentRes() {
        return R.layout.fragment_record_details;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        VkAutoMessageApplication.getAppComponent().inject(RecordFragment.this);
        LogUtils.d("getArguments().getInt(recordIdKey) == " + getArguments().getInt(recordIdKey));
        mRecordPresenter.setRecordId(getArguments().getInt(recordIdKey));
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ButterKnife.bind(RecordFragment.this, view);
        mPicassoTarget = new AvatarView.PicassoTarget(mAvatarView);

        mUserCardView.setOnClickListener(v -> mRecordPresenter.onUserClicked());
        mEnableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> mRecordPresenter.onEnableClicked(isChecked));
        mMessageCardView.setOnClickListener(v -> mRecordPresenter.onEditMessageClicked());
        mRepeatTypeCardView.setOnClickListener(v -> mRecordPresenter.onEditRepeatTypeClicked());
        mTimeCardView.setOnClickListener(v -> mRecordPresenter.onEditTimeClicked());
        mRepeatInfoCardView.setOnClickListener(v -> mRecordPresenter.onEditRepeatInfoClicked());

        // чтобы mAvatarView не моргал.
        mAppBarLayout.setAlpha(0);
        AndroidUtils.setActionOnPreDraw(mAppBarLayout, () -> AndroidUtils.runOnUI(() -> mAppBarLayout.setAlpha(1), 70));

        TransitionUtils.setSharedElementTransitions(getActivity(), R.transition.shared_element);

        Fade fade = new Fade();
        fade.addTarget(CardView.class);
        fade.addTarget(mEnableSwitch);

        Slide slideContent = new Slide(Gravity.END);
        slideContent.addTarget(CardView.class);
        slideContent.addTarget(mEnableSwitch);

        Slide slide = new Slide(Gravity.TOP);
        slide.addTarget(mToolbarTitleTextView);
        slide.addTarget(mToolbarIconImageView);

        int duration = getResources().getInteger(R.integer.transition_duration);
        TransitionSet transitionSet = new TransitionSet()
                .addTransition(fade)
                .addTransition(slide)
                .addTransition(slideContent)
                .setDuration(duration);

        getActivity().getWindow().setExitTransition(transitionSet);
        getActivity().getWindow().setReenterTransition(transitionSet);
        getActivity().getWindow().setEnterTransition(transitionSet);
        getActivity().getWindow().setReturnTransition(transitionSet);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Picasso.with(getActivity()).cancelRequest(mPicassoTarget);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_EDIT_MESSAGE:
                String message = data.getStringExtra(EditMessageDialog.EXTRA_MESSAGE);
                mRecordPresenter.onMessageEdited(message);
                break;
            case REQUEST_EDIT_TIME:
                int hour = data.getIntExtra(EditTimeDialog.EXTRA_HOUR, 19);
                int minute = data.getIntExtra(EditTimeDialog.EXTRA_MINUTE, 18);
                mRecordPresenter.onTimeEdited(hour, minute);
                break;
            case REQUEST_EDIT_REPEAT_TYPE:
                @Record.RepeatType int repeatType = data.getIntExtra(EditRepeatTypeDialog.EXTRA_REPEAT_TYPE, Record.REPEAT_TYPE_HOURS_IN_DAY);
                mRecordPresenter.onRepeatTypeEdited(repeatType);
                break;
            case REQUEST_EDIT_PERIOD:
                int period = data.getIntExtra(EditPeriodDialog.EXTRA_PERIOD, 0);
                mRecordPresenter.onPeriodEdited(period);
                break;
            case REQUEST_EDIT_DAYS_IN_WEEK:
                int daysInWeek = data.getIntExtra(EditDaysInWeekDialog.EXTRA_DAYS_IN_WEEK, 0);
                mRecordPresenter.onDaysInWeekEdited(daysInWeek);
                break;
            case REQUEST_EDIT_DAY_IN_YEAR:
                int month = data.getIntExtra(EditDayInYearDialog.EXTRA_MONTH, 0);
                int dayOfMonth = data.getIntExtra(EditDayInYearDialog.EXTRA_DAY_OF_MONTH, 0);
                mRecordPresenter.onDayInYearEdited(month, dayOfMonth);
                break;
        }
    }

    @Override
    public void showPhoto(String url, String initials) {
        mAvatarView.showInitials(initials);
        Picasso.with(getActivity())
                .load(url)
                .transform(new RoundedTransformation())
                .into(mPicassoTarget);
    }

    @Override
    public void showUserName(String userName) {
        mUsernameTextView.setText(userName);
    }

    @Override
    public void showMessage(String message) {
        mMessageTextView.setText(message);
    }

    @Override
    public void showEnabled(boolean enabled) {
        mEnableSwitch.setChecked(enabled);
        mEnableSwitch.jumpDrawablesToCurrentState();
    }

    @Override
    public void showTime(String time) {
        mTimeTextView.setText(time);
    }

    @Override
    public void showRepeatType(String repeatType) {
        mRepeatTypeTextView.setText(repeatType);
    }

    @Override
    public void showRepeatInfo(String repeatInfo) {
        mRepeatInfoTextView.setText(repeatInfo);
    }

    @Override
    public void showLoading() {
        LogUtils.d("RecordFragment showLoading");
        mAvatarView.showInitials("");
        mUsernameTextView.setText(R.string.loading);
        mMessageTextView.setText(R.string.loading);
        mTimeTextView.setText(R.string.loading);
        mRepeatInfoTextView.setText(R.string.loading);
    }

    @Override
    public void showEditMessage(String message) {
        EditMessageDialog editMessageDialog = EditMessageDialog.newInstance(message);
        editMessageDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_MESSAGE);
        editMessageDialog.show(getFragmentManager(), editMessageDialog.getClass().getName());
    }

    @Override
    public void showEditTime(int hour, int minute) {
        EditTimeDialog editTimeDialog = EditTimeDialog.newInstance(hour, minute);
        editTimeDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_TIME);
        editTimeDialog.show(getFragmentManager(), editTimeDialog.getClass().getName());
    }

    @Override
    public void showEditRepeatType(@Record.RepeatType int repeatType) {
        EditRepeatTypeDialog editRepeatTypeDialog = EditRepeatTypeDialog.newInstance(repeatType);
        editRepeatTypeDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_REPEAT_TYPE);
        editRepeatTypeDialog.show(getFragmentManager(), editRepeatTypeDialog.getClass().getName());
    }

    @Override
    public void showEditPeriod(int period) {
        EditPeriodDialog editPeriodDialog = EditPeriodDialog.newInstance(period);
        editPeriodDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_PERIOD);
        editPeriodDialog.show(getFragmentManager(), editPeriodDialog.getClass().getName());
    }

    @Override
    public void showEditDaysInWeek(int daysInWeek) {
        EditDaysInWeekDialog editDaysInWeekDialog = EditDaysInWeekDialog.newInstance(daysInWeek);
        editDaysInWeekDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_DAYS_IN_WEEK);
        editDaysInWeekDialog.show(getFragmentManager(), editDaysInWeekDialog.getClass().getName());
    }

    @Override
    public void showEditDayInYear(int month, int dayOfMonth) {
        EditDayInYearDialog editDayInYearDialog = EditDayInYearDialog.newInstance(month, dayOfMonth);
        editDayInYearDialog.setTargetFragment(RecordFragment.this, REQUEST_EDIT_DAY_IN_YEAR);
        editDayInYearDialog.show(getFragmentManager(), editDayInYearDialog.getClass().getName());
    }

    @Override
    public void showToast(int stringRes) {
        Toast.makeText(getActivity(), stringRes, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void performBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(BaseActivity.EXTRA_ITEM_ID, getArguments().getInt(recordIdKey));
        getActivity().setResult(Activity.RESULT_OK, intent);

        // чтобы mAvatarView не моргал.
        AndroidUtils.runOnUI(() -> mAppBarLayout.setAlpha(0), getResources().getInteger(R.integer.transition_duration));
        super.performBackPressed();
    }
}
