package com.example.orleviprojectjava;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends ReturnActivity {

    private TextView UserNameProfile, NumImages, NumLikes, premiumStatusText;
    private ImageView premiumBadge;
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
        premiumStatusText = findViewById(R.id.premiumStatusText);
        premiumBadge = findViewById(R.id.premiumBadge);

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
                        Long numPhotos = dataSnapshot.child("numberOfPhotos").getValue(Long.class);
                        Long numLikes = dataSnapshot.child("numberOfLikes").getValue(Long.class);
                        Boolean isPremium = dataSnapshot.child("premium").getValue(Boolean.class);

                        NumImages.setText("Photos: " + String.valueOf(numPhotos));
                        NumLikes.setText("Likes: " + String.valueOf(numLikes));

                        boolean userIsPremium = (isPremium != null && isPremium) || (numPhotos != null && numPhotos >= 3);

                        if (userIsPremium) {
                            premiumStatusText.setText("Premium User");
                            premiumBadge.setVisibility(View.VISIBLE);

                            if (isPremium == null || !isPremium) {dataSnapshot.getRef().child("premium").setValue(true);}
                        }
                        else
                        {
                            int photosNeeded = 3 - (numPhotos.intValue());
                            premiumStatusText.setText("Regular User                                (" + photosNeeded + " more photos for premium)");
                            premiumBadge.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}