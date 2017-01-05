package com.dagger.globalinfo.service;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dagger.globalinfo.R;
import com.dagger.globalinfo.model.InfoObject;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.dagger.globalinfo.activity.MainActivity.CONTENT;

/**
 * Created by Harshit on 01/01/17.
 */

public class FetchInfoService extends JobService {

    InfoObject note;
    NotificationCompat.Builder notificationBuilder;
    private static final String TAG = "SyncService";

    public FetchInfoService() {
    }

    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
        Log.e(TAG, "Executing job id: " + job.getTag());
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference().child(CONTENT);
        Log.e(TAG,dbReference.getKey());
        
        dbReference.addValueEventListener(new ValueEventListener() {    // Never called

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG,"OnDataChanged Called");
                for (DataSnapshot infoDataSnapshot : dataSnapshot.getChildren()) {
                    note = infoDataSnapshot.getValue(InfoObject.class);
                }
                notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_delete_black_48dp)
                        .setContentTitle(note.getTitle())
                        .setContentText(note.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,"Listener Cancelled");
            }
        });
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, notificationBuilder.build());  // Null Pointer
        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        Log.e(TAG, "Finished job: " + job.getTag());
        return false;
    }

}
