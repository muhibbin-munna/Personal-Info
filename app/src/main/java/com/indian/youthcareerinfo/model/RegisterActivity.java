package com.indian.youthcareerinfo.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.indian.youthcareerinfo.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_NID_IMAGE_REQUEST = 1;
    private static final int PICK_PHOTO_IMAGE_REQUEST = 2;
    private EditText firstNameEditText, lastNameEditText, signUpEmailEditText, phoneNoEditText,
            signUpPasswordEditText;
    private Spinner genderSpinnerText, educationSpinnerText;
//    private DatePicker datePicker;
    private Button nidImageButton, photoImageButton, signUpButton2Button;
    private TextView logInBackEditText;
    private FirebaseAuth mAuth;
    private StorageReference nidImageStorageRef, photoImageStorageRef;
    private Uri nidImageUri, photoImageUri;
    private StorageTask mUploadTask1, mUploadTask2;

    private Spinner daySpinnerText,monthSpinnerText,yearSpinnerText;

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nidImageStorageRef = FirebaseStorage.getInstance().getReference("nid");
        photoImageStorageRef = FirebaseStorage.getInstance().getReference("photo");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        firstNameEditText = findViewById(R.id.firstNameId);
        lastNameEditText = findViewById(R.id.lastNameId);
        phoneNoEditText = findViewById(R.id.phoneId);
        genderSpinnerText = findViewById(R.id.genderSpinnerId);
        educationSpinnerText = findViewById(R.id.educationSpinnerId);
//        datePicker = findViewById(R.id.datePickerId);
        signUpEmailEditText = findViewById(R.id.signupnEmailId);
        signUpPasswordEditText = findViewById(R.id.signUpPasswordId);
        nidImageButton = findViewById(R.id.nidimagebutton);
        photoImageButton = findViewById(R.id.photoimagebutton);
        signUpButton2Button = findViewById(R.id.signUp2ButtonId);
        logInBackEditText = findViewById(R.id.loginBackId);
        daySpinnerText = findViewById(R.id.daySpinner);
        monthSpinnerText = findViewById(R.id.monthSpinner);
        yearSpinnerText = findViewById(R.id.yearSpinner);

        yearSpinnerText.setSelection(100);

        nidImageButton.setOnClickListener(this);
        photoImageButton.setOnClickListener(this);
        signUpButton2Button.setOnClickListener(this);
        logInBackEditText.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nidimagebutton:
                openNidFileChooser();
                break;
            case R.id.photoimagebutton:
                openPhotoFileChooser();
                break;
            case R.id.signUp2ButtonId:
                userRegister();
                break;
            case R.id.loginBackId:
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivity);
                break;
        }
    }


    private void openPhotoFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_PHOTO_IMAGE_REQUEST);
    }

    private void openNidFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_NID_IMAGE_REQUEST);
    }

    private void userRegister() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = signUpEmailEditText.getText().toString().trim();
        String phoneNo = phoneNoEditText.getText().toString().trim();
        String password = signUpPasswordEditText.getText().toString().trim();
        if (firstName.isEmpty()) {
            firstNameEditText.setError("Enter a First Name");
            firstNameEditText.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            lastNameEditText.setError("Enter a Last Name");
            lastNameEditText.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            signUpEmailEditText.setError("Enter an email address");
            signUpEmailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signUpEmailEditText.setError("Enter a valid email address");
            signUpEmailEditText.requestFocus();
            return;
        }
        if (phoneNo.isEmpty()) {
            phoneNoEditText.setError("Enter a Phone No");
            phoneNoEditText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            signUpPasswordEditText.setError("Enter a password");
            signUpPasswordEditText.requestFocus();
            return;
        }
        if (password.length() < 8) {
            signUpPasswordEditText.setError("Enter at least 8 characters");
            signUpPasswordEditText.requestFocus();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    saveData();
                    Toast.makeText(getApplicationContext(), "Registered", Toast.LENGTH_LONG).show();

                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "Already Registered", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Not Registered", Toast.LENGTH_LONG).show();
                    }


                }

            }
        });
    }
    private void saveData() {

        String day = daySpinnerText.getSelectedItem().toString();
        String month = monthSpinnerText.getSelectedItem().toString();
        String year = yearSpinnerText.getSelectedItem().toString();

        String birthDate = day + "/"+ month + "/"+ year;
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        final String email = signUpEmailEditText.getText().toString().trim();
        String phoneNo = phoneNoEditText.getText().toString().trim();

        String gender = genderSpinnerText.getSelectedItem().toString();
        String education = educationSpinnerText.getSelectedItem().toString();
        String password = signUpPasswordEditText.getText().toString().trim();

        final String uploadId = mDatabaseRef.push().getKey();
        Upload upload = new Upload(firstName, lastName, email, phoneNo, gender, education, birthDate, password);

        mDatabaseRef.child(uploadId).setValue(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                SharedPreferences preferences;
                preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("email",email);
                editor.putString("uploadId",uploadId);
                editor.putBoolean("loggedIn",true);
                editor.apply();
                Intent profileActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
                profileActivity.putExtra("email",email);
                profileActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(profileActivity);
            }
        });

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
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
        if (photoImageUri != null) {
            final long time2 = System.currentTimeMillis();
            StorageReference fileReference2 = photoImageStorageRef.child(time2
                    + "." + "jpg");
            mUploadTask2 = fileReference2.putFile(photoImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            mDatabaseRef.child(uploadId).child("photourl").setValue(downloadUrl.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_NID_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            String ext = getFileExtension(data.getData());
            if (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("png")) {
                nidImageUri = data.getData();
                nidImageButton.setText(getFileName(nidImageUri));
            } else {
                Toast.makeText(this, "enter a jpg/jpeg/png", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_PHOTO_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            String ext = getFileExtension(data.getData());
            if (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("png")) {
                photoImageUri = data.getData();
                photoImageButton.setText(getFileName(photoImageUri));
            } else {
                Toast.makeText(this, "enter a jpg/jpeg/png", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
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
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}