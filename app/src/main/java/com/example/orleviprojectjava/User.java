package com.example.orleviprojectjava;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    public User(Context context) {
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
    }

    // פעולה ליצירת משתמש חדש, וכתיבת המידע המקומי
    public void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String username = generateUsernameFromEmail(user.getEmail());

                            // שמירה של לייקים, תמונות וסטטוס פרימיום ב-SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(username + "_likes", 0);  // לייקים
                            editor.putInt(username + "_pictures", 0);  // תמונות
                            editor.putBoolean(username + "_isPremium", false);  // פרימיום
                            editor.apply();

                            // הצגת הודעת הצלחה
                            System.out.println("User created successfully with username: " + username);
                        }
                    } else {
                        System.out.println("Registration failed: " + task.getException().getMessage());
                    }
                });
    }

    // פעולה לעדכון לייקים
    public void setLikes(String username, int newLikes) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(username + "_likes", newLikes);  // שמירה של הלייקים החדשים
        editor.apply();

        System.out.println("Likes set to: " + newLikes);
    }

    // פעולה לעדכון תמונות
    public void setPictures(String username, int newPictures) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(username + "_pictures", newPictures);  // שמירה של התמונות החדשות
        editor.apply();

        System.out.println("Pictures set to: " + newPictures);
    }

    // פעולה לעדכון סטטוס פרימיום
    public void setPremiumStatus(String username, boolean isPremium) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(username + "_isPremium", isPremium);  // שמירה של סטטוס הפרימיום
        editor.apply();

        System.out.println("Premium status set to: " + isPremium);
    }

    // פונקציה ליצירת שם משתמש מג'ימייל
    private String generateUsernameFromEmail(String email) {
        // כאן אנחנו לוקחים את החלק של המייל לפני ה-"@"
        String username = email.split("@")[0];
        return username;
    }

    // פונקציה לשלוף את הלייקים
    public int getLikes(String username) {
        return sharedPreferences.getInt(username + "_likes", 0);
    }

    // פונקציה לשלוף את התמונות
    public int getPictures(String username) {
        return sharedPreferences.getInt(username + "_pictures", 0);
    }

    // פונקציה לשלוף את סטטוס הפרימיום
    public boolean isPremium(String username) {
        return sharedPreferences.getBoolean(username + "_isPremium", false);
    }
}
