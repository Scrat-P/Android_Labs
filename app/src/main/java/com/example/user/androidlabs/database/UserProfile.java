package com.example.user.androidlabs.database;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserProfile {
    private String image;
    private String fullName;
    private String phoneNumber;

    public UserProfile(){}

    public UserProfile(String fullName, String phoneNumber, String image){
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}