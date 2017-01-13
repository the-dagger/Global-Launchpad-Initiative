package com.dagger.globalinfo;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Harshit on 06/01/17.
 */

public class GlobalInfoApplication extends Application {

    private static final String EDUCATION = "education";
    private static final String HACKATHONS = "hackathons";
    private static final String MEETUPS = "meetups";
    private static final String TECHNICAL = "technical";
    private static final String CONTENT = "content";
    private static FirebaseAuth auth;
    private static DatabaseReference eduDbReference, hackDbReference, meetDbReference, techDbReference, contentDbReference;
    private static FirebaseDatabase firebaseDatabase;
    private static FirebaseJobDispatcher dispatcher;

    public static int getCount() {
        return count;
    }

    public static void incrementCount(){
        count++;
    }

    private static int count;

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    private static SharedPreferences sharedPreferences;

    public static FirebaseAuth getAuth() {
        return auth;
    }

    public static DatabaseReference getEduDbReference() {
        return eduDbReference;
    }

    public static DatabaseReference getHackDbReference() {
        return hackDbReference;
    }

    public static DatabaseReference getMeetDbReference() {
        return meetDbReference;
    }

    public static DatabaseReference getTechDbReference() {
        return techDbReference;
    }

    public static DatabaseReference getContentDbReference() {
        return contentDbReference;
    }

    public static FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    public static FirebaseJobDispatcher getJobDispatcher() {
        return dispatcher;
    }

    public static String getEDUCATION() {
        return EDUCATION;
    }

    public static String getHACKATHONS() {
        return HACKATHONS;
    }

    public static String getMEETUPS() {
        return MEETUPS;
    }

    public static String getTECHNICAL() {
        return TECHNICAL;
    }

    public static String getCONTENT() {
        return CONTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        sharedPreferences = getSharedPreferences("com.dagger.globalinfo", MODE_PRIVATE);
        firebaseDatabase = FirebaseDatabase.getInstance();
        eduDbReference = firebaseDatabase.getReference().child(EDUCATION);
        hackDbReference = firebaseDatabase.getReference().child(HACKATHONS);
        meetDbReference = firebaseDatabase.getReference().child(MEETUPS);
        techDbReference = firebaseDatabase.getReference().child(TECHNICAL);
        contentDbReference = firebaseDatabase.getReference().child(CONTENT);
        if (GlobalInfoApplication.getSharedPreferences().getBoolean("preferenceTheme", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        auth = FirebaseAuth.getInstance();
    }
}