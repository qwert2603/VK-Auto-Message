package com.qwert2603.vkautomessage.base.list;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ViewAnimator;

import com.qwert2603.vkautomessage.R;
import com.qwert2603.vkautomessage.base.BaseRecyclerViewAdapter;
import com.qwert2603.vkautomessage.base.delete_item.DeleteItemDialog;
import com.qwert2603.vkautomessage.base.in_out_animation.AnimationFragment;
import com.qwert2603.vkautomessage.base.navigation.ActivityInterface;
import com.qwert2603.vkautomessage.base.navigation.NavigationActivity;
import com.qwert2603.vkautomessage.model.Identifiable;
import com.qwert2603.vkautomessage.recycler.RecyclerItemAnimator;
import com.qwert2603.vkautomessage.recycler.SimpleOnItemTouchHelperCallback;
import com.qwert2603.vkautomessage.util.LogUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Фрагмент для отображения сообщения об ошибке/загрузке/пустом списке или самого список.
 * Обрабатывает запросы на удаление элемента и переход к подробностям об элементе.
 *
 * @param <T> тип элемента списка
 */
public abstract class ListFragment<T extends Identifiable> extends AnimationFragment<ListPresenter> implements ListView<T> {

    private static final int POSITION_EMPTY_VIEW = 0;
    private static final int POSITION_LOADING_TEXT_VIEW = 1;
    private static final int POSITION_ERROR_TEXT_VIEW = 2;
    private static final int POSITION_EMPTY_TEXT_VIEW = 3;

    protected static final int REQUEST_DELETE_ITEM = 1;
    protected static final int REQUEST_DETAILS_FOT_ITEM = 2;

    @BindView(R.id.root_view)
    protected View mRootView;

    @BindView(R.id.view_animator)
    protected ViewAnimator mViewAnimator;

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    protected RecyclerItemAnimator mRecyclerItemAnimator;

    protected SimpleOnItemTouchHelperCallback mSimpleOnItemTouchHelperCallback;

    @NonNull
    protected abstract BaseRecyclerViewAdapter<T, ?, ?> getAdapter();

    @LayoutRes
    protected abstract int getLayoutRes();

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);

        ButterKnife.bind(ListFragment.this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 500;
            }
        });
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 42);
        mRecyclerView.setAdapter(getAdapter());


        getAdapter().setClickCallback(getPresenter()::onItemAtPositionClicked);
        getAdapter().setLongClickCallback(getPresenter()::onItemAtPositionLongClicked);
        getAdapter().setItemSwipeDismissCallback(position -> {
            // чтобы элемент вернулся в свое исходное положение после swipe.
            getAdapter().notifyItemChanged(position);

            getPresenter().onItemDismissed(position);
        });

        ((ActivityInterface) getActivity()).getToolbarTitle().setOnClickListener(v -> getPresenter().onToolbarClicked());

        mSimpleOnItemTouchHelperCallback=new SimpleOnItemTouchHelperCallback(getAdapter(), Color.TRANSPARENT, ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete_black_24dp));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSimpleOnItemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerItemAnimator = new RecyclerItemAnimator();
        mRecyclerView.setItemAnimator(mRecyclerItemAnimator);
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // тут считается, что высота элемента всегда равна высоте элемента-пользователя.
                // высота элемента-записи отличается несильно, так что этим можно пренебречь.
                float childHeight = getResources().getDimension(R.dimen.item_user_height);
                int itemsPerScreen = 1 + (int) (mRecyclerView.getHeight() / childHeight);
                mRecyclerItemAnimator.setItemsPerScreen(itemsPerScreen);
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        mViewAnimator.getChildAt(POSITION_ERROR_TEXT_VIEW).setOnClickListener(v -> getPresenter().onReloadList());

        return view;
    }

    @Override
    public void onDestroyView() {
        ((ActivityInterface) getActivity()).getToolbarTitle().setOnClickListener(null);
        mRecyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d("onActivityResult " + requestCode + " " + resultCode + " " + data);

        switch (requestCode) {
            case REQUEST_DELETE_ITEM:
                int deletingItemId = data.getIntExtra(DeleteItemDialog.EXTRA_ITEM_TO_DELETE_ID, -1);
                if (resultCode == Activity.RESULT_OK) {
                    getPresenter().onItemDeleteSubmitted(deletingItemId);
                } else {
                    getPresenter().onItemDeleteCanceled(deletingItemId);
                }
                break;
            case REQUEST_DETAILS_FOT_ITEM:
                // TODO: 05.12.2016 при возвращении от активити, созданной через TaskStackBuilder onActivityResult не вызывается =(
                LogUtils.d("onActivityResult REQUEST_DETAILS_FOT_ITEM " + " " + data.getIntExtra(NavigationActivity.EXTRA_ITEM_ID, -1));
                if (resultCode == Activity.RESULT_OK) {
                    int id = data.getIntExtra(NavigationActivity.EXTRA_ITEM_ID, -1);
                    getPresenter().onReloadItem(id);
                }
                getPresenter().onReadyToAnimate();
                break;
        }
    }

    @Override
    public void showLoading() {
        setViewAnimatorDisplayedChild(POSITION_LOADING_TEXT_VIEW);
    }

    @Override
    public void showError() {
        setViewAnimatorDisplayedChild(POSITION_ERROR_TEXT_VIEW);
    }

    @Override
    public void showEmpty() {
        setViewAnimatorDisplayedChild(POSITION_EMPTY_TEXT_VIEW);
    }

    @Override
    public void showListEnter(List<T> list) {
        setViewAnimatorDisplayedChild(POSITION_EMPTY_VIEW);
        getAdapter().insertModelList(list);
    }

    @Override
    public void showList(List<T> list) {
        setViewAnimatorDisplayedChild(POSITION_EMPTY_VIEW);
        getAdapter().replaceModelList(list);
    }

    @Override
    public void showItemSelected(int position) {
        getAdapter().setSelectedItemPosition(position);
    }

    @Override
    public void notifyItemRemoved(int position) {
        getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void notifyItemInserted(int position, int id) {
        LogUtils.d("notifyItemInserted " + position + " " + id);
        mRecyclerItemAnimator.addItemToAnimateEnter(id);
        getAdapter().notifyItemInserted(position);
    }

    @Override
    public void notifyItemsUpdated(List<Integer> updatedPositions) {
        LogUtils.d("updatedPositions " + updatedPositions);
        for (Integer updatedUserPosition : updatedPositions) {
            getAdapter().notifyItemChanged(updatedUserPosition);
        }
    }

    @Override
    public void scrollListToTop() {
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void smoothScrollListToBottom() {
        mRecyclerView.smoothScrollToPosition(getAdapter().getItemCount() - 1);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        mRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void scrollToPosition(int position) {
        mRecyclerView.scrollToPosition(position);
    }

    @Override
    public void animateAllItemsEnter(boolean animate) {
        mRecyclerItemAnimator.setAlwaysAnimateEnter(animate);
    }

    @Override
    public void delayEachItemEnterAnimation(boolean delay) {
        mRecyclerItemAnimator.setDelayEnter(delay);
    }

    private void setViewAnimatorDisplayedChild(int position) {
        mRecyclerView.setVisibility(position == POSITION_EMPTY_VIEW ? View.VISIBLE : View.GONE);
        if (mViewAnimator.getDisplayedChild() != position) {
            mViewAnimator.setDisplayedChild(position);
        }
    }

    @Override
    public int getItemEnterDelayPerScreen() {
        return mRecyclerItemAnimator.getEnterDelayPerScreen();
    }

    @Override
    public int getLastCompletelyVisibleItemPosition() {
        return ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
    }
}
