package com.qwert2603.vkautomessage.di;

import com.qwert2603.vkautomessage.errors_show.ErrorsHolder;
import com.qwert2603.vkautomessage.model.DataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PresenterModule {

    @Provides
    @Singleton
    DataManager provideDataManager() {
        return new DataManager();
    }

    @Provides
    @Singleton
    ErrorsHolder errorsHolder() {
        return new ErrorsHolder();
    }

}
