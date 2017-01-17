package com.dagger.globalinfo.di.application;

import android.app.Application;
import android.content.SharedPreferences;

import com.dagger.globalinfo.di.qualifiers.Content;
import com.dagger.globalinfo.di.qualifiers.Education;
import com.dagger.globalinfo.di.qualifiers.Hack;
import com.dagger.globalinfo.di.qualifiers.Meet;
import com.dagger.globalinfo.di.qualifiers.Technical;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by saurabh on 16/1/17.
 */
@Module
public class ApplicationModule {
    private static final String EDUCATION = "education";
    private static final String HACKATHONS = "hackathons";
    private static final String MEETUPS = "meetups";
    private static final String TECHNICAL = "technical";
    private static final String CONTENT = "content";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private Application application;
    private FirebaseAuth firebaseAuth;

    public ApplicationModule(Application application) {
        this.application = application;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Singleton
    @Education
    @Provides
    DatabaseReference provideEducationReference() {
        return firebaseDatabase.getReference().child(EDUCATION);
    }

    @Singleton
    @Hack
    @Provides
    DatabaseReference provideHackReference() {
        return firebaseDatabase.getReference().child(HACKATHONS);
    }

    @Singleton
    @Meet
    @Provides
    DatabaseReference provideMeetReference() {
        return firebaseDatabase.getReference().child(MEETUPS);
    }

    @Singleton
    @Technical
    @Provides
    DatabaseReference provideTechnicalReference() {
        return firebaseDatabase.getReference().child(TECHNICAL);
    }

    @Singleton
    @Content
    @Provides
    DatabaseReference provideContentReference() {
        return firebaseDatabase.getReference().child(CONTENT);
    }

    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences() {
        return application.getSharedPreferences("com.dagger.globalinfo", MODE_PRIVATE);
    }

    @Singleton
    @Provides
    FirebaseJobDispatcher provideJobDispatcher() {
        return new FirebaseJobDispatcher(new GooglePlayDriver(application));
    }

    @Singleton
    @Provides
    FirebaseAuth provideAuth() {
        return firebaseAuth;
    }

    @Singleton
    @Provides
    FirebaseStorage provideStorage() {
        return FirebaseStorage.getInstance();
    }

    @Singleton
    @Provides
    String provideContent() {
        return CONTENT;
    }

}
