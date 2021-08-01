package com.example.watsonashton_helpertracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.watsonashton_helpertracker.fragments.AddNewContactFragment;
import com.example.watsonashton_helpertracker.objects.Contacts;
import com.example.watsonashton_helpertracker.objects.User;

public class ProfileFragment extends Fragment {

    private static final String ARG_PROFILE = "ARG_PROFILE";

    public static ProfileFragment newInstance(User user){
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        args.putSerializable(ARG_PROFILE, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile_layout,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = (User) requireArguments().getSerializable(ARG_PROFILE);
        TextView firstName = requireView().findViewById(R.id.textViewFirstPlaceHolder);
        TextView lastName = requireView().findViewById(R.id.textViewLastPlaceHolder);
        TextView height = requireView().findViewById(R.id.textViewHeightPlaceHolder);
        TextView weight = requireView().findViewById(R.id.textViewWeightPlaceHolder);
        TextView hairColor = requireView().findViewById(R.id.textViewHairPlaceHolder);
        TextView eyeColor = requireView().findViewById(R.id.textViewEyePlaceHolder);

        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        height.setText(user.getHeight());
        weight.setText(user.getWeight());
        hairColor.setText(user.getHair());
        eyeColor.setText(user.getEyes());

    }
}
