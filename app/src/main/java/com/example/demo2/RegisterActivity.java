package com.example.demo2;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import com.example.demo2.databinding.ActivityRegisterBinding;
import java.util.Calendar;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding.createtext.setOnClickListener(v -> {
            Intent registeri = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(registeri);
        });


        binding.include.fullname.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(Objects.requireNonNull(binding.include.fullname.getText()).toString().isEmpty()) {
            binding.include.fullname.setError("Enter Name");
            binding.include.fullname.requestFocus();
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        if (!Objects.requireNonNull(binding.include.fullname.getText()).toString().trim().matches("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$")) {
            binding.include.fullname.setError("Only Alphabetical Character Valid");
            binding.include.fullname.requestFocus();
        }
    }
});
        binding.include.phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(Objects.requireNonNull(binding.include.fullname.getText()).toString().isEmpty()) {
                    binding.include.fullname.setError("Enter Name");
                    binding.include.fullname.requestFocus();
                } else if (Objects.requireNonNull(binding.include.phone.getText()).toString().isEmpty()) {
                    binding.include.phone.setError("Enter Contact No.");
                    binding.include.phone.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Objects.requireNonNull(binding.include.phone.getText()).toString().trim().matches("^[0-9]{10}$")) {
                    binding.include.phone.setError("Please Enter valid phone number");
                    binding.include.phone.requestFocus();
                }
            }
        });
        binding.include.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (Objects.requireNonNull(binding.include.phone.getText()).toString().isEmpty()) {
                    binding.include.phone.setError("Enter Contact No.");
                    binding.include.phone.requestFocus();
                } else if (Objects.requireNonNull(binding.include.email.getText()).toString().isEmpty()) {
                    binding.include.email.setError("Enter Email Address");
                    binding.include.email.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Objects.requireNonNull(binding.include.email.getText()).toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    binding.include.email.setError("Please Enter Valid Email Address.");
                    binding.include.email.requestFocus();
                }
            }
        });
        binding.password.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(Objects.requireNonNull(binding.include.email.getText()).toString().isEmpty()) {
            binding.include.email.setError("Enter Email Address");
            binding.include.email.requestFocus();
        } else if (Objects.requireNonNull(binding.password.getText()).toString().isEmpty()) {
            binding.password.setError("Enter Password");
            binding.password.requestFocus();
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(!Objects.requireNonNull(binding.password.getText()).toString().matches(".*[@#!$%^&+=].*")) {
            binding.password.setError("Enter atleast one special character");
            binding.password.requestFocus();
        } else if (binding.password.getText().toString().length() < 8 ) {
            binding.password.setError("Minimum 8 character Required ");
            binding.password.requestFocus();
        }
    }
});
        binding.confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(Objects.requireNonNull(binding.password.getText()).toString().isEmpty()) {
                    binding.password.setError("Please Enter Password");
                    binding.password.requestFocus();
                } else if (Objects.requireNonNull(binding.confirmPassword.getText()).toString().isEmpty()) {
                    binding.confirmPassword.setError("Please Enter Confirm Password");
                    binding.confirmPassword.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!binding.confirmPassword.equals(binding.password)) {
                    binding.confirmPassword.setError("Password Does Not Match");
                    binding.confirmPassword.requestFocus();
                } else {
                    binding.confirmPassword.setError("");
                    binding.confirmPassword.clearFocus();
                }
            }
        });
        int selectGenderId = binding.radiogender.getCheckedRadioButtonId();
        binding.showdate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(selectGenderId <= 0) {
                   // Toast.makeText(RegisterActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
                } else if (binding.showdate.getText().toString().isEmpty()) {
                    binding.showdate.setError("Please Enter Birthdate");
                    binding.showdate.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.registerBtn.setOnClickListener(v -> {
            String fname = Objects.requireNonNull(binding.include.fullname.getText()).toString().trim();
            String mobile = Objects.requireNonNull(binding.include.phone.getText()).toString().trim();
            String email = Objects.requireNonNull(binding.include.email.getText()).toString().trim();
            String pass = Objects.requireNonNull(binding.password.getText()).toString().trim();
            String confirmPass = Objects.requireNonNull(binding.confirmPassword.getText()).toString().trim();
            String showDate = binding.showdate.getText().toString().trim();

            boolean check = validateInfo(fname,mobile,email,pass,confirmPass,showDate);
            if(!binding.check1.isChecked() && !binding.check2.isChecked() && !binding.check3.isChecked()) {
                Toast.makeText(RegisterActivity.this, "Please Select Hobby", Toast.LENGTH_SHORT).show();
            } else  {
                if(check == true){
                        Toast.makeText(RegisterActivity.this, "Successful Registration", Toast.LENGTH_SHORT).show();
                        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Password", pass);
                        editor.putString("Username", email);
                        editor.commit();
                        RegisterActivity.this.finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Required All field", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.pickdate.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                    (view1, year1, month1, dayOfMonth) -> binding.showdate.setText(dayOfMonth + "-" + (month1 + 1) + "-" + year1), year,month,day);
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            datePickerDialog.show();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
    private boolean validateInfo(String fname, String mobile, String email, String pass, String confirmPass, String showDate) {

        if(fname.isEmpty()) {
            binding.include.fullname.requestFocus();
            binding.include.fullname.setError("Enter Name");
            return false;
        } else if (!fname.matches("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$")) {
            binding.include.fullname.requestFocus();
            binding.include.fullname.setError("Please Enter only alphabetical character.");
            return false;
        } else if (mobile.isEmpty()) {
            binding.include.phone.requestFocus();
            binding.include.phone.setError("Enter Contact No.");
            return false;
        } else if (!mobile.matches("^[0-9]{10}$")) {
            binding.include.phone.requestFocus();
            binding.include.phone.setError("Please Enter valid phone number");
            return false;
        } else if (email.isEmpty()) {
            binding.include.email.requestFocus();
            binding.include.email.setError("Enter Email Address");
            return false;
        } else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            binding.include.email.requestFocus();
            binding.include.email.setError("Please Enter Valid Email Address.");
            return false;
        } else if (pass.length() < 8) {
            binding.password.requestFocus();
            binding.password.setError("Minimum 8 character required.");
            return false;
        } else if (!confirmPass.equals(pass)) {
            binding.confirmPassword.requestFocus();
            binding.confirmPassword.setError("Password Doesn't Match");
            return false;
        } else if (showDate.isEmpty()) {
            binding.showdate.requestFocus();
            binding.showdate.setError("Enter Birthdate.");
            return false;
        } else {
            return true;
        }

    }

}