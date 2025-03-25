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

    public AuthManager() { // מאתחל ומגדיר הפניה לענף
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
    }

    //listener – אובייקט שאחראי לטפל בתוצאה של הפעולה (למשל, כדי לדעת אם ההרשמה הצליחה או נכשלה)
    public void registerUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String userId = task.getResult().getUser().getUid();

                        Map<String, Object> newUser = new HashMap<>(); //newUser מגירה של
                        newUser.put("userId", userId);
                        newUser.put("email", email);
                        newUser.put("numberOfLikes", 0);
                        newUser.put("numberOfPhotos", 0);
                        newUser.put("premium", false);

                        usersRef.child(userId).setValue(newUser);
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
}