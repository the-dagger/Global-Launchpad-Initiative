package com.dagger.globalinfo.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dagger.globalinfo.GlobalInfoApplication;
import com.dagger.globalinfo.R;
import com.dagger.globalinfo.activity.PreferenceActivity;

/**
 * Created by Harshit on 09/01/17.
 */

public class PreferenceFragment extends android.preference.PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SwitchPreference nightMode = (SwitchPreference) getPreferenceManager().findPreference("preferenceTheme");
        nightMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                GlobalInfoApplication.getSharedPreferences().edit().putBoolean("preferenceTheme", (Boolean) o).apply();
                Intent intent = new Intent(getActivity(), PreferenceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                if ((boolean) o) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    startActivity(intent);
                    getActivity().finish();
                } else if (!(boolean) o) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    startActivity(intent);
                    getActivity().finish();
                }
                return true;
            }
        });

        ListPreference listPreference = (ListPreference) getPreferenceManager().findPreference("preferenceNotifTime");
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Integer frequency = Integer.valueOf(o.toString());
                GlobalInfoApplication.getSharedPreferences().edit().putInt("preferenceNotifTime",frequency).apply();
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
