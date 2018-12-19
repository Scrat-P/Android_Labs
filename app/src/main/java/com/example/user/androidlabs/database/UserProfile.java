package com.example.user.androidlabs.database;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserProfile {
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public UserProfile() {}

    public UserProfile(String firstName, String lastName, String phoneNumber){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}