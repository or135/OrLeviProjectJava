package com.example.orleviprojectjava;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

public class CommentManager {
    private final Context context;
    private final DatabaseReference databaseRef;
    private final AuthManager authManager;
    private final EditText commentEditText;

    public CommentManager(Context context, DatabaseReference databaseRef, AuthManager authManager, EditText commentEditText) {
        this.context = context;
        this.databaseRef = databaseRef;
        this.authManager = authManager;
        this.commentEditText = commentEditText;
    }

    public void checkPremiumAndUploadComment(ImageData currentImage) {
        if (currentImage == null) {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = authManager.getCurrentUserId();

        // dataSnapshot מכיל את המידע של המשתמש המבוקש
        databaseRef.child("users").child(userId).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                // לוקח את הערכים בשביל לבדוק אם פרימיום
                Boolean isPremium = dataSnapshot.child("premium").getValue(Boolean.class);
                Long photoCount = dataSnapshot.child("numberOfPhotos").getValue(Long.class);

                // בודק
                boolean userIsPremium = (isPremium != null && isPremium) || (photoCount != null && photoCount >= 3);

                if (userIsPremium) {
                    uploadNewComment(currentImage); //קורא לפעולה שבאמת מעלה את התגובה

                    // בודק אם צריך לעדכן את הערך פרימיום
                    if ((isPremium == null || !isPremium) && photoCount != null && photoCount >= 3) {
                        dataSnapshot.getRef().child("premium").setValue(true);
                    }
                }
                else {
                    Toast.makeText(context, "You need to upload at least 3 images to become a premium user and comment on photos", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to check premium status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadNewComment(ImageData currentImage) {
        String comment = commentEditText.getText().toString().trim();
        if (comment.isEmpty()) {
            Toast.makeText(context, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseRef.child("images").child(currentImage.getUserId()).child(currentImage.getImageId()).child("lastComment").setValue(comment)

                .addOnSuccessListener(aVoid -> { //aVoid פשוט מתחיל את הפולה לא מכיל מידע
                    Toast.makeText(context, "Comment uploaded successfully", Toast.LENGTH_SHORT).show();
                    commentEditText.setText("");
                })
                .addOnFailureListener(e -> // e מתחיל את הפעולה ומכיל את המידע של השגיאה
                        Toast.makeText(context, "Failed to upload comment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}