package com.suretrust.farmerconnect;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private TextView logoutButton;
    private TextView settings;
    ImageButton img;
    CircleImageView cr;
    DatabaseReference dbref;
    String ph;
    Uri imguri;
    String myuri="";
    StorageTask uplo;
    StorageReference stref;
    Button edit;
    // Firebase authentication instance
    FirebaseAuth mAuth;
    FirebaseFirestore st;
    String uid;
    TextView tx,sv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        dbref= FirebaseDatabase.getInstance().getReference().child("user");
        stref= FirebaseStorage.getInstance().getReference().child("Profile pic");

        

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tx=view.findViewById(R.id.protxt);
        logoutButton = view.findViewById(R.id.logout_btn);
        edit=view.findViewById(R.id.btnEditProfile);
        sv=view.findViewById(R.id.save);
        img=view.findViewById(R.id.btnCamera);
        cr=view.findViewById(R.id.proimg);
        settings = view.findViewById(R.id.txtSettingsOne);

        mAuth = FirebaseAuth.getInstance();
        st=FirebaseFirestore.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        DocumentReference dr=st.collection("users").document(uid);
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                tx.setText(documentSnapshot.getString("name"));
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the logout function
                logout();
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         imguri=data.getData();
        ph=imguri.getPath();

        cr.setImageURI(imguri);
        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setVisibility(View.VISIBLE);
                sv.setVisibility(View.GONE);
                img.setVisibility(View.GONE);

            }
        });


    }



    private void logout() {
        // Sign out the user from Firebase Auth
        mAuth.signOut();

        // Sign out the user from Google Sign-In (if used)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignIn.getClient(getActivity(), gso).signOut();

        // Redirect the user to the login screen or perform any other required actions
        // For example, you can start a new activity or show a toast message
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish(); // Optional: Finish the current activity to prevent going back
        }
    }

}
