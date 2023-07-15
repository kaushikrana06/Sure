package com.suretrust.farmerconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OTPVerifiedActivity extends AppCompatActivity {

    EditText edt;
    Button btn;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverified);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        edt = findViewById(R.id.edt);
        btn = findViewById(R.id.btn);
        db = FirebaseFirestore.getInstance();

        String mobileNumber = getIntent().getStringExtra("mob_no");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edt.getText().toString();

                // Create a map to store the user's data
                Map<String, Object> userData = new HashMap<>();
                userData.put("name", name);
                userData.put("id", mobileNumber);

                // Save user data in Firestore using UID as document ID
                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .set(userData)
                        .addOnSuccessListener(task -> {
                            // Data saved successfully
                            // You can add any additional actions or notifications here
                            startMainActivity();
                        })
                        .addOnFailureListener(e -> {
                            // Failed to save data
                            // Handle the error accordingly
                        });
            }
        });
    }

    private void startMainActivity() {
        Intent i = new Intent(OTPVerifiedActivity.this, MainActivity.class);
        startActivity(i);
    }
}
