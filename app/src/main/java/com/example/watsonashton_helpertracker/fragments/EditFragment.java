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
import com.example.watsonashton_helpertracker.objects.Contacts;

public class EditFragment extends Fragment {
      private static final String ARG_CONTACTS = "ARG_CONTACTS";
    EditFragmentListener mListener;

    public interface EditFragmentListener{
        void EditContactFieldEmpty();
        void EditContactIsReadyToAdd(String firstName, String lastName, String phoneNum);
    }


    public static EditFragment newInstance(Contacts contacts){
        Bundle args = new Bundle();
        EditFragment fragment = new  EditFragment();
        args.putSerializable(ARG_CONTACTS, contacts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof EditFragment.EditFragmentListener){
            mListener = (EditFragment.EditFragmentListener) context;
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_contact_layout,container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull  Menu menu, @NonNull  MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.save_new_contact, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        EditText firstName = requireView().findViewById(R.id.editTextTextPersonFirstName);
        EditText lastName = requireView().findViewById(R.id.editTextTextPersonLastName);
        EditText phoneNum = requireView().findViewById(R.id.editTextEditPhone);

        if(firstName.getText().toString().trim().equals("") ||
                lastName.getText().toString().trim().equals("")||
                phoneNum.getText().toString().trim().equals("")){

            mListener.EditContactFieldEmpty();
        }else{
            mListener.EditContactIsReadyToAdd(firstName.getText().toString(), lastName.getText().toString(), phoneNum.getText().toString());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        Contacts contact = (Contacts) requireArguments().getSerializable(ARG_CONTACTS);
        EditText firstName = requireView().findViewById(R.id.editTextTextPersonFirstName);
        EditText lastName = requireView().findViewById(R.id.editTextTextPersonLastName);
        EditText phoneNum = requireView().findViewById(R.id.editTextEditPhone);

        String name = contact.getFullName();
        String[] stringArray = name.split(" ");

       firstName.setText(stringArray[0]);
       lastName.setText(stringArray[1]);
       phoneNum.setText(contact.getPhoneNum());

    }
}
