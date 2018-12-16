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
    private EditText emailField;
    private EditText passwordField;
    private EditText passwordConfirmationField;
    private Button registerButton;
    private Button backToLoginButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        emailField = view.findViewById(R.id.emailField);
        passwordField = view.findViewById(R.id.passwordField);
        passwordConfirmationField = view.findViewById(R.id.passwordConfirmationField);
        registerButton = view.findViewById(R.id.registerButton);
        backToLoginButton = view.findViewById(R.id.backToLoginButton);

        backToLoginButton.setOnClickListener(backToLoginButtonListener);
        registerButton.setOnClickListener(registerButtonListener);
    }

    private View.OnClickListener backToLoginButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            Navigation.findNavController(v).popBackStack();
        }
    };

    private View.OnClickListener registerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            disableButtons(registerButton, backToLoginButton);

            final String email = emailField.getText().toString().trim();
            final String password = passwordField.getText().toString().trim();
            final String passwordConfirmation = passwordConfirmationField.getText().toString().trim();

            if (!email.isEmpty() && password.equals(passwordConfirmation)) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(completeRegisterListener);
            } else {
                enableButtons(registerButton, backToLoginButton);
                Toast.makeText(getContext(), R.string.auth_error_message, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private OnCompleteListener<AuthResult> completeRegisterListener = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            enableButtons(registerButton, backToLoginButton);
            if (task.isSuccessful()) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            } else {
                Log.d("Registration", task.getException().getMessage());
                Toast.makeText(getContext(), R.string.auth_error_message, Toast.LENGTH_SHORT).show();
            }
        }
    };

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
