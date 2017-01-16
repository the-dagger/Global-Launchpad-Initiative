package com.dagger.globalinfo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;

import com.dagger.globalinfo.di.application.ApplicationComponent;
import com.dagger.globalinfo.di.application.ApplicationModule;
import com.dagger.globalinfo.di.application.DaggerApplicationComponent;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;

/**
 * Created by Harshit on 06/01/17.
 */

public class GlobalInfoApplication extends Application {

    private static int count;
    @Inject
    SharedPreferences preferences;
    private ApplicationComponent component;

    public static int getCount() {
        return count;
    }

    public static void incrementCount() {
        count++;
    }

    public static GlobalInfoApplication get(Context context) {
        return (GlobalInfoApplication) context.getApplicationContext();
    }

    public ApplicationComponent getComponent() {
        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        component.inject(this);
        if (preferences.getBoolean("preferenceTheme", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
}