package com.indian.youthcareerinfo.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.indian.youthcareerinfo.R;

import static com.google.android.play.core.install.model.AppUpdateType.*;
import static com.google.android.play.core.install.model.InstallStatus.*;
import static com.google.android.play.core.install.model.UpdateAvailability.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button customSigninButton;
    private EditText emailEditText, passwordEditText;
    private TextView signUp, forgetpassword;
    private FirebaseAuth mAuth;
    SharedPreferences preferences;
    AppUpdateManager appUpdateManager;

    DatabaseReference mDatabaseReference;
    Query query;
    String email, uploadId;

    CheckBox checkBox;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailEditText = findViewById(R.id.email_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        customSigninButton = findViewById(R.id.custom_signin_button);
        signUp = findViewById(R.id.sign_up);
        forgetpassword = findViewById(R.id.forgetpassword);
        checkBox = findViewById(R.id.showPass);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");


        preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        email = preferences.getString("email", "");
        email = email.trim();
        checkUpdate();
        boolean loggedIn = preferences.getBoolean("loggedIn", false);
        if (loggedIn) {
            checkAndRequestPermissions();
            Intent profileActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
            profileActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            profileActivity.putExtra("email", email);
            finish();
            startActivity(profileActivity);
        }
        checkAndRequestPermissions();
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (passwordEditText.getText().toString().trim().length() >= 8) {
                    customSigninButton.setBackgroundColor(getResources().getColor(R.color.green));
                } else {
                    customSigninButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordEditText.setSelection(passwordEditText.getText().length());
                } else {
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordEditText.setSelection(passwordEditText.getText().length());
                }
            }
        });


        mAuth = FirebaseAuth.getInstance();

        customSigninButton.setOnClickListener(this);
        signUp.setOnClickListener(this);
        forgetpassword.setOnClickListener(this);
    }

    private void checkUpdate() {
        Log.d("TAG", "onActivityResult: 11");
        appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                Log.d("TAG", "onActivityResult: 1");
                if ((appUpdateInfo.updateAvailability() == UPDATE_AVAILABLE)
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    // Request the update.
                    Log.d("TAG", "onActivityResult: 2");

                    try {
                        Log.d("TAG", "onActivityResult: 3");
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                IMMEDIATE,
                                // The current activity making the update request.
                                MainActivity.this,
                                // Include a request code to later monitor this update request.
                                111);
                    } catch (IntentSender.SendIntentException ignored) {
                        Log.d("TAG", "onActivityResult: 4");
                    }
                }
            }
        });
        appUpdateManager.registerListener(installStateUpdatedListener);
    }

    InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState installState) {
            if (installState.installStatus() == DOWNLOADED) {
                Log.d("TAG", "onActivityResult: 10");
                MainActivity.this.popupSnackbarForCompleteUpdate();
            } else
                Log.e("UPDATE", "Not downloaded yet");
        }
    };


    private void popupSnackbarForCompleteUpdate() {

        Snackbar snackbar =
                Snackbar.make(
                        findViewById(android.R.id.content),
                        "Update almost finished!",
                        Snackbar.LENGTH_INDEFINITE);
        //lambda operation used for below action
        snackbar.setAction(this.getString(R.string.restart), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.green));
        snackbar.show();
    }

    protected void onResume() {
        super.onResume();
        if (appUpdateManager != null) {
            appUpdateManager.getAppUpdateInfo()
                    .addOnSuccessListener(
                            new OnSuccessListener<AppUpdateInfo>() {
                                @Override
                                public void onSuccess(AppUpdateInfo appUpdateInfo) {
                                    Log.d("TAG", "onActivityResult: 9");
                                    if (appUpdateInfo.updateAvailability()
                                            == DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                        // If an in-app update is already running, resume the update.
                                        try {
                                            appUpdateManager.startUpdateFlowForResult(
                                                    appUpdateInfo,
                                                    IMMEDIATE,
                                                    MainActivity.this,
                                                    111);
                                            Log.d("TAG", "onActivityResult: 7");
                                        } catch (IntentSender.SendIntentException e) {
                                            Log.d("TAG", "onActivityResult: 8");
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode != RESULT_OK) {
                checkUpdate();
                Log.d("TAG", "onActivityResult: 6");
            } else {
                Log.d("TAG", "onActivityResult: 5");
            }
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.custom_signin_button:
                userLogIn();
                break;
            case R.id.sign_up:

                Intent signUpIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(signUpIntent);
                break;
            case R.id.forgetpassword:
                forgetpasswordoption();
                break;
        }
    }

    private void forgetpasswordoption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.forget_password, null);
        final EditText forgetemail;
        forgetemail = view.findViewById(R.id.forgetemail);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String mail = forgetemail.getText().toString();
                        if (mail.length()>0) {
                            mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.setTitle("Reset Password?");
        builder.setMessage("Enter Your Email To Received Reset Link.");
        builder.create();
        builder.show();
    }

    private boolean checkAndRequestPermissions() {
        int sms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        if (sms != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);
            return false;
        }
        return true;
    }

    private void userLogIn() {
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Enter an email address");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            emailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Enter a password");
            passwordEditText.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    query = mDatabaseReference.orderByChild("email").equalTo(email);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                uploadId = child.getKey();
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("uploadId", uploadId);
                                editor.apply();
                                Log.d(TAG, "onDataChange: " + uploadId);
                            }
                            if(uploadId!=null) {
                                mDatabaseReference.child(uploadId).child("password").setValue(password);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    SharedPreferences.Editor editor = preferences.edit();

                    Intent profileActivity = new Intent(getApplicationContext(), WelcomeActivity.class);
                    profileActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    FirebaseUser user = task.getResult().getUser();
                    editor.putString("email", email);
                    editor.putLong("creationDate", user.getMetadata().getCreationTimestamp());
                    editor.putBoolean("loggedIn", true);
                    editor.apply();
                    profileActivity.putExtra("email", email);
                    finish();
                    startActivity(profileActivity);


                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}