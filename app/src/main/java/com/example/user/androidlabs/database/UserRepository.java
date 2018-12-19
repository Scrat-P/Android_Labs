package com.example.user.androidlabs.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UserRepository {
    private FirebaseAuth authInstance;
    private FirebaseUser user;

    private StorageReference imageReference;
    private DatabaseReference profileReference;

    public UserRepository() {
        authInstance = FirebaseAuth.getInstance();
        user = authInstance.getCurrentUser();
        if (user != null) {
            imageReference = FirebaseStorage.getInstance().getReference().child(user.getUid());
            profileReference = FirebaseDatabase.getInstance().getReference()
                    .child("userProfiles").child(user.getUid());
        }
    }

    public String getEmail(){
        return user.getEmail();
    }

    public Task<AuthResult> signIn(String email, String password){
        return authInstance.signInWithEmailAndPassword(email, password);
    }

    public void signOut(){
        authInstance.signOut();
    }

    public Task<AuthResult> createNewUser(String email, String password){
        return authInstance.createUserWithEmailAndPassword(email, password);
    }

    public Task<Void> updateEmail(String email){
        return user.updateEmail(email);
    }

    public Task<byte[]> getProfileImageBitmap(){
        return imageReference.getBytes(Long.MAX_VALUE);
    }

    public UploadTask putProfileImageBitmap(byte[] image){
        return imageReference.putBytes(image);
    }

    public void setUserProfile(UserProfile profile){
        profileReference.setValue(profile);
    }

    public void addProfileEventListener(ValueEventListener profileEventListener){
        profileReference.addValueEventListener(profileEventListener);
    }
}