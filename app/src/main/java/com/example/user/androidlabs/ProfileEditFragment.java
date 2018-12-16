package com.example.user.androidlabs;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.app.Activity.RESULT_OK;

public class ProfileEditFragment extends Fragment {

    private static final int RC_PICK_IMAGE_REQUEST = 1234;
    private Boolean isPhotoChanged = false;
    private Uri selectedAvatarUri;
    private ImageView profileImage;
    private EditText emailField;
    private EditText phoneField;
    private EditText fullNameField;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        profileImage = view.findViewById(R.id.profileEditImageView);
        profileImage.setOnClickListener(profileImageListener);

        emailField = view.findViewById(R.id.profileEditEmailField);
        phoneField = view.findViewById(R.id.profileEditPhoneField);
        fullNameField = view.findViewById(R.id.profileEditFullNameField);

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        emailField.setText(userEmail);

        Button saveButton = view.findViewById(R.id.profileEditSaveButton);
        saveButton.setOnClickListener(saveButtonListener);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(user.getUid());
        reference.getBytes(Long.MAX_VALUE)
            .addOnSuccessListener(successImageLoadListener)
            .addOnFailureListener(failureImageLoadListener);

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference()
                .child("userProfiles").child(user.getUid());
        dbReference.addValueEventListener(profileEventListener);
    }

    private View.OnClickListener profileImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_PICK_IMAGE_REQUEST);
        }
    };

    private ValueEventListener profileEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
            if (userProfile != null){
                fullNameField.setText(userProfile.fullName);
                phoneField.setText(userProfile.phoneNumber);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d("ProfileEditInfo", databaseError.getMessage());
            Toast.makeText(getContext(), R.string.profile_edit_error_message, Toast.LENGTH_SHORT).show();
        }
    };

    private OnSuccessListener<byte[]> successImageLoadListener = new OnSuccessListener<byte[]>() {
        @Override
        public void onSuccess(byte[] bytes) {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
            profileImage.setImageBitmap(bmp);
        }
    };

    private OnFailureListener failureImageLoadListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("ProfileEditImage", exception.getMessage());
            Toast.makeText(getContext(), R.string.profile_edit_error_message, Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = emailField.getText().toString().trim();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getContext(),
                                R.string.profile_edit_error_message,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            String fullName = fullNameField.getText().toString().trim();
            String phoneNumber = phoneField.getText().toString().trim();
            DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference()
                    .child("userProfiles").child(user.getUid());

            UserProfile profile = new UserProfile(fullName, phoneNumber);
            dbReference.setValue(profile);

            if(isPhotoChanged){
                StorageReference reference = FirebaseStorage.getInstance().getReference().child(user.getUid());
                reference.putFile(selectedAvatarUri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),
                                R.string.profile_edit_error_message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            Navigation.findNavController(v).navigate(R.id.profileFragment);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            isPhotoChanged = true;
            selectedAvatarUri = data.getData();
            profileImage.setImageURI(selectedAvatarUri);
        }
    }
}
