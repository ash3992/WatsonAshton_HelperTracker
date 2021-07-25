package com.example.watsonashton_helpertracker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.watsonashton_helpertracker.R;

public class LogInFragment extends Fragment {

    LogInListener mListener;


    public interface  LogInListener{
        void LogInNewUserClicked();
        void LogInFieldsEmpty();
        void LogInUser(String email, String password);

    }

    public static LogInFragment newInstance(){
        Bundle args = new Bundle();
        LogInFragment fragment = new LogInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof LogInFragment.LogInListener){
            mListener = (LogInFragment.LogInListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
   return inflater.inflate(R.layout.fragment_sign_in_layout,container, false);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText loginEmailEditText = requireView().findViewById(R.id.editTextLogInEmail);
        EditText loginPasswordEditText = requireView().findViewById(R.id.editTextLogInPassword);
        Button loginButton = requireView().findViewById(R.id.loginButton);
        TextView textNewUser = requireView().findViewById(R.id.textViewNewUser);
        textNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.LogInNewUserClicked();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginEmailEditText.getText().toString().trim().equals("") ||
                        loginPasswordEditText.getText().toString().trim().equals("")){
                    mListener.LogInFieldsEmpty();
                }else{

                    mListener.LogInUser(loginEmailEditText.getText().toString(),
                            loginPasswordEditText.getText().toString());
                }
            }
        });


    }
}
