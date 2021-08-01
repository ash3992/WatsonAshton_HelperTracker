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

public class AddNewContactFragment extends Fragment {

    AddNewContactFragmentListener mListener;
    EditText firstName;
    EditText lastName;
    EditText phoneNumber;

    public interface  AddNewContactFragmentListener{
        void addContactFieldsEmpty();
        void addContactReadyToAdd(String f_name, String l_name, String phoneNum);

    }

    public static AddNewContactFragment newInstance(){
        Bundle args = new Bundle();
        AddNewContactFragment fragment = new AddNewContactFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof AddNewContactFragment.AddNewContactFragmentListener){
            mListener = (AddNewContactFragment.AddNewContactFragmentListener) context;
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        firstName =  requireView().findViewById(R.id.editTextFirstName);
        lastName = requireView().findViewById(R.id.editTextLastName);
        phoneNumber = requireView().findViewById(R.id.editTextPhone);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.saving_contact, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull  MenuItem item) {

        if(firstName.getText().toString().trim().equals("") ||
                lastName.getText().toString().trim().equals("")||
                phoneNumber.getText().toString().trim().equals("")){

            mListener.addContactFieldsEmpty();
        }else{
            mListener.addContactReadyToAdd(firstName.getText().toString(), lastName.getText().toString(), phoneNumber.getText().toString());
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_contact_view,container, false);
    }
}
