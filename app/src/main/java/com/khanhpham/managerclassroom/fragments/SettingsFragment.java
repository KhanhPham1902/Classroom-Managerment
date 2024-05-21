package com.khanhpham.managerclassroom.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.khanhpham.managerclassroom.R;

import java.util.Locale;
import java.util.Objects;

public class SettingsFragment extends Fragment {

    SwitchCompat swDarkMode;
    ImageView btnVN, btnUK, btnBackSettings;
    boolean darkMode;
    String langCode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        swDarkMode = view.findViewById(R.id.swDarkMode);
        btnVN = view.findViewById(R.id.btnVN);
        btnUK = view.findViewById(R.id.btnUK);
        btnBackSettings = view.findViewById(R.id.btnBackSettings);

        // get from SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        darkMode = sharedPreferences.getBoolean("dark",false);
        langCode = sharedPreferences.getString("lang","");

        if(darkMode){
            swDarkMode.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // switch to dark mode
        swDarkMode.setOnClickListener(v -> {
            if(darkMode){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor = sharedPreferences.edit();
                editor.putBoolean("dark",false);
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor = sharedPreferences.edit();
                editor.putBoolean("dark",true);
            }
            editor.apply();
        });

        // set vietnamese
        btnVN.setOnClickListener(v -> {
            setLanguage("vn");
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // set english
        btnUK.setOnClickListener(v -> {
            setLanguage("en");
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // back
        btnBackSettings.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
    public void setLanguage(String langCode) {
        // Set language
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        // Save language preference to SharedPreferences
        editor = sharedPreferences.edit();
        editor.putString("lang", langCode);
        editor.apply();
    }
}