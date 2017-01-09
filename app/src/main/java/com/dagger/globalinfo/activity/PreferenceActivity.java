package com.dagger.globalinfo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dagger.globalinfo.R;
import com.dagger.globalinfo.fragment.PreferenceFragment;

/**
 * Created by Harshit on 09/01/17.
 */

public class PreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preference_fragment, new PreferenceFragment())
                .commit();
    }
}
