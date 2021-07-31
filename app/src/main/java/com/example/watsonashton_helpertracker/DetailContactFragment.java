package com.example.watsonashton_helpertracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.watsonashton_helpertracker.objects.Contacts;

import java.util.Objects;

public class DetailContactFragment extends Fragment {
    private static final String ARG_CONTACTS = "ARG_CONTACTS";
  DetailContactFragmentListener mListener;

    public interface DetailContactFragmentListener{
        //void addContactFieldsEmpty();
       // void addContactReadyToAdd(String f_name, String l_name, String phoneNum);
        void EditContact();
        void TrashContact(String firstName, String lastName, String phoneNum);


    }


    public static DetailContactFragment newInstance(Contacts contacts){
        Bundle args = new Bundle();
        DetailContactFragment fragment = new  DetailContactFragment();
        args.putSerializable(ARG_CONTACTS, contacts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof DetailContactFragment.DetailContactFragmentListener){
            mListener = (DetailContactFragment.DetailContactFragmentListener) context;
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_detail_view,container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull  Menu menu, @NonNull  MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.details_contacts_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull  MenuItem item) {

        Contacts contact = (Contacts) requireArguments().getSerializable(ARG_CONTACTS);

        String name = contact.getFullName();
        String[] stringArray = name.split(" ");

        if(item.getItemId() == R.id.edit_icon){
            mListener.EditContact();

        }else if(item.getItemId() == R.id.trash_icon){
            mListener.TrashContact(stringArray[0], stringArray[1], contact.getPhoneNum());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        Contacts contact = (Contacts) requireArguments().getSerializable(ARG_CONTACTS);
        TextView name = requireView().findViewById(R.id.textViewFirstNameDetail);
        TextView phoneNum  = requireView().findViewById(R.id.textViewPhoneNumberDetail);
        name.setText(contact.getFullName());
        phoneNum.setText(contact.getPhoneNum());

    }
}
