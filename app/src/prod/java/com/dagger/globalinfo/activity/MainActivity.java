package com.dagger.globalinfo.activity;

import android.view.View;

public class MainActivity extends BaseActivity {

    @Override
    protected void onResume() {
        super.onResume();
        if (admins.contains(auth.getCurrentUser().getEmail())){
            fab.setVisibility(View.VISIBLE);
        }
    }
}