package com.indian.youthcareerinfo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.indian.youthcareerinfo.Adapters.NotificationAdapter;
import com.indian.youthcareerinfo.R;
import com.indian.youthcareerinfo.Services.UploadSmsAutoService;
import com.indian.youthcareerinfo.model.UploadNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";
    SwipeRefreshLayout swipeLayout;
    RecyclerView notificationRv;

    private NotificationAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private List<UploadNotification> mUploads;
    private LinearLayoutManager mlayoutManager;
    SharedPreferences preferences;
    public long creationDate;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        preferences = getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        creationDate = preferences.getLong("creationDate", 0);
        notificationRv = view.findViewById(R.id.notification_rv);
        swipeLayout = view.findViewById(R.id.notification_swipe_container);
        mProgressCircle = view.findViewById(R.id.progressbarId);
        mlayoutManager = new LinearLayoutManager(getContext());
        notificationRv.setLayoutManager(mlayoutManager);
        mUploads = new ArrayList<>();


        loadNotification();

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNotification();
                mAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });
        return view;
    }

    private void loadNotification() {
        DatabaseReference notificarionDatabaseRef = FirebaseDatabase.getInstance().getReference("Notification");
        notificarionDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UploadNotification uploadNotification = postSnapshot.getValue(UploadNotification.class);
                    if(uploadNotification.getTime() >= creationDate - 604800000 ) {
                        Log.d(TAG, "onDataChange: "+creationDate);
                        Log.d(TAG, "onDataChange: "+uploadNotification.getTime());
                        mUploads.add(uploadNotification);
                    }
                }
                Collections.reverse(mUploads);
                mAdapter = new NotificationAdapter(getContext(), mUploads);

                notificationRv.setAdapter(mAdapter);
//                mAdapter.setOnItemClickListener(NotificationFragment.this);
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }
}