package com.example.user.androidlabs;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserProfile {
    public String fullName;
    public String phoneNumber;

    public UserProfile(){}

    public UserProfile(String fullName, String phoneNumber){
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }
}