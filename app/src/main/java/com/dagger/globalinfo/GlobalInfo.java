package com.dagger.globalinfo;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Harshit on 26/12/16.
 */

public class GlobalInfo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
