package com.qwert2603.vkautomessage.user_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.VkAutoMessageApplication;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.integer_view.anim_integer_view.CounterIntegerView;
import com.qwert2603.vkautomessage.model.User;
import com.qwert2603.vkautomessage.user_details.UserPresenter;
import com.qwert2603.vkautomessage.user_details.UserView;
import com.qwert2603.vkautomessage.util.RoundedTransformation;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserListAdapter extends BaseRecyclerViewAdapter<User, UserListAdapter.UserViewHolder, UserPresenter> {

    public UserListAdapter() {
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
        return new UserViewHolder(view);
    }

    public class UserViewHolder
            extends BaseRecyclerViewAdapter<User, ?, UserPresenter>.RecyclerViewHolder
            implements UserView {

        @BindView(R.id.photo_image_view)
        ImageView mPhotoImageView;

        @BindView(R.id.user_name_text_view)
        TextView mUsernameTextView;

        @BindView(R.id.records_count_layout)
        LinearLayout mRecordsCountLinearLayout;

        @BindView(R.id.records_count_text_view)
        CounterIntegerView mRecordsCountTextView;

        @BindView(R.id.enabled_records_count_text_view)
        CounterIntegerView mEnabledRecordsCountTextView;

        @Inject
        UserPresenter mUserPresenter;

        public UserViewHolder(View itemView) {
            super(itemView);
            VkAutoMessageApplication.getAppComponent().inject(UserViewHolder.this);
            ButterKnife.bind(UserViewHolder.this, itemView);
        }

        @Override
        protected UserPresenter getPresenter() {
            return mUserPresenter;
        }

        @Override
        protected void setModel(User user) {
            mUserPresenter.setUser(user);
        }

        @Override
        public void bindPresenter() {
            super.bindPresenter();
            mRecordsCountEverShown = false;
        }

        @Override
        public void unbindPresenter() {
            super.unbindPresenter();
            Picasso.with(mPhotoImageView.getContext()).cancelRequest(mPhotoImageView);
        }

        @Override
        public void showName(String name) {
            mUsernameTextView.setText(name);
        }

        @Override
        public void showPhoto(String url) {
            Picasso.with(mPhotoImageView.getContext())
                    .load(url)
                    .transform(new RoundedTransformation())
                    .into(mPhotoImageView);
        }

        @Override
        public void hideRecordsCount() {
            mRecordsCountLinearLayout.setVisibility(View.GONE);
        }

        private boolean mRecordsCountEverShown = false;

        @Override
        public void showRecordsCount(int recordsCount, int enabledRecordsCount) {
            mRecordsCountLinearLayout.setVisibility(View.VISIBLE);
            mRecordsCountTextView.setInteger(recordsCount, mRecordsCountEverShown);
            mEnabledRecordsCountTextView.setInteger(enabledRecordsCount, mRecordsCountEverShown);
            if (!mRecordsCountEverShown) {
                mRecordsCountEverShown = true;
            }
        }
    }
}
