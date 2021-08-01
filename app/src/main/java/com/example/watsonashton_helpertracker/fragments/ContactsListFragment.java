package com.example.watsonashton_helpertracker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.watsonashton_helpertracker.adapters.ContactsAdapter;
import com.example.watsonashton_helpertracker.R;
import com.example.watsonashton_helpertracker.objects.Contacts;

import java.util.ArrayList;

public class ContactsListFragment extends ListFragment {
   ContactListener mListener;
    private static final String ARG_NUMBERS = "ARG_NUMBERS";

    public interface  ContactListener{
       void ContactAdd();
       void ContactListItemWasClicked(int i);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ContactsListFragment.ContactListener){
            mListener = (ContactsListFragment.ContactListener) context;
        }
    }

    public static ContactsListFragment newInstance(ContactsAdapter contactsAdapter) {

        Bundle args = new Bundle();

        ContactsListFragment fragment = new ContactsListFragment();
       fragment.setListAdapter(contactsAdapter);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments() != null){
            ArrayList<Contacts> people = (ArrayList<Contacts>) getArguments().get(ARG_NUMBERS);

            if (people != null && getContext() != null) {
                ArrayAdapter<Contacts> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        people

                );
                setListAdapter(adapter);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull  MenuItem item) {
        mListener.ContactAdd();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull  View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mListener.ContactListItemWasClicked(position);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull  MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.contacts_lists_menu, menu);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_number_list_view,container,false);

    }


}
