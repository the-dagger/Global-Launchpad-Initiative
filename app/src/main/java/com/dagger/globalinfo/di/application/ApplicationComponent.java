package com.dagger.globalinfo.di.application;

import android.content.SharedPreferences;

import com.dagger.globalinfo.GlobalInfoApplication;
import com.dagger.globalinfo.activity.BaseActivity;
import com.dagger.globalinfo.di.qualifiers.Content;
import com.dagger.globalinfo.di.qualifiers.Education;
import com.dagger.globalinfo.di.qualifiers.Hack;
import com.dagger.globalinfo.di.qualifiers.Meet;
import com.dagger.globalinfo.di.qualifiers.Technical;
import com.dagger.globalinfo.fragment.PreferenceFragment;
import com.dagger.globalinfo.service.FetchInfoService;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by saurabh on 16/1/17.
 */
@Component(modules = ApplicationModule.class)
@Singleton
public interface ApplicationComponent {
    void inject(BaseActivity baseActivity);


    void inject(PreferenceFragment preferenceFragment);

    void inject(FetchInfoService fetchInfoService);

    void inject(GlobalInfoApplication application);

    SharedPreferences preferences();

    @Meet
    DatabaseReference meetReference();

    @Technical
    DatabaseReference technicalReference();

    @Hack
    DatabaseReference hackReference();

    @Education
    DatabaseReference educationReference();

    @Content
    DatabaseReference contentReference();
}
