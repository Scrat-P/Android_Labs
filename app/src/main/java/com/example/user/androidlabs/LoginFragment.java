package com.example.user.androidlabs;


import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        final EditText emailField = view.findViewById(R.id.emailField);
        final EditText passwordField = view.findViewById(R.id.passwordField);
        final Button createNewUserButton = view.findViewById(R.id.createNewUserButton);
        final Button loginButton = view.findViewById(R.id.loginButton);

        createNewUserButton.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registrationFragment)
        );
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                disableButtons(createNewUserButton, loginButton);

                final String email = emailField.getText().toString().trim();
                final String password = passwordField.getText().toString().trim();



                if (!email.isEmpty() && !password.isEmpty()) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(getActivity(), MainActivity.class));
                                    } else {
                                        enableButtons(createNewUserButton, loginButton);
                                        Log.d("Login", task.getException().getMessage());
                                        Toast.makeText(getContext(), R.string.auth_error_message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    enableButtons(createNewUserButton, loginButton);
                    Toast.makeText(getContext(), R.string.auth_error_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void disableButtons(Button createNewUserButton, Button loginButton) {
        createNewUserButton.setEnabled(false);
        loginButton.setEnabled(false);
    }

    private void enableButtons(Button createNewUserButton, Button loginButton) {
        createNewUserButton.setEnabled(true);
        loginButton.setEnabled(true);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
