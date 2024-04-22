package com.khanhpham.managerclassroom.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.khanhpham.managerclassroom.R;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    FirebaseDatabase database;
    FirebaseAuth auth;
    LinearLayout signupLayout;
    EditText signUpEmail, signUpPassword, signUpConfirm, signUpName;
    TextView txtLoginRedirect;
    Button signUpButton;
    ImageButton btnHidePassword, btnHideConfirmPass;
    ProgressBar pbSignup;
    private String email, pass, confirmPass, name, id;
    private int count1 = 0;
    private int count2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        signupLayout = findViewById(R.id.signupLayout);
        signUpButton = findViewById(R.id.signupButton);
        btnHidePassword = findViewById(R.id.btnHidePass);
        btnHideConfirmPass = findViewById(R.id.btnHideConfirmPass);
        signUpEmail = findViewById(R.id.signupEmail);
        signUpPassword = findViewById(R.id.signupPassword);
        signUpName = findViewById(R.id.signupName);
        txtLoginRedirect = findViewById(R.id.txtLoginRedirect);
        signUpConfirm = findViewById(R.id.signupConfirmPass);
        pbSignup = findViewById(R.id.pbSignUp);

        //Show or hide password
        btnHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count1++;
                String password = signUpPassword.getText().toString();
                if (count1 % 2 == 1) { //Show password
                    if (!password.isEmpty()) {
                        signUpPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        btnHidePassword.setImageResource(R.drawable.visibility_off);
                    }
                } else { //Hide password
                    if (!password.isEmpty()) {
                        signUpPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        btnHidePassword.setImageResource(R.drawable.visibility);
                    }
                }
            }
        });

        //Show or hide confirm password
        btnHideConfirmPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count2++;
                String confpass = signUpConfirm.getText().toString();
                if (count2 % 2 == 1) { //Show password
                    if (!confpass.isEmpty()) {
                        signUpConfirm.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        btnHideConfirmPass.setImageResource(R.drawable.visibility_off);
                    }
                } else { //Hide password
                    if (!confpass.isEmpty()) {
                        signUpConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        btnHideConfirmPass.setImageResource(R.drawable.visibility);
                    }
                }
            }
        });

        //Signup button click
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpButton.setVisibility(View.GONE);
                pbSignup.setVisibility(View.VISIBLE);

                confirmPass = signUpConfirm.getText().toString();
                email = signUpEmail.getText().toString();
                pass = signUpPassword.getText().toString();
                name = signUpName.getText().toString();

                if (name.isEmpty()) {
                    signUpEmail.setError(getString(R.string.name_empty));
                    Toast.makeText(SignupActivity.this, getString(R.string.name_empty), Toast.LENGTH_SHORT).show();
                    signUpButton.setVisibility(View.VISIBLE);
                    pbSignup.setVisibility(View.GONE);
                } else if (email.isEmpty()) {
                    signUpEmail.setError(getString(R.string.email_empty));
                    Toast.makeText(SignupActivity.this, getString(R.string.email_empty), Toast.LENGTH_SHORT).show();
                    signUpButton.setVisibility(View.VISIBLE);
                    pbSignup.setVisibility(View.GONE);
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    signUpEmail.setError(getString(R.string.email_format_error));
                    Toast.makeText(SignupActivity.this, getString(R.string.email_format_error), Toast.LENGTH_SHORT).show();
                    signUpButton.setVisibility(View.VISIBLE);
                    pbSignup.setVisibility(View.GONE);
                } else if (pass.isEmpty()) {
                    signUpPassword.setError(getString(R.string.pass_empty));
                    Toast.makeText(SignupActivity.this, getString(R.string.pass_empty), Toast.LENGTH_SHORT).show();
                    signUpButton.setVisibility(View.VISIBLE);
                    pbSignup.setVisibility(View.GONE);
                } else if (pass.length() < 6) {
                    signUpPassword.setError(getString(R.string.pass_lenght));
                    Toast.makeText(SignupActivity.this, getString(R.string.pass_lenght), Toast.LENGTH_SHORT).show();
                    signUpButton.setVisibility(View.VISIBLE);
                    pbSignup.setVisibility(View.GONE);
                } else if (confirmPass.isEmpty()) {
                    signUpConfirm.setError(getString(R.string.confpass_empty));
                    Toast.makeText(SignupActivity.this, getString(R.string.confpass_empty), Toast.LENGTH_SHORT).show();
                    signUpButton.setVisibility(View.VISIBLE);
                    pbSignup.setVisibility(View.GONE);
                } else {
                    if (!confirmPass.equals(pass)) {
                        signUpConfirm.setError(getString(R.string.pass_match));
                        Toast.makeText(SignupActivity.this, getString(R.string.pass_match), Toast.LENGTH_SHORT).show();
                        signUpButton.setVisibility(View.VISIBLE);
                        pbSignup.setVisibility(View.GONE);
                    } else {
                        auth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        createUserDatabase();
                                        Toast.makeText(SignupActivity.this, getString(R.string.signup_success), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Create user account failed
                                        showAlertDialog(getString(R.string.warning), getString(R.string.signup_failed));
                                        signUpButton.setVisibility(View.VISIBLE);
                                        pbSignup.setVisibility(View.GONE);
                                    }
                                });
                    }
                }
            }
        });

        //Login redirect click
        txtLoginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        //Hide keyboard when touch main view
        signupLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    // Create user database
    private void createUserDatabase() {
        FirebaseUser user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        if (user != null) {
            HashMap<String, Object> map = new HashMap<>();
            id = user.getUid();
            email = user.getEmail();
            map.put("id", id);
            map.put("username", name);
            map.put("email", email);
            database.getReference().child("users").child(id).child("profile").setValue(map);
        }
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Hide keyboard
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}