package com.khanhpham.managerclassroom.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.khanhpham.managerclassroom.R;
import com.khanhpham.managerclassroom.databinding.ActivityMainBinding;
import com.khanhpham.managerclassroom.fragments.ControlFragment;
import com.khanhpham.managerclassroom.fragments.HomeFragment;
import com.khanhpham.managerclassroom.fragments.ListRoomFragment;
import com.khanhpham.managerclassroom.fragments.NotificationFragment;
import com.khanhpham.managerclassroom.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    String id;
    FirebaseAuth auth;
    boolean darkMode;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null)
            id = user.getUid();

        replaceFragment(new HomeFragment());

        binding.bottomView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navHome) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.navControl) {
                replaceFragment(new ListRoomFragment());
            } else if (item.getItemId() == R.id.navNoti) {
                replaceFragment(new NotificationFragment());
            } else {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        Log.d("ID",id);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}