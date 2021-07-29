package com.example.watsonashton_helpertracker.objects;

import java.io.Serializable;

public class Contacts implements Serializable {

    private final String name;
    private final String phoneNum;

    public Contacts(String name, String phoneNum){
        this.name = name;
        this.phoneNum = phoneNum;
    }

    public String getFullName() {
        return name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

}
