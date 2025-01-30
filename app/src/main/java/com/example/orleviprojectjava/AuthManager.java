package com.example.orleviprojectjava;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

public class AuthManager {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    public AuthManager() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
    }

    public void registerUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String userId = task.getResult().getUser().getUid();

                        // Create a new User object
                        UserOC newUser = new UserOC(userId, email);

                        // Save user data to Firebase Realtime Database
                        usersRef.child(userId).setValue(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    // Data saved successfully
                                })
                                .addOnFailureListener(e -> {
                                    // Handle any errors
                                });
                    }
                    listener.onComplete(task);
                });
    }

    public void loginUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public String getCurrentUserId() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }

    // Method to upgrade a user to premium
    public void upgradeToPremium(String userId, String email) {
        UserPremium premiumUser = new UserPremium(userId, email);
        usersRef.child(userId).setValue(premiumUser);
    }
}