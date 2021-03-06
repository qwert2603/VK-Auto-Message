package com.qwert2603.vkautomessage;

import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Scheduler;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Класс для передачи событий от модели к презентерам.
 */
public class RxBus {

    public static class Event {
        public static final int EVENT_USERS_VK_DATA_UPDATED = 1;
        public static final int EVENT_MODE_SHOW_ERRORS_CHANGED = 2;
        public static final int EVENT_RECORD_ENABLED_CHANGED = 3;

        public final int mEvent;
        @Nullable public final Object mObject;

        public Event(int event, @Nullable Object object) {
            mEvent = event;
            mObject = object;
        }
    }

    @Named(Const.UI_THREAD)
    @Inject
    Scheduler mUiScheduler;

    public RxBus() {
        VkAutoMessageApplication.getAppComponent().inject(RxBus.this);
    }

    private final Subject<Event, Event> mBus = new SerializedSubject<>(PublishSubject.create());

    public void send(Event o) {
        mBus.onNext(o);
    }

    public Observable<Event> toObservable() {
        return mBus.observeOn(mUiScheduler);
    }

}
