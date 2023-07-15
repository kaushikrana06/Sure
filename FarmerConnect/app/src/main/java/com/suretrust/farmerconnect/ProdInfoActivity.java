package com.suretrust.farmerconnect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProdInfoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView qualitytv, quantitytv, prod_datetv, prod_nametv, prod_infotv, harv_practv;
    private Button farmers_btn;
    private FirebaseAuth mAuth;DatabaseReference dbref;DrawerLayout drawerLayout;
    FirebaseFirestore st;
    TextView tx,pro;
    String uid;


    @Override @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_prod_info);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



       Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        NavigationView navigationView=findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener( this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        View hd= navigationView.getHeaderView(0);
        toggle.syncState();

        mAuth = FirebaseAuth.getInstance();


        pro=hd.findViewById(R.id.protxt);
        st= FirebaseFirestore.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        DocumentReference dr=st.collection("users").document(uid);
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                pro.setText(documentSnapshot.getString("name"));
            }
        });

        qualitytv = findViewById(R.id.quality);
        quantitytv = findViewById(R.id.quantity);
        prod_datetv = findViewById(R.id.prod_date);
        prod_nametv = findViewById(R.id.prod_name);
        prod_infotv = findViewById(R.id.prod_info);
        harv_practv = findViewById(R.id.harv_prac);
        farmers_btn = findViewById(R.id.farmers_btn);

        // Get the scanned data from the intent
        String qrID = getIntent().getStringExtra("scanned_info");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("qrcode").document(qrID);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Extract field values from the document
                    String quality = document.getString("quality");
                    String quantity = document.getString("quantity");
                    String prodDate = document.getString("production_date");
                    String prodName = document.getString("prod_name");
                    String prodInfo = document.getString("prod_info");
                    String harvPrac = document.getString("harvesting_practices");

                    // Assign values to TextViews
                    qualitytv.setText(quality);
                    quantitytv.setText(quantity);
                    prod_datetv.setText(prodDate);
                    prod_nametv.setText(prodName);
                    prod_infotv.setText(prodInfo);
                    harv_practv.setText(harvPrac);
                } else {
                    // Document doesn't exist
                }
            } else {
                // Error accessing Firestore
            }
        });

        farmers_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProdInfoActivity.this, FarmerInfoActivity.class);
                intent.putExtra("qrID", qrID);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Call finish to close the InfoActivity and go back to the previous fragment
        finish();
    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){

            case R.id.settings:
                Intent intent = new Intent(ProdInfoActivity.this, Settings.class);
                startActivity(intent);
                break;
            case R.id.donationhis:
                Intent intent1 = new Intent(ProdInfoActivity.this, Donation.class);
                startActivity(intent1);
                break;
            case R.id.help:
                Intent intent2 = new Intent(ProdInfoActivity.this, Help.class);
                startActivity(intent2);
                break;
            case R.id.info:
                Intent intent4 = new Intent(ProdInfoActivity.this, Information.class);
                startActivity(intent4);
                break;
            case R.id.logout:
                mAuth.signOut();
                // Sign out the user from Google Sign-In (if used)
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignIn.getClient(this, gso).signOut();

                // Redirect the user to the login screen or perform any other required actions
                // For example, you can start a new activity or show a toast message
                if (this != null) {
                    Intent intent5 = new Intent(this, LoginActivity.class);
                    intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent5);
                    this.finish(); // Optional: Finish the current activity to prevent going back
                }
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
