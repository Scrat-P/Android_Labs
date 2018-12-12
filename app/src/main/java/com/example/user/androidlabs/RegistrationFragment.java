package com.example.user.androidlabs;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

public class RegistrationFragment extends Fragment {

    public RegistrationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final EditText emailField = view.findViewById(R.id.emailField);
        final EditText passwordField = view.findViewById(R.id.passwordField);
        final EditText passwordConfirmationField = view.findViewById(R.id.passwordConfirmationField);
        final Button registerButton = view.findViewById(R.id.registerButton);
        final Button backToLoginButton = view.findViewById(R.id.backToLoginButton);

        backToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                disableButtons(registerButton, backToLoginButton);

                final String email = emailField.getText().toString().trim();
                final String password = passwordField.getText().toString().trim();
                final String passwordConfirmation = passwordConfirmationField.getText().toString().trim();

                if (!email.isEmpty() && password.equals(passwordConfirmation)) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(getActivity(), MainActivity.class));
                                    } else {
                                        enableButtons(registerButton, backToLoginButton);
                                        Log.d("Registration", task.getException().getMessage());
                                        Toast.makeText(getContext(), R.string.auth_error_message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    enableButtons(registerButton, backToLoginButton);
                    Toast.makeText(getContext(), R.string.auth_error_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void disableButtons(Button registerButton, Button loginButton) {
        registerButton.setEnabled(false);
        loginButton.setEnabled(false);
    }

    private void enableButtons(Button registerButton, Button loginButton) {
        registerButton.setEnabled(true);
        loginButton.setEnabled(true);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
