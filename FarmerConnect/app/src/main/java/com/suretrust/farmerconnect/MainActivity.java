package com.suretrust.farmerconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CircleImageView cr;
    DatabaseReference dbref;
    private Button logoutButton;
    private FirebaseAuth mAuth;
    DrawerLayout drawerLayout;
    FirebaseFirestore st;
    TextView tx,pro;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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



        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new HomeFragment();
        fragmentTransaction.commit();
        fragmentTransaction.replace(R.id.fragment_container, fragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        loadFragment(new HomeFragment());
                        return true;
                    case R.id.menu_fragment:
                        loadFragment(new ScannerFragment());
                        return true;
//                    case R.id.menu_profile:
//                        loadFragment(new ProfileFragment());
//                        return true;
                    default:
                        return false;
                }
            }
        });

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){

            case R.id.settings:
                loadFragment(new SettingsFragment());
                break;
            case R.id.donationhis:
                Intent intent1 = new Intent(MainActivity.this, Donation.class);
                startActivity(intent1);
                break;
            case R.id.help:
                Intent intent2 = new Intent(MainActivity.this, Help.class);
                startActivity(intent2);
                break;
            case R.id.info:
                Intent intent4 = new Intent(MainActivity.this, Information.class);
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

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}