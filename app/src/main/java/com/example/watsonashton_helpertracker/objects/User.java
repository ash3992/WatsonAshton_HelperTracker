package com.example.watsonashton_helpertracker.objects;

import java.io.Serializable;

public class User implements Serializable {

    private final String eyes;
    private final String firstName;
    private final String hair;
    private final String height;
    private final String lastName;
    private final String weight;

    public User( String eyes, String firstName, String hair, String height, String lastName,  String weight) {

        this.eyes = eyes;
        this.firstName = firstName;
        this.hair = hair;
        this.height = height;
        this.lastName = lastName;
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }


    public String getEyes() {
        return eyes;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getHair() {
        return hair;
    }

    public String getLastName() {
        return lastName;
    }


    public String getWeight() {
        return weight;
    }
}
