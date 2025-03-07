package com.example.orleviprojectjava;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private ImageView imageViewGallery;
    private TextView userNameImage;
    private TextView lastComment;
    private EditText newComment;
    private ImageView nextImage;
    private ImageView uploadComment;
    private DatabaseReference databaseRef;
    private List<ImageData> imageList;
    private int currentImageIndex = 0;
    private AuthManager authManager;
    private CommentManager commentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        imageViewGallery = findViewById(R.id.imageViewGallery);
        userNameImage = findViewById(R.id.UserNameImage);
        lastComment = findViewById(R.id.LastComment);
        newComment = findViewById(R.id.NewComment);
        nextImage = findViewById(R.id.NextImage);
        uploadComment = findViewById(R.id.UploadComment);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        authManager = new AuthManager();
        imageList = new ArrayList<>();

        // Initialize CommentManager
        commentManager = new CommentManager(this, databaseRef, authManager, newComment);

        loadImages();

        nextImage.setOnClickListener(v -> showNextImage());
        uploadComment.setOnClickListener(v -> {
            if (!imageList.isEmpty()) {
                ImageData currentImage = imageList.get(currentImageIndex);
                commentManager.checkPremiumAndUploadComment(currentImage);
            } else {
                Toast.makeText(this, "No images available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImages() {
        databaseRef.child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageList.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot imageSnapshot : userSnapshot.getChildren()) {
                        String imageId = imageSnapshot.child("imageId").getValue(String.class);
                        String userId = imageSnapshot.child("userId").getValue(String.class);
                        String imageBase64 = imageSnapshot.child("imageData").getValue(String.class);
                        String lastCommentText = imageSnapshot.child("lastComment").getValue(String.class);

                        databaseRef.child("users").child(userId).get().addOnSuccessListener(userDataSnapshot -> {
                            String userEmail = userDataSnapshot.child("email").getValue(String.class);
                            imageList.add(new ImageData(imageId, userId, imageBase64, userEmail, lastCommentText));

                            if (imageList.size() == 1) {
                                showCurrentImage();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GalleryActivity.this, "Failed to load images: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCurrentImage() {
        if (imageList.isEmpty()) {
            Toast.makeText(this, "No images available", Toast.LENGTH_SHORT).show();
            return;
        }

        ImageData currentImage = imageList.get(currentImageIndex);

        byte[] decodedString = Base64.decode(currentImage.getImageBase64(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageViewGallery.setImageBitmap(decodedByte);

        userNameImage.setText(currentImage.getUserEmail());

        if (currentImage.getLastCommentText() != null) {
            lastComment.setText(currentImage.getLastCommentText());
        } else {
            lastComment.setText("No comments yet");
        }
    }

    private void showNextImage() {
        if (imageList.isEmpty()) return;

        currentImageIndex = (currentImageIndex + 1) % imageList.size();
        showCurrentImage();
    }

    public void ReturnFG(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}