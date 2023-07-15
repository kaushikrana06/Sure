package com.suretrust.farmerconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerifyOTPActivity extends AppCompatActivity {

    // variable for FirebaseAuth class
    private FirebaseAuth mAuth;

    // variable for our text input field for OTP
    private EditText edtOTP;
    private TextView tv_mobno;

    // button for verifying OTP
    private Button verifyOTPBtn;

    // string for storing the verification ID
    private String verificationId;
    private String mob_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_verify_otp);
//        Window window = this.getWindow();
//        window.setStatusBarColor(this.getResources ().getColor(R.color.white));
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        // initializing instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // initializing variables for EditText and Button
        edtOTP = findViewById(R.id.idEdtOtp);
        verifyOTPBtn = findViewById(R.id.idBtnVerify);
        tv_mobno = findViewById(R.id.tv_mobno);

        // get the verification ID passed from GenerateOTPActivity
        verificationId = getIntent().getStringExtra("verificationId");
        mob_no=getIntent().getStringExtra("mobno");
        tv_mobno.setText(mob_no);
        // setting onClick listener for verify OTP button
        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if the user has entered the OTP
                if (TextUtils.isEmpty(edtOTP.getText().toString())) {
                    // display a toast message if OTP is empty
                    Toast.makeText(VerifyOTPActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    // verify the entered OTP
                    verifyCode(edtOTP.getText().toString());
                }
            }
        });
    }

    private void verifyCode(String code) {
        // get the credentials using verification ID and entered OTP
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // sign in with the obtained credentials
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        // check if the entered OTP is correct
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the OTP is correct and the task is successful,
                            // send the user to the MainActivity
                            Intent i = new Intent(VerifyOTPActivity.this, OTPVerifiedActivity.class);
                            i.putExtra("mob_no", mob_no);
                            startActivity(i);
                            finish();
                        } else {
                            // if the OTP is not correct, display an error message
                            Toast.makeText(VerifyOTPActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
