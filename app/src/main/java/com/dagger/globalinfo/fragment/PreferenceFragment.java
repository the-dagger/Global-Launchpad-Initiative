package com.dagger.globalinfo.fragment;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dagger.globalinfo.GlobalInfoApplication;
import com.dagger.globalinfo.R;
import com.dagger.globalinfo.di.activity.ActivityComponent;
import com.dagger.globalinfo.di.activity.DaggerActivityComponent;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;
import com.takisoft.fix.support.v7.preference.SwitchPreferenceCompat;

import javax.inject.Inject;

/**
 * Created by Harshit on 09/01/17.
 */

public class PreferenceFragment extends PreferenceFragmentCompat {
    @Inject
    SharedPreferences preferences;

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ActivityComponent component = DaggerActivityComponent.builder()
                .applicationComponent(GlobalInfoApplication.get(getContext()).getComponent())
                .build();
        component.inject(this);
        SwitchPreferenceCompat nightMode = (SwitchPreferenceCompat) getPreferenceManager().findPreference("preferenceTheme");
        nightMode.setOnPreferenceChangeListener(new android.support.v7.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.support.v7.preference.Preference preference, Object o) {

                preferences.edit().putBoolean("preferenceTheme", (Boolean) o).apply();
                if ((boolean) o) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    getActivity().recreate();
                    getActivity().setResult(AppCompatDelegate.MODE_NIGHT_YES);
                } else if (!(boolean) o) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getActivity().recreate();
                    getActivity().setResult(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            }
        });

        ListPreference listPreference = (ListPreference) getPreferenceManager().findPreference("preferenceNotifTime");
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Integer frequency = Integer.valueOf(o.toString());
                preferences.edit().putInt("preferenceNotifTime", frequency).apply();
                return true;
            }
        });

        try {
            getPreferenceManager().findPreference("version").setSummary(getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        getPreferenceManager().findPreference("sourceCode").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                        .addDefaultShareMenuItem()
                        .setShowTitle(true);
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(getActivity(), Uri.parse("https://github.com/the-dagger/Global-Launchpad-Initiative"));
                return false;
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

}
