package com.example.orleviprojectjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView UserNameProfile, NumImages, NumLikes;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserNameProfile = findViewById(R.id.UserNameProfile);
        NumImages = findViewById(R.id.NumImages);
        NumLikes = findViewById(R.id.NumLikes);

        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            UserNameProfile.setText(currentUser.getEmail());

            mDatabase.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        Long numImages = dataSnapshot.child("numImages").getValue(Long.class);
                        Long numLikes = dataSnapshot.child("numLikes").getValue(Long.class);

                        NumImages.setText(String.valueOf(numImages != null ? numImages : 0));
                        NumLikes.setText(String.valueOf(numLikes != null ? numLikes : 0));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void ReturnFP(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}