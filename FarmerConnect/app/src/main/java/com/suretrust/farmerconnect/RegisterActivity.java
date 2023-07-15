package com.suretrust.farmerconnect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    Button registerUser, performLogin;
    FirebaseAuth fAuth;
    TextView txt1;

    FirebaseFirestore firestore;
    EditText name, email, password;
    ProgressBar pgRegister;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        name = findViewById(R.id.entername);
        email = findViewById(R.id.enteremail);
        password = findViewById(R.id.enterpassword);
        performLogin = findViewById(R.id.performLogin);
        pgRegister = findViewById(R.id.progressbarRegister);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        registerUser = findViewById(R.id.registerbutton);
        txt1 = findViewById(R.id.txt1);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getname = name.getText().toString().trim();
                String getemail = email.getText().toString().trim();
                String getpassword = password.getText().toString().trim();
                pgRegister.setVisibility(View.VISIBLE);

                if (getname.equals("") || getemail.equals("") || getpassword.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Please enter complete information", Toast.LENGTH_SHORT).show();
                    pgRegister.setVisibility(View.INVISIBLE);
                } else {
                    fAuth.createUserWithEmailAndPassword(getemail,getpassword)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    String uid = fAuth.getCurrentUser().getUid();

                                    UserData userData = new UserData(getname, getemail);

                                    firestore.collection("users").document(uid)
                                            .set(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    fAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                            finish();
                                                            Toast.makeText(RegisterActivity.this, "User Registered Successfully, Please Check The Verification Link Send To Your Registered Email.", Toast.LENGTH_SHORT).show();
                                                            pgRegister.setVisibility(View.INVISIBLE);
                                                        }
                                                    });

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                                    pgRegister.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    pgRegister.setVisibility(View.INVISIBLE);
                                }
                            });

                }
            }
        });

        performLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        String text = "Already have an account? Login With Us!";
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        };
        spannableString.setSpan(clickableSpan, 25, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt1.setText(spannableString);
        txt1.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
