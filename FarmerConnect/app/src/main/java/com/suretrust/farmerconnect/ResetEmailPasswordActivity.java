package com.suretrust.farmerconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetEmailPasswordActivity extends AppCompatActivity {
    FirebaseAuth fauth;
    EditText enterEmailToResetPassword;
    Button sendResetLink;
    TextView gotologinFromResetPassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_email_password);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fauth = FirebaseAuth.getInstance();
        enterEmailToResetPassword = findViewById(R.id.enterResetEmail);
        sendResetLink = findViewById(R.id.sendResetLink);
        gotologinFromResetPassword = findViewById(R.id.txtViewGoToLoginFromResetEmailPassword);

        sendResetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resetEmail = enterEmailToResetPassword.getText().toString().trim();

                fauth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(ResetEmailPasswordActivity.this, "Check Email to Reset Password.", Toast.LENGTH_SHORT).show();
                            Intent i5 = new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(i5);
                            finish();
                        } else {
                            Toast.makeText(ResetEmailPasswordActivity.this, "Error: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        String test1 = "Login to your Account";
        SpannableString ss6 = new SpannableString(test1);
        ClickableSpan cs6 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent i3 = new Intent(ResetEmailPasswordActivity.this,LoginActivity.class);
                startActivity(i3);
                finish();
            }
        };
        ss6.setSpan(cs6,0,20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        gotologinFromResetPassword.setText(ss6);
        gotologinFromResetPassword.setMovementMethod(LinkMovementMethod.getInstance());



    }


}
