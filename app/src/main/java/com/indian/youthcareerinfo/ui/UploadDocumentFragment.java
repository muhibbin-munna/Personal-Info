package com.indian.youthcareerinfo.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.indian.youthcareerinfo.R;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class UploadDocumentFragment extends Fragment {

    Button nidImageButtonUpload,updateButton;
    private static final int PICK_NID_IMAGE_REQUEST = 1;
    private Uri nidImageUri;
    private StorageTask mUploadTask1;
    private DatabaseReference mDatabaseRef;
    private StorageReference nidImageStorageRef;
    private Query query;
    String uploadId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_document, container, false);

        nidImageStorageRef = FirebaseStorage.getInstance().getReference("nid");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");


        nidImageButtonUpload = view.findViewById(R.id.nidimagebuttonUpload);
        updateButton = view.findViewById(R.id.updatebutton);

        nidImageButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNidFileChooser();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences;
                preferences = getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                String email = preferences.getString("email","");
                email = email.trim();
                query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("email").equalTo(email);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child: snapshot.getChildren()) {
//                            Toast.makeText(getContext(), ""+child.getKey(), Toast.LENGTH_SHORT).show();
                            uploadId = child.getKey();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                Toast.makeText(getContext(), ""+uploadId, Toast.LENGTH_SHORT).show();

                final long time1 = System.currentTimeMillis();
                StorageReference fileReference1 = nidImageStorageRef.child(time1
                        + "." + "jpg");
                if (nidImageUri != null) {

                    mUploadTask1 = fileReference1.putFile(nidImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    Uri downloadUrl = urlTask.getResult();
                                    mDatabaseRef.child(uploadId).child("nidurl").setValue(downloadUrl.toString());
                                    Toast.makeText(getContext(), "updated", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });
                }
            }
        });


        return view;
    }

    private void openNidFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_NID_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_NID_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            String ext = getFileExtension(data.getData());
            if (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("png")) {
                nidImageUri = data.getData();
                nidImageButtonUpload.setText(getFileName(nidImageUri));
            } else {
                Toast.makeText(getContext(), "enter a jpg/jpeg/png", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}