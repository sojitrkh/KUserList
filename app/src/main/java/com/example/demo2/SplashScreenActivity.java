package com.example.demo2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        new Handler().postDelayed(() -> {

            SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
            Boolean check = sharedPreferences.getBoolean("flag", false);
            Intent splashi;
            if(check)
            {
                splashi = new Intent(SplashScreenActivity.this, DashBoardActivity.class);
                startActivity(splashi);
            }
            else
            {
                splashi = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(splashi);
            }


        }, 1000);
    }
}