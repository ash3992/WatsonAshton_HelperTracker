package com.example.watsonashton_helpertracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.watsonashton_helpertracker.fragments.LogInFragment;

public class SignUpFragment extends Fragment {

    SignUpListener mListener;

    public interface  SignUpListener{
        //void LogInNewUserClicked();
        void SignUpAlreadyAnUserClicked();
    }

    public static SignUpFragment newInstance(){
        Bundle args = new Bundle();
        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof SignUpFragment.SignUpListener){
            mListener = (SignUpFragment.SignUpListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up_layout,container, false);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textNewUser = requireView().findViewById(R.id.textViewAlreadyAnUser);
        textNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.SignUpAlreadyAnUserClicked();
            }
        });
    }
}
