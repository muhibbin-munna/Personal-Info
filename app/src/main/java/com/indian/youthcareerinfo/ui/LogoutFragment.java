package com.indian.youthcareerinfo.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indian.youthcareerinfo.model.MainActivity;
import com.indian.youthcareerinfo.R;

import static android.content.Context.MODE_PRIVATE;

public class LogoutFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferences;
        preferences = getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email","");
        editor.putString("uploadId","");
        editor.putLong("creationDate",0);
//        editor.remove("email");
//        editor.remove("uploadId");
        editor.putBoolean("loggedIn",false);
        editor.apply();
        getActivity().finish();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }
}