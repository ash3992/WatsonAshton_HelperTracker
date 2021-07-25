package com.example.watsonashton_helpertracker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.watsonashton_helpertracker.R;

public class NewContactFragment extends Fragment {
    EditText firstName;
    EditText lastName;
    EditText phoneNumber;
    NewContactListener mListener;

    public interface  NewContactListener{
        void newContactFieldsEmpty();
        void newContactReadyToAdd(String f_name, String l_name, String phoneNum);
    }

    public static NewContactFragment newInstance(){
        Bundle args = new Bundle();
        NewContactFragment fragment = new NewContactFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof NewContactFragment.NewContactListener){
            mListener = (NewContactFragment.NewContactListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first_contact_added,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        firstName =  requireView().findViewById(R.id.editTextFirstContactFirstName);
        lastName = requireView().findViewById(R.id.editTextFirstContactLastName);
        phoneNumber = requireView().findViewById(R.id.editTextFirstContactPhoneNumber);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull  Menu menu, @NonNull  MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.save_new_contact, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(firstName.getText().toString().trim().equals("") ||
                lastName.getText().toString().trim().equals("")||
                phoneNumber.getText().toString().trim().equals("")){

            mListener.newContactFieldsEmpty();
        }else{
            mListener.newContactReadyToAdd(firstName.getText().toString(), lastName.getText().toString(), phoneNumber.getText().toString());

        }
        return super.onOptionsItemSelected(item);
    }
}
