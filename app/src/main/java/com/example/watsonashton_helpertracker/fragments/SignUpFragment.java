package com.example.watsonashton_helpertracker.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.watsonashton_helpertracker.R;

public class SignUpFragment extends Fragment {

    SignUpListener mListener;
    Spinner heightSpinner;
    Spinner hairSpinner;
    Spinner eyeSpinner;
    String height;
    String hairColor;
    String eyeColor;
    TextView firstNameTextView;
    TextView lastNameTextView;
    TextView emailTextView;
    TextView passwordTextView;
    Button sigUpButton;

    public interface  SignUpListener{
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
        return inflater.inflate(R.layout.fragment_sign_up_new_layout,container, false);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        height = "4'8\"";
        hairColor = "Red";
        eyeColor = "Brown";
        heightSpinner = requireView().findViewById(R.id.spinnerHeight);
        hairSpinner = requireView().findViewById(R.id.spinnerHair);
        eyeSpinner = requireView().findViewById(R.id.spinnerEye);
        firstNameTextView = requireView().findViewById(R.id.editTextSignUpFirstName);
        lastNameTextView = requireView().findViewById(R.id.editTextSignUpLastName);
        emailTextView = requireView().findViewById(R.id.editTextSignUpEmail);
        passwordTextView = requireView().findViewById(R.id.editTextLogInPassword);
        sigUpButton = requireView().findViewById(R.id.buttonCreateAccount);
        TextView textNewUser = requireView().findViewById(R.id.textViewAlreadyAnUser);


        textNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.SignUpAlreadyAnUserClicked();
            }
        });

        heightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               ((TextView) parent.getChildAt( 0)).setTextColor(Color.BLACK);
                if(heightSpinner.getSelectedItem().toString().equals("4'8\"")){
                    height = "4'8\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("4'9\"")){
                    height = "4'9\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("5'0")){
                    height = "5'0";
                }else if(heightSpinner.getSelectedItem().toString().equals("5'1\"")){
                    height = "5'1\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("5'2\"")){
                    height = "5'2\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("5'3\"")){
                    height = "5'3\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("5'4\"")){
                    height = "5'4\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("5'5\"")){
                    height = "5'5\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("5'6\"")){
                    height = "5'6\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("5'7\"")){
                    height = "5'7\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("5'8\"")){
                    height = "5'8\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("5'9\"")){
                    height = "5'9\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("5'10\"")){
                    height = "5'10\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("5'11\"")){
                    height = "5'11\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("6'0")){
                    height = "6'0";
                }else if(heightSpinner.getSelectedItem().toString().equals("6'1\"")){
                    height = "6'1\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("6'2\"")){
                    height = "6'2\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("6'3\"")){
                    height = "6'3\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("6'4\"")){
                    height = "6'4\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("6'5\"")){
                    height = "6'5\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("6'6\"")){
                    height = "6'6\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("6'7\"")){
                    height = "6'7\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("6'8\"")){
                    height = "6'8\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("6'9\"")){
                    height = "6'9\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("6'10\"")){
                    height = "6'10\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("6'11\"")){
                    height = "6'11\"";
                }else if(heightSpinner.getSelectedItem().toString().equals("7'0")){
                    height = "7'0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        hairSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt( 0)).setTextColor(Color.BLACK);
                if(hairSpinner.getSelectedItem().toString().equals("Red")){
                    hairColor = "Red";
                } else if(hairSpinner.getSelectedItem().toString().equals("Blonde")){
                    hairColor = "Blonde";
                }else if(hairSpinner.getSelectedItem().toString().equals("Brunette")){
                    hairColor = "Brunette";
                }else if(hairSpinner.getSelectedItem().toString().equals("Black")){
                    hairColor = "Black";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        eyeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt( 0)).setTextColor(Color.BLACK);

                if(eyeSpinner.getSelectedItem().toString().equals("Brown")){
                    hairColor = "Brown";
                } else if(eyeSpinner.getSelectedItem().toString().equals("Blue")){
                    hairColor = "Blue";
                }else if(eyeSpinner.getSelectedItem().toString().equals("Hazel")){
                    hairColor = "Hazel";
                }else if(eyeSpinner.getSelectedItem().toString().equals("Amber")){
                    hairColor = "Amber";
                }else if(eyeSpinner.getSelectedItem().toString().equals("Gray")){
                    hairColor = "Gray";
                }else if(eyeSpinner.getSelectedItem().toString().equals("Green")){
                    hairColor = "Green";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CreateSpinners();
    }

    public void CreateSpinners(){

        String [] sortByArrayHeight = getResources().getStringArray(R.array.heightArray);
        ArrayAdapter<String> spinnerHeightAdaptor = new ArrayAdapter<>(
                getActivity().getBaseContext(),
                android.R.layout.simple_list_item_1,
                sortByArrayHeight

        );
        String [] sortByArrayHair = getResources().getStringArray(R.array.hairColorArray);
        ArrayAdapter<String> spinnerHairAdaptor = new ArrayAdapter<>(
                getActivity().getBaseContext(),
                android.R.layout.simple_list_item_1,
               sortByArrayHair

        );

        String [] sortByArrayEye = getResources().getStringArray(R.array.eyeColorArray);
        ArrayAdapter<String> spinnerEyeAdaptor = new ArrayAdapter<>(
                getActivity().getBaseContext(),
                android.R.layout.simple_list_item_1,
                sortByArrayEye

        );

        heightSpinner.setAdapter(spinnerHeightAdaptor);
        hairSpinner.setAdapter(spinnerHairAdaptor);
        eyeSpinner.setAdapter(spinnerEyeAdaptor);
    }
}
