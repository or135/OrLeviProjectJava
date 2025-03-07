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

                        // Create a UserPremium object
                        UserPremium newUser = new UserPremium(userId, email);

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

    public void getUserData(String userId, ResultCallback<UserPremium> callback) {
        usersRef.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                UserPremium user = task.getResult().getValue(UserPremium.class);
                callback.onResult(user);
            } else {
                callback.onResult(null);
            }
        });
    }

    public void updateUserPhotos(String userId, int numberOfPhotos, ResultCallback<Boolean> callback) {
        usersRef.child(userId).child("numberOfPhotos").setValue(numberOfPhotos)
                .addOnSuccessListener(aVoid -> {
                    // Check if user should be premium now
                    if (numberOfPhotos >= 3) {
                        usersRef.child(userId).child("premium").setValue(true)
                                .addOnSuccessListener(aVoid2 -> callback.onResult(true))
                                .addOnFailureListener(e -> callback.onResult(false));
                    } else {
                        callback.onResult(true);
                    }
                })
                .addOnFailureListener(e -> callback.onResult(false));
    }

    // Interface for async callbacks
    public interface ResultCallback<T> {
        void onResult(T result);
    }
}