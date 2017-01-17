package com.dagger.globalinfo.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.dagger.globalinfo.BuildConfig;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    public static final String KEY_ADMINS = "admins";
    public static ArrayList<String> admins = new ArrayList<>();
    public static boolean isAdmin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (user== null) {
            return;
        }

        loadAdmins();
    }

    private void loadAdmins() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        remoteConfig.setConfigSettings(configSettings);

        long cacheExpiration = 3600;
        if (remoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        remoteConfig.fetch(cacheExpiration).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                remoteConfig.activateFetched();
                String admins = remoteConfig.getString(KEY_ADMINS);
                isAdmin = !TextUtils.isEmpty(admins)
                        && admins.contains(user.getUid());
                fab.setVisibility(isAdmin ? View.VISIBLE : View.INVISIBLE);
                if (mSectionsPagerAdapter != null) {

                    //Redraw everything.
                    mSectionsPagerAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}