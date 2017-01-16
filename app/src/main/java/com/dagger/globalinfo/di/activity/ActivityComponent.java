package com.dagger.globalinfo.di.activity;

import com.dagger.globalinfo.di.application.ApplicationComponent;
import com.dagger.globalinfo.fragment.MainActivityFragment;
import com.dagger.globalinfo.fragment.PreferenceFragment;
import com.dagger.globalinfo.service.FetchInfoService;

import dagger.Component;

/**
 * Created by saurabh on 16/1/17.
 */
@PerActivity
@Component(modules = ActivityModule.class, dependencies = ApplicationComponent.class)
public interface ActivityComponent {
    void inject(PreferenceFragment preferenceFragment);

    void inject(MainActivityFragment mainActivityFragment);

    void inject(FetchInfoService fetchInfoService);
}
