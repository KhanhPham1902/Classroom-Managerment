package com.khanhpham.managerclassroom.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.khanhpham.managerclassroom.R;
import com.khanhpham.managerclassroom.databinding.ActivitySplashBinding;
import com.khanhpham.managerclassroom.methods.InternetCheck;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding splashBinding;
    Animation topAnim, bottomAnim;
    final private int TIME = 3000;
    boolean darkMode;
    String langCode;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get data from sharedPreferences
        sharedPreferences = getSharedPreferences("SETTINGS",MODE_PRIVATE);
        darkMode = sharedPreferences.getBoolean("dark", false);
        langCode = sharedPreferences.getString("lang","");

        // Set dark/light mode
        if(darkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Set language
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        splashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(splashBinding.getRoot());

        // Set animation
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        splashBinding.cardviewSplash.setAnimation(topAnim);
        splashBinding.txtSplash.setAnimation(bottomAnim);

        // check internet connection
        boolean isInternetConnected = InternetCheck.isInternetAvailable(getApplicationContext());
        if (isInternetConnected) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, TIME);
        } else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }
}