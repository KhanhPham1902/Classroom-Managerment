package com.khanhpham.managerclassroom.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.khanhpham.managerclassroom.R;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    EditText loginEmail, loginPassword;
    Button loginButton;
    TextView signUpRedirect, txtForgetPass;
    ImageButton imgButton;
    LinearLayout loginLayout;
    ProgressBar pbLogin;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        loginLayout = findViewById(R.id.loginLayout);
        loginButton = findViewById(R.id.loginButton);
        loginEmail = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);
        signUpRedirect = findViewById(R.id.txtSignupRedirect);
        imgButton = findViewById(R.id.imgButton);
        txtForgetPass = findViewById(R.id.txtForgetPass);
        pbLogin = findViewById(R.id.pbLogin);

        //Login button click event
        loginButton.setOnClickListener(v -> {
            if (!validateUsername() | !validatePassword()) {
            } else {
                loginButton.setVisibility(View.GONE);
                pbLogin.setVisibility(View.VISIBLE);
                checkUser();
            }
        });

        //signup redirect text click event
        signUpRedirect.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });

        //Show or hide password
        imgButton.setOnClickListener(v -> {
            count++;
            String pass = loginPassword.getText().toString();
            if (count % 2 == 1) { //Show password
                if (!pass.isEmpty()) {
                    loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imgButton.setImageResource(R.drawable.visibility_off);
                }
            } else { //Hide password
                if (!pass.isEmpty()) {
                    loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgButton.setImageResource(R.drawable.visibility);
                }
            }
        });

        //Hide keyboard when touch main view
        loginLayout.setOnTouchListener((v, event) -> {
            hideKeyboard();
            return false;
        });

        // Forgot password text click
        txtForgetPass.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPass.class);
            startActivity(intent);
        });
    }

    //Check validate username
    public boolean validateUsername() {
        String email = loginEmail.getText().toString();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError(getString(R.string.email_invalid));
            Toast.makeText(this, getString(R.string.email_invalid), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            loginEmail.setError(null);
            return true;
        }
    }

    //Check validate password
    public boolean validatePassword() {
        String pass = loginPassword.getText().toString();
        if (pass.isEmpty()) {
            loginPassword.setError(getString(R.string.pass_empty));
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    //Check user information
    public void checkUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user.isEmailVerified()) {
                        Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        user.sendEmailVerification()
                                .addOnSuccessListener(unused -> {
                                    showAlertDialogEmail(getString(R.string.email_verification));
                                    pbLogin.setVisibility(View.GONE);
                                    loginButton.setVisibility(View.VISIBLE);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(LoginActivity.this, getString(R.string.email_send_failed), Toast.LENGTH_SHORT).show();
                                    pbLogin.setVisibility(View.GONE);
                                    loginButton.setVisibility(View.VISIBLE);
                                });
                    }
                }).addOnFailureListener(e -> {
                    try {
                        throw e;
                    } catch (FirebaseAuthInvalidUserException ex) {
                        loginEmail.setError(getString(R.string.user_check));
                        loginEmail.requestFocus();
                        pbLogin.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                    } catch (FirebaseAuthInvalidCredentialsException ex) {
                        loginPassword.setError(getString(R.string.user_info_check));
                        loginPassword.requestFocus();
                        pbLogin.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                    } catch (Exception ex) {
                        Toast.makeText(LoginActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        pbLogin.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                    }
                });
        pbLogin.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);
    }

    private void showAlertDialogEmail(String message) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cancel_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtDescDialog = dialog.findViewById(R.id.txtDescDialog);
        Button btnDialog = dialog.findViewById(R.id.btnDialog);
        Button btnSkip = dialog.findViewById(R.id.btnDone);

        txtDescDialog.setText(message);

        // Open email app
        btnDialog.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        // skip
        btnSkip.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    //Hide keyboard
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    // Check if user already logged in
    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, getText(R.string.please_login), Toast.LENGTH_SHORT).show();
        }
    }
}