package com.dagger.globalinfo;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Harshit on 06/01/17.
 */

public class GlobalInfo extends Application {

    private static GlobalInfo instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static GlobalInfo getInstance(){
        return instance;
    }

}