package com.example.orleviprojectjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class GalleryActivity extends ReturnActivity {
    private ImageView imageViewGallery, nextImage, uploadComment;
    private TextView userNameImage, lastComment, NumLikesG;
    private EditText newComment;
    private ImageButton imageLike;
    private DatabaseReference databaseRef;
    private List<ImageData> imageList;
    private int currentImageIndex = 0;
    private AuthManager authManager;
    private CommentManager commentManager;
    private String currentImageId = null;
    private boolean isLoadingImages = false;

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
        imageLike = findViewById(R.id.imageLike);
        NumLikesG = findViewById(R.id.NumLikesG);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        authManager = new AuthManager();
        imageList = new ArrayList<>();
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

        imageLike.setOnClickListener(v -> {
            if (!imageList.isEmpty()) {
                addLike();
            } else {
                Toast.makeText(this, "No images available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImages() {
        databaseRef.child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isLoadingImages = true;

                // שמירת ID התמונה הנוכחית לפני הניקוי
                if (!imageList.isEmpty() && currentImageIndex < imageList.size()) {
                    currentImageId = imageList.get(currentImageIndex).getImageId();
                }

                imageList.clear();
                List<ImageData> tempImageList = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot imageSnapshot : userSnapshot.getChildren()) {
                        String imageId = imageSnapshot.child("imageId").getValue(String.class);
                        String userId = imageSnapshot.child("userId").getValue(String.class);
                        String imageBase64 = imageSnapshot.child("imageData").getValue(String.class);
                        String lastCommentText = imageSnapshot.child("lastComment").getValue(String.class);
                        Long likes = imageSnapshot.child("likes").getValue(Long.class);

                        databaseRef.child("users").child(userId).get().addOnSuccessListener(userDataSnapshot -> {
                            String userEmail = userDataSnapshot.child("email").getValue(String.class);
                            ImageData imageData = new ImageData(imageId, userId, imageBase64, userEmail, lastCommentText);
                            if (likes != null) {
                                imageData.setLikes(likes);
                            }

                            synchronized(tempImageList) {
                                tempImageList.add(imageData);

                                // כאשר כל התמונות נטענו
                                if (tempImageList.size() == getTotalImageCount(dataSnapshot)) {
                                    imageList.addAll(tempImageList);

                                    // איתור המיקום של התמונה הנוכחית אחרי הטעינה
                                    if (currentImageId != null) {
                                        for (int i = 0; i < imageList.size(); i++) {
                                            if (imageList.get(i).getImageId().equals(currentImageId)) {
                                                currentImageIndex = i;
                                                break;
                                            }
                                        }
                                    } else {
                                        currentImageIndex = 0;
                                    }

                                    // בדיקה שהאינדקס תקין
                                    if (currentImageIndex >= imageList.size()) {
                                        currentImageIndex = 0;
                                    }

                                    isLoadingImages = false;
                                    showCurrentImage();
                                }
                            }
                        });
                    }
                }

                // אם אין תמונות כלל
                if (!dataSnapshot.hasChildren()) {
                    currentImageIndex = 0;
                    currentImageId = null;
                    isLoadingImages = false;
                    Toast.makeText(GalleryActivity.this, "No images available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                isLoadingImages = false;
                Toast.makeText(GalleryActivity.this, "Failed to load images: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // פונקציה עזר לספירת כמות התמונות הכוללת
    private int getTotalImageCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
            for (DataSnapshot imageSnapshot : userSnapshot.getChildren()) {
                count++;
            }
        }
        return count;
    }

    private void showCurrentImage() {
        if (imageList.isEmpty()) {
            Toast.makeText(this, "No images available", Toast.LENGTH_SHORT).show();
            return;
        }

        // בדיקה נוספת שהאינדקס תקין
        if (currentImageIndex >= imageList.size()) {
            currentImageIndex = 0;
        }

        ImageData currentImage = imageList.get(currentImageIndex);
        currentImageId = currentImage.getImageId(); // עדכון ה-ID הנוכחי

        byte[] decodedString = Base64.decode(currentImage.getImageBase64(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageViewGallery.setImageBitmap(decodedByte);

        userNameImage.setText(currentImage.getUserEmail());
        if (currentImage.getLastCommentText() != null) {
            lastComment.setText(currentImage.getLastCommentText());
        } else {
            lastComment.setText("No comments yet");
        }
        NumLikesG.setText(String.valueOf(currentImage.getLikes()));
    }

    private void showNextImage() {
        if (imageList.isEmpty()) return;
        currentImageIndex = (currentImageIndex + 1) % imageList.size();
        showCurrentImage();
    }

    private void addLike() {
        // מניעת הוספת לייק במהלך טעינת תמונות
        if (isLoadingImages) {
            return;
        }

        // בדיקה נוספת שהאינדקס תקין לפני הגישה
        if (currentImageIndex >= imageList.size()) {
            currentImageIndex = 0;
        }

        ImageData currentImage = imageList.get(currentImageIndex);
        String imageId = currentImage.getImageId();
        String imageOwnerId = currentImage.getUserId();

        // עדכון מיידי של הממשק המשתמש
        long newLikes = currentImage.getLikes() + 1;
        currentImage.setLikes(newLikes);
        NumLikesG.setText(String.valueOf(newLikes));

        // עדכון בבסיס הנתונים
        DatabaseReference imageRef = databaseRef.child("images").child(imageOwnerId).child(imageId).child("likes");
        imageRef.setValue(newLikes).addOnSuccessListener(aVoid -> {
            // עדכון מספר הלייקים של בעל התמונה
            updateUserLikes(imageOwnerId);
        }).addOnFailureListener(e -> {
            // במקרה של שגיאה, החזרת הערך הקודם
            currentImage.setLikes(newLikes - 1);
            NumLikesG.setText(String.valueOf(newLikes - 1));
            Toast.makeText(this, "Failed to add like: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUserLikes(String userId) {
        DatabaseReference userRef = databaseRef.child("users").child(userId).child("numberOfLikes");
        userRef.get().addOnSuccessListener(dataSnapshot -> {
            long currentLikes = 0;
            if (dataSnapshot.exists()) {
                Long value = dataSnapshot.getValue(Long.class);
                if (value != null) {
                    currentLikes = value;
                }
            }
            userRef.setValue(currentLikes + 1);
        });
    }
}