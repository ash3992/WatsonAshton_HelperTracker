package com.example.watsonashton_helpertracker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.watsonashton_helpertracker.R;

public class HomeScreenFragment extends Fragment {
    HomeScreenListener mListener;
    MenuItem contacts;
    MenuItem profile;
    MenuItem logOut;


    public interface  HomeScreenListener{

        void SignalButtonPushed();
        void StopSignalButtonHasBeenPushed();
        void UserLogOut();
        void UserClickContactsList();
        void ProfileIconClick();

    }

    public static HomeScreenFragment newInstance(){
        Bundle args = new Bundle();
        HomeScreenFragment fragment = new HomeScreenFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof HomeScreenFragment.HomeScreenListener){
            mListener = (HomeScreenFragment.HomeScreenListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_screen_layout,container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull  Menu menu, @NonNull  MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_screen_menu, menu);
        contacts = menu.getItem(0);
        profile = menu.getItem(1);
        logOut = menu.getItem(2);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull  MenuItem item) {

        if(item.getItemId() == R.id.log_out_icon){
            mListener.UserLogOut();
        }
        else if(item.getItemId() == R.id.contacts_icon){
            mListener.UserClickContactsList();
        }
        else if(item.getItemId() == R.id.profile_icon){
            mListener.ProfileIconClick();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    ImageView imageClick = requireView().findViewById(R.id.imageViewStartSignal);
    imageClick.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            contacts.setEnabled(false);
            profile.setEnabled(false);
            logOut.setEnabled(false);
            mListener.SignalButtonPushed();
        }
    });

        Button stopSignal = requireView().findViewById(R.id.buttonStopSignal);
        stopSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contacts.setEnabled(true);
                profile.setEnabled(true);
                logOut.setEnabled(true);
                mListener.StopSignalButtonHasBeenPushed();
            }
        });




    }
}
