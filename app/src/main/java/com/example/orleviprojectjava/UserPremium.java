package com.example.orleviprojectjava;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;

public class UserPremium extends UserOC {

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    public UserPremium() {
        super();
        mAuth = FirebaseAuth.getInstance();
    }

    public void saveUserDataWithPremium(int numImages, int numLikes, boolean isPremium, OnCompleteListener<Void> listener) {
        String userId = mAuth.getCurrentUser().getUid();  // מזהה המשתמש
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        PremiumUser premiumUser = new PremiumUser(numImages, numLikes, isPremium);

        userRef.setValue(premiumUser).addOnCompleteListener(listener);
    }

    public void setIsPremium(boolean isPremium, OnCompleteListener<Void> listener) {
        String userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.child("isPremium").setValue(isPremium).addOnCompleteListener(listener);
    }

    public static class PremiumUser extends User {
        private boolean isPremium;

        public PremiumUser(int numImages, int numLikes, boolean isPremium) {
            super(numImages, numLikes);
            this.isPremium = isPremium;
        }

        public boolean isPremium() {
            return isPremium;
        }

        public void setPremium(boolean premium) {
            isPremium = premium;
        }
    }
}