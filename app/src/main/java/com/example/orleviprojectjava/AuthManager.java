package com.example.orleviprojectjava;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

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

                        // Create a user map with the necessary fields
                        // We're using a simple map instead of UserOC object to avoid serialization issues
                        // and to make sure all necessary fields are included
                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("userId", userId);
                        newUser.put("email", email);
                        newUser.put("numberOfLikes", 0);
                        newUser.put("numberOfPhotos", 0);
                        newUser.put("premium", false);

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

    // Check if the current user is premium
    public void checkIfUserIsPremium(ResultCallback<Boolean> callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onResult(false);
            return;
        }

        usersRef.child(userId).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                Boolean isPremium = dataSnapshot.child("premium").getValue(Boolean.class);
                Long photoCount = dataSnapshot.child("numberOfPhotos").getValue(Long.class);

                // User is premium if they have the premium flag or have 3+ photos
                boolean userIsPremium = (isPremium != null && isPremium) ||
                        (photoCount != null && photoCount >= 3);

                callback.onResult(userIsPremium);
            } else {
                callback.onResult(false);
            }
        }).addOnFailureListener(e -> callback.onResult(false));
    }

    // Interface for async callbacks
    public interface ResultCallback<T> {
        void onResult(T result);
    }
}