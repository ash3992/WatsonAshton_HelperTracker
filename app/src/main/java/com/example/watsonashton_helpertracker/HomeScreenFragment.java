package com.example.watsonashton_helpertracker;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.watsonashton_helpertracker.fragments.LogInFragment;

public class HomeScreenFragment extends Fragment {
    HomeScreenListener mListener;


    public interface  HomeScreenListener{

        void SignalButtonPushed();

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
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    ImageView d = requireView().findViewById(R.id.imageViewStartSignal);
    d.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.SignalButtonPushed();
        }
    });



    }
}
