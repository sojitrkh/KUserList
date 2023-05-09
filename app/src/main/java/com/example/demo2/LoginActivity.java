package com.example.demo2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.demo2.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

private ActivityLoginBinding loginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = loginBinding.getRoot();
        setContentView(view);
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        loginBinding.createtext1.setOnClickListener(v -> {
            Intent forgoti = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(forgoti);
        });

        loginBinding.createtext2.setOnClickListener(v -> {
            Intent noregisteri = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(noregisteri);
        });

       if(preferences.contains("Username")) {
           if(preferences.contains("Password")) {
               Intent logini = new Intent(getApplicationContext(), DashBoardActivity.class);
               startActivity(logini);
           } else {
               Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
           }
       } else
       {
           Snackbar snackbar = Snackbar.make(view,"Please Do Register Your Self", Snackbar.LENGTH_LONG);
           snackbar.show();
       }

        loginBinding.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                 if (Objects.requireNonNull(loginBinding.email.getText()).toString().isEmpty()) {
                    loginBinding.email.setError("Enter Email Address");
                    loginBinding.email.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Objects.requireNonNull(loginBinding.email.getText()).toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    loginBinding.email.setError("Please Enter Valid Email Address.");
                    loginBinding.email.requestFocus();
                }
            }
        });
        loginBinding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(Objects.requireNonNull(loginBinding.email.getText()).toString().isEmpty()) {
                    loginBinding.email.setError("Enter Email Address");
                    loginBinding.email.requestFocus();
                } else if (Objects.requireNonNull(loginBinding.password.getText()).toString().isEmpty()) {
                    loginBinding.password.setError("Enter Password");
                    loginBinding.password.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!Objects.requireNonNull(loginBinding.password.getText()).toString().matches(".*[@#!$%^&+=].*")) {
                    loginBinding.password.setError("Enter atleast one special character");
                    loginBinding.password.requestFocus();
                } else if (loginBinding.password.getText().toString().length() < 8 ) {
                    loginBinding.password.setError("Minimum 8 character Required ");
                    loginBinding.password.requestFocus();
                }
            }
        });


        loginBinding.loginBtn.setOnClickListener(v -> {
                String username = preferences.getString("Username",null);
                String password = preferences.getString("Password",null);
                String mail = Objects.requireNonNull(loginBinding.email.getText()).toString().trim();
                String pass = Objects.requireNonNull(loginBinding.password.getText()).toString().trim();

                if(username != null && mail != null && username.equalsIgnoreCase(mail)) {
                    if(password != null && pass != null && password.equals(pass)) {
                        Toast.makeText(LoginActivity.this, "Successful Login", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Username", username);
                        editor.putString("Password",password);
                        editor.commit();
                        Intent logini = new Intent(getApplicationContext(), DashBoardActivity.class);
                        startActivity(logini);
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Please Enter Registered Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(view,"Please Do Register Your Self", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}