package com.suretrust.farmerconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.SparseLongArray;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.suretrust.farmerconnect.LoginActivity;
import com.suretrust.farmerconnect.MainActivity;
import com.suretrust.farmerconnect.WalkthroughActivity;

public class SplashScreen extends AppCompatActivity {
    private static final long SPLASH_TIMEOUT = 1000; // 1 second
    private static final String PREF_FIRST_TIME = "first_time";

    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);
//        Window window = this.getWindow();
//        window.setStatusBarColor(this.getResources().getColor(R.color.white));

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
        boolean isFirstTime = prefs.getBoolean(PREF_FIRST_TIME, true);

        // Using a Handler to delay the transition to the next activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if the user is already logged in
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // User is logged in, navigate to MainActivity
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                }
                else if (isFirstTime) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(PREF_FIRST_TIME, false);
                    editor.apply();

                    Intent intent = new Intent(SplashScreen.this, WalkthroughActivity.class);
                    startActivity(intent);
                }
                else{
                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(intent);
                    }

                // Close the splash screen activity
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
