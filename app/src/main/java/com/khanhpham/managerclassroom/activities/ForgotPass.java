package com.khanhpham.managerclassroom.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.khanhpham.managerclassroom.R;

public class ForgotPass extends AppCompatActivity {

    Button btnSubmit;
    ImageView btnBackForgot;
    EditText edtForgotPass;
    ProgressBar progressBar;
    FirebaseAuth auth;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        auth = FirebaseAuth.getInstance();

        edtForgotPass = findViewById(R.id.edtForgotPass);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBackForgot = findViewById(R.id.btnBackForgot);
        progressBar = findViewById(R.id.pbForgotPass);

        // Submit button click
        btnSubmit.setOnClickListener(v -> {
            email = edtForgotPass.getText().toString().trim();
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtForgotPass.setError(getString(R.string.email_invalid));
                Toast.makeText(ForgotPass.this, getString(R.string.email_pre_enter), Toast.LENGTH_SHORT).show();
            } else {
                edtForgotPass.setError(null);
                resetPassWord();
            }
        });

        // Back
        btnBackForgot.setOnClickListener(v -> finish());
    }

    // Reset pass
    private void resetPassWord() {
        btnSubmit.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> showAlertDialog(getString(R.string.forgot_pass_reset)))
                .addOnFailureListener(e -> Toast.makeText(ForgotPass.this, getString(R.string.error), Toast.LENGTH_SHORT).show());
    }

    // Alert dialog
    private void showAlertDialog(String message) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cancel_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtDescDialog = dialog.findViewById(R.id.txtDescDialog);
        Button btnDialog = dialog.findViewById(R.id.btnDialog);
        Button btnDone = dialog.findViewById(R.id.btnDone);

        txtDescDialog.setText(message);

        // Open email app
        btnDialog.setOnClickListener(v -> {
            Intent intent1 = new Intent(Intent.ACTION_MAIN);
            intent1.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            finish();
        });

        // Done and finish
        btnDone.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent2 = new Intent(ForgotPass.this, LoginActivity.class);
            startActivity(intent2);
            finish();
        });

        dialog.show();
    }
}