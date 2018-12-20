package com.example.user.androidlabs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;

public class EmailValidator implements TextWatcher {
    private EditText editText;
    private Context context;

    public EmailValidator(EditText editText, Context context){
        this.context = context;
        this.editText = editText;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String email = editText.getText().toString();
        if (email.isEmpty()){
            editText.setError("Empty email");
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editText.setError("Incorrect email");
        }
        else{
            editText.setError(null);
        }
    }
}
