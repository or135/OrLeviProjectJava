package com.example.orleviprojectjava;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class AuthManager {
    private FirebaseAuth auth;

    public AuthManager() {
        auth = FirebaseAuth.getInstance();
    }

    public void registerUser(String email, String password, String username, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Authentication succeeded
                        String userId = auth.getCurrentUser().getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("username", username);
                        user.put("likes", 0);
                        user.put("pictures", 0);

                    }
                });
    }

    public void loginUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public String getCurrentUserId() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        } else {
            return null; // User not authenticated
        }
    }

}
