package com.example.orleviprojectjava;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;

public class UserOC {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    public UserOC() {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void saveUserData(int numImages, int numLikes, OnCompleteListener<Void> listener) {
        String userId = mAuth.getCurrentUser().getUid();  // מזהה המשתמש
        userRef = database.getReference("users").child(userId);

        User user = new User(numImages, numLikes);

        userRef.setValue(user).addOnCompleteListener(listener);
    }

    public void setNumImages(int numImages, OnCompleteListener<Void> listener) {
        String userId = mAuth.getCurrentUser().getUid();
        userRef = database.getReference("users").child(userId);

        userRef.child("numImages").setValue(numImages).addOnCompleteListener(listener);
    }

    public void setNumLikes(int numLikes, OnCompleteListener<Void> listener) {
        String userId = mAuth.getCurrentUser().getUid();
        userRef = database.getReference("users").child(userId);

        userRef.child("numLikes").setValue(numLikes).addOnCompleteListener(listener);
    }

    public void getUserData(ValueEventListener listener) {
        String userId = mAuth.getCurrentUser().getUid();
        userRef = database.getReference("users").child(userId);

        userRef.addValueEventListener(listener);
    }

    public static class User {
        private int numImages;
        private int numLikes;

        public User(int numImages, int numLikes) {
            this.numImages = numImages;
            this.numLikes = numLikes;
        }

        public int getNumImages() {
            return numImages;
        }

        public void setNumImages(int numImages) {
            this.numImages = numImages;
        }

        public int getNumLikes() {
            return numLikes;
        }

        public void setNumLikes(int numLikes) {
            this.numLikes = numLikes;
        }
    }
}
